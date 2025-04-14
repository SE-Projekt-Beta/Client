package at.aau.serg.websocketbrokerdemo

import at.aau.serg.websocketbrokerdemo.network.GameStomp
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import at.aau.serg.websocketbrokerdemo.dkt.DktClientHandler
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.dkt.OwnershipClient
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import com.example.myapplication.R
import com.google.gson.JsonObject


class MainActivity : ComponentActivity() {

    private lateinit var gameStomp: GameStomp
    private lateinit var clientHandler: DktClientHandler

    private lateinit var responseView: TextView
    private lateinit var ownershipView: TextView
    private lateinit var rollDiceButton: Button
    private lateinit var buyButton: Button

    private lateinit var myPlayerName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fragment_fullscreen)

        myPlayerName = intent.getStringExtra("USERNAME") ?: "unknown" // <- war PLAYER_NAME, nun USERNAME!

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
        gameStomp = GameStomp(dktHandler = clientHandler)
        gameStomp.connect()
    }

    private fun setupButtons() {
        rollDiceButton.setOnClickListener {
            val payload = JsonObject().apply {
                addProperty("playerId", myPlayerName)
            }
            gameStomp.sendGameMessage(GameMessage(GameMessageType.ROLL_DICE, payload))
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
                    gameStomp.sendGameMessage(GameMessage(GameMessageType.BUY_PROPERTY, payload))
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