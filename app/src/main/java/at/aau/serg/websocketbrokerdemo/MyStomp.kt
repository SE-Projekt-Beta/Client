import android.util.Log
import at.aau.serg.websocketbrokerdemo.dkt.DktClientHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import at.aau.serg.websocketbrokerdemo.network.GameMessage
import at.aau.serg.websocketbrokerdemo.lobby.LobbyHandler
import at.aau.serg.websocketbrokerdemo.network.MessageType
import com.google.gson.GsonBuilder



private  val WEBSOCKET_URI = "ws://10.0.2.2:8080/websocket-example-broker";
class MyStomp(
    private val dktHandler: DktClientHandler? = null,
    private val lobbyHandler: LobbyHandler? = null
) {
    private lateinit var session: StompSession
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val gson = GsonBuilder().create()

    fun connect() {
        val client = StompClient(OkHttpWebSocketClient())
        scope.launch {
            session = client.connect(WEBSOCKET_URI)

            val dktFlow: Flow<String> = session.subscribeText("/topic/dkt")
            launch {
                dktFlow.collect { msg ->
                    val gameMessage = gson.fromJson(msg, GameMessage::class.java)

                    if (gameMessage.type == MessageType.LOBBY_UPDATE || gameMessage.type == MessageType.START_GAME) {
                        lobbyHandler?.handle(gameMessage)
                    } else {
                        dktHandler?.handle(gameMessage)
                    }
                }
            }
            Log.i("MyStomp", "Verbindung aufgebaut!")
        }
    }

    fun sendGameMessage(message: GameMessage) {
        val json = gson.toJson(message)
        scope.launch {
            session.sendText("/app/dkt", json)
        }
    }
}
