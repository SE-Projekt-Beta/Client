import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import at.aau.serg.websocketbrokerdemo.Callbacks
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

private  val WEBSOCKET_URI = "ws://10.0.2.2:8080/websocket-example-broker";
class MyStomp(val callbacks: Callbacks) {

    private lateinit var topicFlow: Flow<String>
    private lateinit var collector:Job

    private lateinit var jsonFlow: Flow<String>
    private lateinit var jsonCollector:Job

    private lateinit var client:StompClient
    private lateinit var session: StompSession

    private val scope:CoroutineScope=CoroutineScope(Dispatchers.IO)
    fun connect() {

            client = StompClient(OkHttpWebSocketClient()) // other config can be passed in here
            scope.launch {
                session=client.connect(WEBSOCKET_URI)
                topicFlow= session.subscribeText("/topic/hello-response")
                //connect to topic
                collector=scope.launch { topicFlow.collect{
                        msg->
                    //todo logic
                    callback(msg)
                } }

                //connect to topic
                jsonFlow= session.subscribeText("/topic/rcv-object")
                jsonCollector=scope.launch { jsonFlow.collect{
                        msg->
                    var o=JSONObject(msg)
                    callback(o.get("text").toString())
                } }

                //subscription for eventcards
                val eventFlow = session.subscribeText("/topic/dkt")
                val eventCollector = scope.launch { eventFlow.collect { msg -> handleEventCard(msg)}}

                callback("connected")
            }

    }
    private fun callback(msg:String){
        Handler(Looper.getMainLooper()).post{
            callbacks.onResponse(msg)
        }
    }
    fun sendHello(){

        scope.launch {
            Log.e("tag","connecting to topic")

            session.sendText("/app/hello","message from client")
           }
    }
    fun sendJson(){
        var json=JSONObject();
        json.put("from","client")
        json.put("text","from client")
        var o=json.toString()

        scope.launch {
            session.sendText("/app/object",o);
        }

    }

    private fun handleEventCard(msg: String){
        try {
            val gameMessage = JSONObject(msg)
            val type = gameMessage.getString("type")
            val playload = gameMessage.getString("playload")

            when(type){
                "risiko_card" -> showEventCard(playload, "Risiko")
                "bank_card" -> showEventCard(playload, "Bank")
                else -> Log.e("STOMP", "Unbekannter Kartentyp: $type")
            }
        } catch (e: Exception){
            Log.e("STOMP", "Fehler beim Verarbeiten der Ereigniskarte", e)
        }
    }

    private fun showEventCard(message: String, category: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(callbacks as Context, "$category Ereigniskarte: $message", Toast.LENGTH_LONG).show()
        }
    }
}