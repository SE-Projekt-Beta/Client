package at.aau.serg.websocketbrokerdemo.network

import WEBSOCKET_URI
import android.util.Log
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyMessage
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyMessageType
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
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

            // Subscribe to lobby updates
            session.subscribeText("/topic/lobby").collect { message ->
                Log.i("LobbyStomp", "Received from /topic/lobby: $message")
                handleLobbyMessage(parseLobbyMessage(message))
            }

            Log.i("LobbyStomp", "Connected to WebSocket. Waiting for lobby updates.")
        }
    }

    fun sendCreateLobby() {
        val createMessage = LobbyMessage(LobbyMessageType.CREATE_LOBBY, JsonObject())
        scope.launch {
            session.sendText("/app/lobby", Gson().toJson(createMessage))
        }
    }

    fun sendJoinLobby(username: String) {
        val payload = JsonObject().apply {
            addProperty("username", username)
        }
        val joinMessage = LobbyMessage(LobbyMessageType.JOIN_LOBBY, payload)
        scope.launch {
            session.sendText("/app/lobby", Gson().toJson(joinMessage))
            Log.i("LobbyStomp", "Sent JOIN_LOBBY message for username: $username")
        }
    }

    fun sendStartGame() {
        val startMessage = LobbyMessage(LobbyMessageType.START_GAME, JsonObject())
        scope.launch {
            session.sendText("/app/lobby", Gson().toJson(startMessage))
        }
    }

    private fun parseLobbyMessage(json: String): LobbyMessage {
        return Gson().fromJson(json, LobbyMessage::class.java)
    }

//    private fun handleLobbyMessage(message: LobbyMessage) {
//        when (message.type) {
//            LobbyMessageType.LOBBY_UPDATE -> {
//                val playersJson = message.payload.asJsonObject.getAsJsonArray("players")
//                val players = playersJson.map {
//                    val obj = it.asJsonObject
//                    PlayerDTO(
//                        id = obj.get("id").asInt,
//                        nickname = obj.get("nickname").asString
//                    )
//                }
//                Log.i("LobbyStomp", "Parsed players: $players")
//                listener.onLobbyUpdate(players)
//            }
//
//            LobbyMessageType.START_GAME -> listener.onStartGame(message.payload.asJsonObject)
//
//            else -> Log.w("LobbyStomp", "Unhandled message type: ${message.type}")
//        }
//    }

    private fun handleLobbyMessage(message: LobbyMessage) {
        when (message.type) {
            LobbyMessageType.LOBBY_UPDATE -> {
                val lobbiesJson = message.payload.asJsonObject.getAsJsonArray("lobbies")
                val lobbies = lobbiesJson.map {
                    val obj = it.asJsonObject
                    LobbyDTO(
                        id = obj.get("id").asString,
                        name = obj.get("name").asString
                    )
                }
                Log.i("LobbyStomp", "Parsed lobbies: $lobbies")
                listener.onLobbyUpdate(lobbies)
            }

            LobbyMessageType.START_GAME -> listener.onStartGame(message.payload.asJsonObject)

            else -> Log.w("LobbyStomp", "Unhandled message type: ${message.type}")
        }
    }



}