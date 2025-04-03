import android.os.Handler
import android.os.Looper
import android.util.Log
import at.aau.serg.websocketbrokerdemo.Callbacks
import at.aau.serg.websocketbrokerdemo.dkt.DktClientHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import org.json.JSONObject
import at.aau.serg.websocketbrokerdemo.dkt.GameMessage
import com.google.gson.Gson


private  val WEBSOCKET_URI = "ws://10.0.2.2:8080/websocket-example-broker";
class MyStomp(private val dktHandler: DktClientHandler) {

    private lateinit var session: StompSession
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun connect() {
        val client = StompClient(OkHttpWebSocketClient())

        scope.launch {
            session = client.connect(WEBSOCKET_URI)

            // DKT Topic abonnieren
            val dktFlow: Flow<String> = session.subscribeText("/topic/dkt")
            launch {
                dktFlow.collect { msg ->
                    val gameMessage = Gson().fromJson(msg, GameMessage::class.java)
                    dktHandler.handle(gameMessage)
                }
            }


            Log.i("MyStomp", "Verbindung aufgebaut!")
        }
    }

    fun sendHello() {
        scope.launch {
            session.sendText("/app/hello", "message from client")
        }
    }

    fun sendJson() {
        val json = JSONObject()
        json.put("from", "client")
        json.put("text", "from client")

        scope.launch {
            session.sendText("/app/object", json.toString())
        }
    }

    fun sendGameMessage(message: GameMessage) {
        val json = Gson().toJson(message)
        scope.launch {
            session.sendText("/app/dkt", json)
        }
    }
}