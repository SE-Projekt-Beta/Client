package at.aau.serg.websocketbrokerdemo.network

import WEBSOCKET_URI
import android.util.Log
import at.aau.serg.websocketbrokerdemo.game.GameClientHandler
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import com.google.gson.GsonBuilder

/**
 * Handles game‐level STOMP messaging for a specific lobby.
 */
class GameStomp(
    private val dktHandler: GameClientHandler,
    private val lobbyId: Int,
    private val onConnected: (() -> Unit)? = null
) {
    private lateinit var session: StompSession
    private val scope = CoroutineScope(Dispatchers.IO)
    private val gson = GsonBuilder().create()

    fun connect() {
        val client = StompClient(OkHttpWebSocketClient())
        scope.launch {
            session = client.connect(WEBSOCKET_URI)

            // Only receive messages for this lobby
            launch {
                session.subscribeText("/topic/dkt/$lobbyId").collect { raw ->
                    Log.i("GameStomp", "≪ /topic/dkt/$lobbyId: $raw")
                    val gm = gson.fromJson(raw, GameMessage::class.java)
                    dktHandler.handle(gm)
                }
            }

            Log.i("GameStomp", "Connected to game lobby $lobbyId")
            onConnected?.invoke()
        }
    }

    fun sendGameMessage(message: GameMessage) {
        val json = gson.toJson(message)
        scope.launch {
            session.sendText("/app/dkt/$lobbyId", json)
            Log.i("GameStomp", "≫ /app/dkt/$lobbyId: $json")
        }
    }
}