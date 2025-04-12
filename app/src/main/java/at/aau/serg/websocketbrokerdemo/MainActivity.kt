package at.aau.serg.websocketbrokerdemo

import MyStomp
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import at.aau.serg.websocketbrokerdemo.dkt.DktClientHandler
import at.aau.serg.websocketbrokerdemo.network.GameMessage
import at.aau.serg.websocketbrokerdemo.dkt.OwnershipClient
import at.aau.serg.websocketbrokerdemo.network.MessageType
import com.example.myapplication.R
import com.google.gson.JsonObject


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
            val payload = JsonObject().apply {
                addProperty("playerId", myPlayerName)
            }
            mystomp.sendGameMessage(GameMessage(MessageType.ROLL_DICE, payload))
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
                    val payload = JsonObject().apply {
                        addProperty("playerId", myPlayerName)
                        addProperty("tilePos", tilePos)
                    }
                    mystomp.sendGameMessage(GameMessage(MessageType.BUY_PROPERTY, payload))
                    visibility = View.GONE
                }
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
