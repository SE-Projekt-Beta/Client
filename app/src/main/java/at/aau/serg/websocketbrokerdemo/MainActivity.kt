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

        // Initialisierung
        clientHandler = DktClientHandler(this)
        mystomp = MyStomp(clientHandler)

        // Buttons
        findViewById<Button>(R.id.connectbtn).setOnClickListener {
            mystomp.connect()
        }

        findViewById<Button>(R.id.hellobtn).setOnClickListener {
            mystomp.sendHello()
        }

        findViewById<Button>(R.id.jsonbtn).setOnClickListener {
            mystomp.sendJson()
        }

        findViewById<Button>(R.id.dktbtn).setOnClickListener {
            val msg = GameMessage("test", "Hallo vom Client")
            mystomp.sendGameMessage(msg)
        }

        findViewById<Button>(R.id.rollDiceBtn).setOnClickListener {
            val payload = """{"playerId": "player1"}"""
            val msg = GameMessage("roll_dice", payload)
            mystomp.sendGameMessage(msg)
        }

        response = findViewById(R.id.response_view)
    }

    // Wird von DktClientHandler aufgerufen, wenn man ein Feld kaufen darf
    fun showBuyButton(tileName: String, tilePos: Int, playerId: String) {
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

