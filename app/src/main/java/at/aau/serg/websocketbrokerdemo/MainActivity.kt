package at.aau.serg.websocketbrokerdemo

import at.aau.serg.websocketbrokerdemo.network.GameStomp
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import at.aau.serg.websocketbrokerdemo.game.GameClientHandler
import at.aau.serg.websocketbrokerdemo.game.GameStateClient
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.game.OwnershipClient
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.example.myapplication.R
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

class MainActivity : ComponentActivity() {

    private lateinit var gameStomp: GameStomp
    private lateinit var clientHandler: GameClientHandler

    private lateinit var responseView: TextView
    private lateinit var ownershipView: TextView
    private lateinit var rollDiceButton: Button
    private lateinit var buyButton: Button

    private lateinit var myPlayerName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fragment_fullscreen)

        myPlayerName = intent.getStringExtra("USERNAME") ?: "unknown"

        val playersJson = intent.getStringExtra("players_json")
        if (playersJson != null) {
            val type = object : TypeToken<List<PlayerDTO>>() {}.type
            val players = Gson().fromJson<List<PlayerDTO>>(playersJson, type)
            LobbyClient.setPlayers(players)
        }

        initViews()
        setupNetwork()
        setupButtons()
        checkIfMyTurn()
    }

    private fun initViews() {
        responseView = findViewById(R.id.response_view)
        ownershipView = findViewById(R.id.ownership_view)
        rollDiceButton = findViewById(R.id.rollDiceBtn)
        buyButton = findViewById(R.id.buybtn)
        buyButton.visibility = View.GONE
    }

    private fun setupNetwork() {
        clientHandler = GameClientHandler(this)
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

    private fun checkIfMyTurn() {
        val order = LobbyClient.allPlayers()
        val myIndex = order.indexOfFirst { it.nickname == myPlayerName }

        val isMyTurn = myIndex == 0
        runOnUiThread {
            rollDiceButton.visibility = if (isMyTurn) View.VISIBLE else View.GONE
        }
    }

    fun showResponse(msg: String) {
        runOnUiThread {
            responseView.text = msg
        }
    }

    fun showOwnership() {
        runOnUiThread {
            /*
            val text = GameStateClient.getAllPositions().entries.joinToString("\n\n") { (id, pos) ->
                val state = GameStateClient.getPlayerState(id)
                val props = state?.properties?.joinToString(", ") ?: "keine"
                "$id -> Feld: $pos, Besitz: $props, Geld: ${state?.money}€"
        }

            ownershipView.text = text

             */
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

    fun showJailDialog(playerId: String) {
        runOnUiThread {
            android.app.AlertDialog.Builder(this)
                .setTitle("🚔 Ins Gefängnis!")
                .setMessage("$playerId wurde ins Gefängnis geschickt.")
                .setPositiveButton("OK", null)
                .show()
        }
    }
    fun enableDiceButton() {
        runOnUiThread {
            rollDiceButton.visibility = View.VISIBLE
        }
    }

    fun disableDiceButton() {
        runOnUiThread {
            rollDiceButton.visibility = View.GONE
        }
    }

    fun getMyPlayerName(): String {
        return myPlayerName
    }


}