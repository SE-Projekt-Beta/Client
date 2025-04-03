package at.aau.serg.websocketbrokerdemo

import MyStomp
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import at.aau.serg.websocketbrokerdemo.dkt.DktClientHandler
import at.aau.serg.websocketbrokerdemo.dkt.GameMessage
import at.aau.serg.websocketbrokerdemo.dkt.OwnershipClient
import com.example.myapplication.R
import org.json.JSONObject


class MainActivity : ComponentActivity() {

    private lateinit var mystomp: MyStomp
    private lateinit var clientHandler: DktClientHandler
    private lateinit var response: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fragment_fullscreen)

        // Initialisiere Handler & Verbindung
        clientHandler = DktClientHandler(this)
        mystomp = MyStomp(clientHandler)
        mystomp.connect()

        // Würfeln
        findViewById<Button>(R.id.rollDiceBtn).setOnClickListener {
            val payload = JSONObject().apply {
                put("playerId", "player1")
            }
            val msg = GameMessage("roll_dice", payload.toString())
            mystomp.sendGameMessage(msg)
        }

        // Views
        response = findViewById(R.id.response_view)
        findViewById<TextView>(R.id.ownership_view).text = "Noch kein Besitz"
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
    fun showResponse(msg: String) {
        runOnUiThread {
            response.text = msg
        }
    }

    fun showEventCard(text: String) {
        runOnUiThread {
            android.app.AlertDialog.Builder(this)
                .setTitle("📦 Ereigniskarte")
                .setMessage(text)
                .setPositiveButton("OK", null)
                .show()

        }
    }
    fun showOwnership() {
        val ownershipTextView = findViewById<TextView>(R.id.ownership_view)
        val sb = StringBuilder()
        for ((player, props) in OwnershipClient.all()) {
            sb.append("$player besitzt:\n  - ${props.joinToString("\n  - ")}\n\n")
        }
        runOnUiThread {
            ownershipTextView.text = sb.toString()
        }
    }


}

