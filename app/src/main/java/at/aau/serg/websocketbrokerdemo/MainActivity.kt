package at.aau.serg.websocketbrokerdemo

import MyStomp
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import at.aau.serg.websocketbrokerdemo.dkt.DktClientHandler
import at.aau.serg.websocketbrokerdemo.dkt.GameMessage
import com.example.myapplication.R
import org.json.JSONObject


class MainActivity : ComponentActivity() {

    private lateinit var mystomp: MyStomp
    private lateinit var clientHandler: DktClientHandler

    lateinit var response: TextView
    lateinit var ownershipView: TextView
    lateinit var lobbyView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fragment_fullscreen)

        // Views
        response = findViewById(R.id.response_view)
        ownershipView = findViewById(R.id.ownership_view)
        lobbyView = findViewById(R.id.lobby_view)

        // Netzwerk
        clientHandler = DktClientHandler(this)
        mystomp = MyStomp(clientHandler)
        mystomp.connect()

        // Buttons
        findViewById<Button>(R.id.rollDiceBtn).setOnClickListener {
            val payload = JSONObject()
            payload.put("playerId", "player1")
            mystomp.sendGameMessage(GameMessage("roll_dice", payload.toString()))
        }

        findViewById<Button>(R.id.buybtn).setOnClickListener {
            // Kaufen wird dynamisch angezeigt, daher kein direkter Code hier nötig
        }
    }

    fun showResponse(msg: String) {
        runOnUiThread {
            response.text = msg
        }
    }

    fun showOwnership() {
        runOnUiThread {
            val sb = StringBuilder()
            for ((player, props) in at.aau.serg.websocketbrokerdemo.dkt.OwnershipClient.all()) {
                sb.append("$player besitzt:\n  - ${props.joinToString("\n  - ")}\n\n")
            }
            ownershipView.text = sb.toString()
        }
    }

    fun updateLobby(players: List<String>) {
        runOnUiThread {
            lobbyView.text = "Lobby:\n" + players.joinToString("\n") { "- $it" }
        }
    }

    fun showBuyButton(tileName: String, tilePos: Int, playerId: String) {
        runOnUiThread {
            val button = findViewById<Button>(R.id.buybtn)
            button.text = "Kaufen: $tileName"
            button.visibility = View.VISIBLE

            button.setOnClickListener {
                val payload = JSONObject()
                payload.put("playerId", playerId)
                payload.put("tilePos", tilePos)
                mystomp.sendGameMessage(GameMessage("buy_property", payload.toString()))
                button.visibility = View.GONE
            }
        }
    }


    fun showEventCard(cardText: String) {
        runOnUiThread {
            android.app.AlertDialog.Builder(this)
                .setTitle("Ereigniskarte")
                .setMessage(cardText)
                .setPositiveButton("OK", null)
                .show()
        }
    }
}

