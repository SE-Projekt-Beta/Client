package at.aau.serg.websocketbrokerdemo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import at.aau.serg.websocketbrokerdemo.game.*
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.network.GameStomp
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.JsonObject

class GameBoardActivity : ComponentActivity() {

    private lateinit var gameStomp: GameStomp
    private lateinit var clientHandler: GameClientHandler

    private lateinit var responseView: TextView
    private lateinit var ownershipView: TextView
    private lateinit var rollDiceButton: Button
    private lateinit var buyButton: Button

    private lateinit var myPlayerName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_fullscreen)

        myPlayerName = intent.getStringExtra("USERNAME") ?: "unknown"

        val playersJson = intent.getStringExtra("players_json")
        if (playersJson != null) {
            val type = object : TypeToken<List<PlayerDTO>>() {}.type
            val players: List<PlayerDTO> = Gson().fromJson(playersJson, type)
            LobbyClient.setPlayers(players)
            Log.i("GameBoardActivity", "Player order loaded: $players")
        }

        initViews()
        setupNetwork()
        setupButtons()
    }


    private fun initViews() {
        responseView = findViewById(R.id.response_view)
        ownershipView = findViewById(R.id.ownership_view)
        rollDiceButton = findViewById(R.id.rollDiceBtn)
        buyButton = findViewById(R.id.buybtn)

        // Zu Beginn sind Buttons nicht sichtbar
        rollDiceButton.visibility = View.GONE
        buyButton.visibility = View.GONE
    }

    private fun setupNetwork() {
        clientHandler = GameClientHandler(
            showResponse = ::showResponse,
            enableDiceButton = { runOnUiThread { rollDiceButton.visibility = View.VISIBLE } },
            disableDiceButton = { runOnUiThread { rollDiceButton.visibility = View.GONE } },
            showBuyButton = ::showBuyButton,
            showOwnership = ::showOwnership
        )
        gameStomp = GameStomp(dktHandler = clientHandler, lobbyId = LobbyClient.lobbyId)
        gameStomp.connect()
    }

    private fun setupButtons() {
        rollDiceButton.setOnClickListener {
            val payload = JsonObject().apply {
                addProperty("playerId", LobbyClient.playerId) // wichtig: INT!
            }
            gameStomp.sendGameMessage(GameMessage(LobbyClient.lobbyId, GameMessageType.ROLL_DICE, payload))
        }
    }

    private fun showResponse(msg: String) {
        runOnUiThread {
            responseView.text = msg
        }
    }

    private fun showBuyButton(tileName: String, tilePos: Int, playerId: Int) {
        runOnUiThread {
            buyButton.apply {
                text = "Kaufen: $tileName"
                visibility = View.VISIBLE
                setOnClickListener {
                    val payload = JsonObject().apply {
                        addProperty("playerId", playerId)
                        addProperty("tilePos", tilePos)
                    }
                    gameStomp.sendGameMessage(GameMessage(LobbyClient.lobbyId, GameMessageType.BUY_PROPERTY, payload))
                    visibility = View.GONE
                }
            }
        }
    }

    private fun showOwnership() {
        runOnUiThread {
            val text = OwnershipClient.all().entries.joinToString("\n\n") { (player, props) ->
                "$player besitzt:\n  - ${props.joinToString("\n  - ")}"
            }
            ownershipView.text = text
        }
    }
}
