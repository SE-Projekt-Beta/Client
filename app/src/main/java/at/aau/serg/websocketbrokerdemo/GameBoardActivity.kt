package at.aau.serg.websocketbrokerdemo

import android.content.Intent
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
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

class GameBoardActivity : ComponentActivity() {

    private lateinit var gameStomp: GameStomp
    private lateinit var clientHandler: GameClientHandler

    private lateinit var responseView: TextView
    private lateinit var ownershipView: TextView
    private lateinit var rollDiceButton: Button
    private lateinit var buyButton: Button
    private lateinit var orderView: TextView

    private var playerOrder: List<PlayerDTO> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_fullscreen)

        val playersJson = intent.getStringExtra("players_json")
        if (playersJson != null) {
            val type = object : TypeToken<List<PlayerDTO>>() {}.type
            playerOrder = Gson().fromJson(playersJson, type)
        }

        initViews()
        displayPlayerOrder(currentId = -1)  // Initialanzeige, noch kein Spieler dran
        setupNetwork()
        setupButtons()
    }

    private fun initViews() {
        responseView = findViewById(R.id.response_view)
        ownershipView = findViewById(R.id.ownership_view)
        rollDiceButton = findViewById(R.id.rollDiceBtn)
        buyButton = findViewById(R.id.buybtn)
        orderView = findViewById(R.id.order_view)

        rollDiceButton.visibility = View.GONE
        buyButton.visibility = View.GONE
    }

    private fun displayPlayerOrder(currentId: Int) {
        val text = playerOrder.joinToString("\n") { player ->
            if (player.id == currentId) "➡️ **${player.nickname}**"
            else "   ${player.nickname}"
        }
        runOnUiThread {
            orderView.text = "Spielreihenfolge:\n$text"
        }
    }

    private fun setupNetwork() {
        clientHandler = GameClientHandler(
            showResponse = ::showResponse,
            enableDiceButton = { runOnUiThread { rollDiceButton.visibility = View.VISIBLE } },
            disableDiceButton = { runOnUiThread { rollDiceButton.visibility = View.GONE } },
            showBuyButton = ::showBuyButton,
            showOwnership = ::showOwnership,
            updateCurrentPlayerHighlight = { currentId -> displayPlayerOrder(currentId) },
            updateGameState = ::updateGameState,
        )
        gameStomp = GameStomp(dktHandler = clientHandler, lobbyId = LobbyClient.lobbyId)
        gameStomp.connect()
    }

    private fun setupButtons() {
        rollDiceButton.setOnClickListener {
            val payload = JsonObject().apply {
                addProperty("playerId", LobbyClient.playerId)
            }
            gameStomp.sendGameMessage(GameMessage(LobbyClient.lobbyId, GameMessageType.ROLL_DICE, payload))
            rollDiceButton.visibility = View.GONE
        }
    }

    private fun showResponse(msg: String) {
        runOnUiThread {
            responseView.text = msg
        }
    }

    fun updateGameState(currentPlayerId: String, currentRound: Int, players: JsonArray, board: JsonArray) {
        runOnUiThread {
            // print it on screen
            Log.i("GameActivity", "Current Player ID: $currentPlayerId")
            Log.i("GameActivity", "Current Round: $currentRound")
            Log.i("GameActivity", "Players: $players")
            Log.i("GameActivity", "Board: $board")

            // Update the UI with the current game state
            showOwnership()
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
