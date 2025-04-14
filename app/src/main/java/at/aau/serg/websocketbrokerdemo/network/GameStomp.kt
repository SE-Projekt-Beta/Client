package at.aau.serg.websocketbrokerdemo.network

import WEBSOCKET_URI
import android.util.Log
import at.aau.serg.websocketbrokerdemo.dkt.DktClientHandler
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
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

class GameStomp(
    private val dktHandler: DktClientHandler,
    private val onConnected: (() -> Unit)? = null
) {
    private lateinit var session: StompSession
    private val scope = CoroutineScope(Dispatchers.IO)
    private val gson = GsonBuilder().create()

    fun connect() {
        val client = StompClient(OkHttpWebSocketClient())
        scope.launch {
            session = client.connect(WEBSOCKET_URI)

            val gameFlow: Flow<String> = session.subscribeText("/topic/dkt")
            launch {
                gameFlow.collect { msg ->
                    Log.i("GameStomp", "Nachricht vom Server: $msg")
                    val gameMessage = gson.fromJson(msg, GameMessage::class.java)
                    dktHandler.handle(gameMessage)
                }
            }
            Log.i("GameStomp", "Game-Verbindung aufgebaut")
            onConnected?.invoke()
        }
    }

    fun sendGameMessage(message: GameMessage) {
        val json = gson.toJson(message)
        scope.launch {
            session.sendText("/app/dkt", json)
        }
    }
}