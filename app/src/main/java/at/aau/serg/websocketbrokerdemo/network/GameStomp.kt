package at.aau.serg.websocketbrokerdemo.network

import WEBSOCKET_URI
import android.util.Log
import at.aau.serg.websocketbrokerdemo.game.GameClientHandler
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

class GameStomp(
    private val dktHandler: GameClientHandler,
    private val lobbyId: Int,
    private var onConnected: (() -> Unit)? = null
) {

    fun setOnConnectedListener(cb: () -> Unit) {
        onConnected = cb
    }

    private lateinit var session: StompSession
    private val scope = CoroutineScope(Dispatchers.IO)
    private val gson = GsonBuilder().create()

    fun connect() {
        val client = StompClient(OkHttpWebSocketClient())
        scope.launch {
            session = client.connect(WEBSOCKET_URI)
            Log.i("GameStomp", "✅ STOMP verbunden für Lobby $lobbyId")

            // 1️⃣: Verbindung steht – Callback an Client übergeben
            onConnected?.invoke()

            // 2️⃣: Jetzt auf Spielnachrichten lauschen
            launch {
                session.subscribeText("/topic/dkt/$lobbyId").collect { raw ->
                    Log.i("GameStomp", "≪ /topic/dkt/$lobbyId: $raw")
                    val gm = gson.fromJson(raw, GameMessage::class.java)
                    // if its a game state message, call the onGameStateReceived method
                    if (gm.type == GameMessageType.GAME_STATE) {
                        onGameStateReceived()
                    }
                    dktHandler.handle(gm)
                }
            }
        }
    }

    var gameStateReceived = false

    fun onGameStateReceived() {
        gameStateReceived = true
        Log.i("GameStomp", "Game state response received")
    }

    fun requestGameStateWithRetry(
        maxRetries: Int = 3,
        timeoutMs: Long = 3000
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            var attempts = 0
            gameStateReceived = false

            while (attempts < maxRetries && !gameStateReceived) {
                Log.i("GameStomp", "Requesting game state attempt ${attempts + 1}")
                requestGameState()

                withTimeoutOrNull(timeoutMs) {
                    while (!gameStateReceived) {
                        delay(100) // Polling interval
                    }
                }

                if (!gameStateReceived) {
                    Log.w("GameStomp", "No response received, retrying...")
                }

                attempts++
            }

            if (!gameStateReceived) {
                Log.e("GameStomp", "Failed to receive game state after $maxRetries attempts")
            }
        }
    }

    fun requestGameState() {
        Log.i("GameStomp", "Requesting game state for lobby $lobbyId")
        val payload = JsonObject().apply {
            addProperty("lobbyId", LobbyClient.lobbyId)
        }
        sendGameMessage(GameMessage(LobbyClient.lobbyId, GameMessageType.REQUEST_GAME_STATE, payload))
    }

    fun sendGameMessage(message: GameMessage) {
        val json = gson.toJson(message)
        scope.launch {
            session.sendText("/app/dkt/$lobbyId", json)
            Log.i("GameStomp", "≫ /app/dkt/$lobbyId: $json")
        }
    }

    companion object
}
