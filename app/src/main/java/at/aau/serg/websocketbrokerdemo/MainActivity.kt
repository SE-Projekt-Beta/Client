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
import at.aau.serg.websocketbrokerdemo.dkt.OwnershipClient
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import com.example.myapplication.R
import org.json.JSONObject


class MainActivity : ComponentActivity() {

    private lateinit var mystomp: MyStomp
    private lateinit var clientHandler: DktClientHandler

    private lateinit var responseView: TextView
    private lateinit var ownershipView: TextView
    private lateinit var lobbyView: TextView
    private lateinit var rollDiceButton: Button
    private lateinit var buyButton: Button
    private lateinit var myPlayerName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fragment_fullscreen)

        myPlayerName = intent.getStringExtra("PLAYER_NAME") ?: "unknown"

        initViews()
        setupNetwork()
        setupButtons()
    }


    private fun initViews() {
        responseView = findViewById(R.id.response_view)
        ownershipView = findViewById(R.id.ownership_view)
        rollDiceButton = findViewById(R.id.rollDiceBtn)
        buyButton = findViewById(R.id.buybtn)
        buyButton.visibility = View.GONE
    }

    private fun setupNetwork() {
        clientHandler = DktClientHandler(this)
        mystomp = MyStomp(dktHandler = clientHandler)
        mystomp.connect()
    }

    private fun setupButtons() {
        rollDiceButton.setOnClickListener {
            val payload = JSONObject().apply {
                put("playerId", myPlayerName)
            }
            mystomp.sendGameMessage(GameMessage("roll_dice", payload.toString()))
        }

    }

    fun showResponse(msg: String) {
        runOnUiThread {
            responseView.text = msg
        }
    }

    fun showOwnership() {
        runOnUiThread {
            val text = OwnershipClient.all().entries.joinToString("\n\n") { (player, props) ->
                "$player besitzt:\n  - ${props.joinToString("\n  - ")}"
            }
            ownershipView.text = text
        }
    }

    fun updateLobby(players: List<String>) {
        runOnUiThread {
            lobbyView.text = "Lobby:\n" + players.joinToString("\n") { "- $it" }
        }
    }

    fun showBuyButton(tileName: String, tilePos: Int, playerId: String) {
        runOnUiThread {
            buyButton.apply {
                text = "Kaufen: $tileName"
                visibility = View.VISIBLE
                setOnClickListener {
                    val payload = JSONObject().apply {
                        put("playerId", myPlayerName)
                        put("tilePos", tilePos)
                    }
                    mystomp.sendGameMessage(GameMessage("buy_property", payload.toString()))
                    visibility = View.GONE
                }
            }
        }
    }

    fun showEventCard(text: String, description: String) {
        runOnUiThread {
            val title = when {
                text.contains("Risiko", ignoreCase = true) -> "⚠️ Risiko-Karte"
                text.contains("Bank", ignoreCase = true) -> "🏦 Bank-Karte"
                else -> "📦 Ereigniskarte"
            }
            android.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
