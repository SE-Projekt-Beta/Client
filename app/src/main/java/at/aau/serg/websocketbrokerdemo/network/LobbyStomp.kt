package at.aau.serg.websocketbrokerdemo.network

import WEBSOCKET_URI
import android.util.Log
import at.aau.serg.websocketbrokerdemo.lobby.LobbyHandler
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyMessage
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyMessageType
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

class LobbyStomp(private val listener: LobbyMessageListener) {
    private lateinit var session: StompSession
    private val scope = CoroutineScope(Dispatchers.IO)

    fun connect() {
        val client = StompClient(OkHttpWebSocketClient())
        scope.launch {
            session = client.connect(WEBSOCKET_URI)

            val lobbyFlow: Flow<String> = session.subscribeText("/topic/lobby")
            lobbyFlow.collect { message ->
                Log.i("LobbyStomp", "Received message from server: $message")
                val parsed = parseLobbyMessage(message)
                handleLobbyMessage(parsed)
            }
        }
    }

    private fun parseLobbyMessage(msg: String): LobbyMessage {
        return Gson().fromJson(msg, LobbyMessage::class.java)
    }

    private fun handleLobbyMessage(msg: LobbyMessage) {
        when (msg.type) {
            LobbyMessageType.LOBBY_UPDATE -> {
                val usernames = msg.payload.asJsonObject.getAsJsonArray("usernames")
                    .map { it.asString }
                Log.i("LobbyStomp", "Parsed players: $usernames")
                listener.onLobbyUpdate(usernames)
            }
            LobbyMessageType.START_GAME -> listener.onStartGame()
            else -> {}
        }
    }

    fun sendJoinLobby(username: String) {
        val payload = JsonObject().apply {
            addProperty("username", username)
        }
        val joinMessage = LobbyMessage(LobbyMessageType.JOIN_LOBBY, payload)
        scope.launch {
            session.sendText("/app/lobby", Gson().toJson(joinMessage))
            Log.i("LobbyStomp", "Sent join lobby message for username: $username")
        }
    }

    fun sendStartGame() {
        val startMessage = LobbyMessage(LobbyMessageType.START_GAME, JsonObject())
        scope.launch {
            session.sendText("/app/lobby", Gson().toJson(startMessage))
        }
    }
}
