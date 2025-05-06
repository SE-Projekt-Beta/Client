// src/main/java/at/aau/serg/websocketbrokerdemo/network/LobbyStomp.kt
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
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

class LobbyStomp(private val listener: LobbyMessageListener) {

    // ↓ NEW: one-time callback used by ListLobby to know when to send LIST_LOBBIES
    private var onConnected: (() -> Unit)? = null

    /** Register a callback to fire right after the WebSocket opens. */
    fun setOnConnectedListener(cb: () -> Unit) {
        onConnected = cb
    }

    private lateinit var session: StompSession
    private val scope = CoroutineScope(Dispatchers.IO)

    fun connect() {
        val client = StompClient(OkHttpWebSocketClient())
        scope.launch {
            session = client.connect(WEBSOCKET_URI)

            // Socket is open—invoke the one-time listener
            onConnected?.invoke()

            // Subscribe to the global lobby list topic
            session.subscribeText("/topic/lobby").collect { raw ->
                Log.i("LobbyStomp", "Received from /topic/lobby: $raw")
                handleLobbyMessage(parseLobbyMessage(raw))
            }

            Log.i("LobbyStomp", "Connected to WebSocket. Waiting for lobby updates.")
        }
    }

    fun sendCreateLobby(lobbyName: String) {
        val payload = JsonObject().apply { addProperty("lobbyName", lobbyName) }
        val msg = LobbyMessage(null, LobbyMessageType.CREATE_LOBBY, payload)
        scope.launch { session.sendText("/app/lobby", Gson().toJson(msg)) }
    }

    fun sendListLobbies() {
        val msg = LobbyMessage(null, LobbyMessageType.LIST_LOBBIES, JsonObject())
        scope.launch { session.sendText("/app/lobby", Gson().toJson(msg)) }
    }

    fun sendJoinLobby(lobbyId: String, username: String) {
        val payload = JsonObject().apply {
            addProperty("username", username)
            addProperty("lobbyId", lobbyId)
        }
        val msg = LobbyMessage(lobbyId, LobbyMessageType.JOIN_LOBBY, payload)
        scope.launch {
            session.sendText("/app/lobby", Gson().toJson(msg))
            Log.i("LobbyStomp", "Sent JOIN_LOBBY for user=$username, lobbyId=$lobbyId")
        }
    }

    fun sendStartGame() {
        val id = LobbyClient.lobbyId
        val payload = JsonObject().apply { addProperty("lobbyId", id) }
        val msg = LobbyMessage(id, LobbyMessageType.START_GAME, payload)
        scope.launch { session.sendText("/app/lobby", Gson().toJson(msg)) }
    }

    private fun parseLobbyMessage(json: String): LobbyMessage =
        Gson().fromJson(json, LobbyMessage::class.java)

    private fun handleLobbyMessage(message: LobbyMessage) {
        when (message.type) {
            LobbyMessageType.LOBBY_CREATED -> {
                val lobbyid = message.lobbyId
                LobbyClient.lobbyId = lobbyid
                listener.onLobbyListUpdate(listOf()) // or trigger a fetch
            }
            LobbyMessageType.LOBBY_LIST -> {
                val arr = message.payload.asJsonArray
                val lobbies = arr.map { elem ->
                    val o = elem.asJsonObject
                    LobbyDTO(
                        id = o.get("lobbyId").asString,
                        name = o.get("lobbyName").asString,
                        playerCount = o.get("playerCount").asInt
                    )
                }
                listener.onLobbyListUpdate(lobbies)
            }
            LobbyMessageType.LOBBY_UPDATE -> {
                val playersJson = message.payload.asJsonObject.getAsJsonArray("players")
                val players = playersJson.map {
                    val o = it.asJsonObject
                    PlayerDTO(o.get("id").asInt, o.get("nickname").asString)
                }
                listener.onLobbyUpdate(players)
            }
            LobbyMessageType.START_GAME -> {
                listener.onStartGame(message.payload.asJsonObject)
            }
            LobbyMessageType.ERROR -> {
                Log.e("LobbyStomp", "Server error: ${message.payload}")
            }
            else -> Log.w("LobbyStomp", "Unhandled message type: ${message.type}")
        }
    }
}
