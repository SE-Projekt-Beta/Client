package at.aau.serg.websocketbrokerdemo.network

import WEBSOCKET_URI
import android.util.Log
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.network.dto.GameStartedPayload
import at.aau.serg.websocketbrokerdemo.network.dto.InitPlayerPayload
import at.aau.serg.websocketbrokerdemo.network.dto.JoinLobbyPayload
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyMessage
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyMessageType
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyUpdatePayload
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

class LobbyStomp(private val listener: LobbyMessageListener) {
    private lateinit var session: StompSession
    private val scope = CoroutineScope(Dispatchers.IO)

    fun connect() {
        val client = StompClient(OkHttpWebSocketClient())
        scope.launch {
            session = client.connect(WEBSOCKET_URI)

            // Erst abonnieren
            session.subscribeText("/topic/lobby")
                .map { jsonText ->
                    // turn the String into your data class
                    Gson().fromJson(jsonText, LobbyMessage::class.java)
                }
                .collect { lobbyMsg ->
                    handleLobbyMessage(lobbyMsg)
                }

            // Sobald verbunden und subscribed: Falls Username vorhanden, JOIN senden
            if (LobbyClient.username.isNotEmpty()) {
                sendJoinLobby(LobbyClient.username)
            }
        }
    }

    fun sendInit(username: String) {
        // use the JoinLobbyPayload class instead of thhe below code
        val payload = InitPlayerPayload(0, username)
        val initMessage = LobbyMessage(LobbyMessageType.PLAYER_INIT, payload)
        scope.launch {
            val json = Gson().toJson(initMessage)
            session.sendText("/app/lobby", json)
            Log.i("LobbyStomp", "Sent JOIN_LOBBY for $username")
        }
    }

    fun sendJoinLobby(username: String) {
        // use the JoinLobbyPayload class instead of thhe below code
        val payload = JoinLobbyPayload(username)
        val joinMessage = LobbyMessage(LobbyMessageType.JOIN_LOBBY, payload)
        scope.launch {
            val json = Gson().toJson(joinMessage)
            session.sendText("/app/lobby", json)
            Log.i("LobbyStomp", "Sent JOIN_LOBBY for $username")
        }
    }

    fun sendStartGame() {
        val startMessage = LobbyMessage(LobbyMessageType.START_GAME, JsonObject())
        scope.launch {
            session.sendText("/app/lobby", Gson().toJson(startMessage))
        }
    }
    private fun handleLobbyMessage(message: LobbyMessage) {
        Log.i("LobbyStomp", "Received message: $message")
        when (message.type) {
            LobbyMessageType.LOBBY_UPDATE -> {
                val payload = message.payload as? LobbyUpdatePayload?: return;

                val players: List<PlayerDTO> = payload.players.map { entry ->
                    PlayerDTO(
                        id       = entry.playerId,
                        nickname = entry.nickname
                    )
                }
                Log.i("LobbyStomp", "Parsed players: $players")
                listener.onLobbyUpdate(players)
            }

            LobbyMessageType.START_GAME -> listener.onStartGame(message.payload as GameStartedPayload)

            else -> Log.w("LobbyStomp", "Unhandled message type: ${message}")
        }
    }

}
