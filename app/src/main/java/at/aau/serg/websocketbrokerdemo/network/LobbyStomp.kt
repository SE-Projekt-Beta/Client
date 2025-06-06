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

const val APP_LOBBY = "/app/lobby"

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

    fun sendCreateUser(username: String) {
        val payload = JsonObject().apply {
            addProperty("username", username);
        }
        val msg = LobbyMessage(null, LobbyMessageType.CREATE_USER, payload)
        scope.launch { session.sendText(APP_LOBBY, Gson().toJson(msg)) }
    }

    fun sendCreateLobby(lobbyName: String) {
        val payload = JsonObject().apply { addProperty("lobbyName", lobbyName) }
        val msg = LobbyMessage(null, LobbyMessageType.CREATE_LOBBY, payload)
        scope.launch { session.sendText(APP_LOBBY, Gson().toJson(msg)) }
    }

    fun sendListLobbies() {
        val msg = LobbyMessage(null, LobbyMessageType.LIST_LOBBIES, JsonObject())
        scope.launch { session.sendText(APP_LOBBY, Gson().toJson(msg)) }
    }

    fun sendJoinLobby(playerId: Int, lobbyId: Int) {
        val payload = JsonObject().apply {
            addProperty("playerId", playerId)
            addProperty("lobbyId", lobbyId)
        }
        val msg = LobbyMessage(lobbyId, LobbyMessageType.JOIN_LOBBY, payload)
        scope.launch {
            session.sendText(APP_LOBBY, Gson().toJson(msg))
            Log.i("LobbyStomp", "Sent JOIN_LOBBY for user=$playerId, lobbyId=$lobbyId")
        }
    }

    fun sendStartGame() {
        val id = LobbyClient.lobbyId
        val payload = JsonObject().apply { addProperty("lobbyId", id) }
        val msg = LobbyMessage(id, LobbyMessageType.START_GAME, payload)
        scope.launch { session.sendText(APP_LOBBY, Gson().toJson(msg)) }
    }

    private fun parseLobbyMessage(json: String): LobbyMessage =
        Gson().fromJson(json, LobbyMessage::class.java)

    private fun handleLobbyMessage(message: LobbyMessage) {

        // check lobbyid
        if (message.lobbyId != 0 && message.lobbyId != LobbyClient.lobbyId) {
            Log.w("LobbyStomp", "Received message for different lobby: ${message.lobbyId}")
            return
        }

        when (message.type) {
            LobbyMessageType.USER_CREATED -> {
                // check if the playerid has already been set
                if (LobbyClient.playerId != -1) {
                    Log.w("LobbyStomp", "Player ID already set: ${LobbyClient.playerId}")
                    return
                }

                val playerId = message.payload.asJsonObject["playerId"].asInt
                val username = message.payload.asJsonObject["username"].asString

                // check if the nickname is the same as the one in the client
                if (username != LobbyClient.username) {
                    Log.w("LobbyStomp", "Nickname mismatch: $username != ${LobbyClient.username}")
                    return
                }


                LobbyClient.playerId = playerId
                Log.i("LobbyStomp", "Player ID set to $playerId")
            }
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
                        id = o["lobbyId"].asInt,
                        name = o["lobbyName"].asString,
                        playerCount = o["playerCount"].asInt
                    )
                }
                listener.onLobbyListUpdate(lobbies)
            }
            LobbyMessageType.LOBBY_UPDATE -> {
                val playersJson = message.payload.asJsonObject.getAsJsonArray("players")
                val players = playersJson.map {
                    val o = it.asJsonObject
                    PlayerDTO(o["id"].asInt, o["nickname"].asString)
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