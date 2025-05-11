package at.aau.serg.websocketbrokerdemo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import at.aau.serg.websocketbrokerdemo.game.*
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.model.ClientBoardMap
import at.aau.serg.websocketbrokerdemo.model.TileInfoDialog
import at.aau.serg.websocketbrokerdemo.network.GameStomp
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GameBoardActivity : ComponentActivity() {

    private lateinit var gameStomp: GameStomp
    private lateinit var gameClientHandler: GameClientHandler

    private lateinit var textCurrentTurn: TextView
    private lateinit var textDice: TextView
    private lateinit var textCash: TextView
    private lateinit var textTile: TextView
    private lateinit var overlay: TextView

    private lateinit var btnRollDice: Button
    private lateinit var btnBuy: Button
    private lateinit var btnBuildHouse: Button
    private lateinit var btnBuildHotel: Button
    private lateinit var btnShowOwnership: Button
    private lateinit var btnViewField: Button

    private var myId = -1
    private lateinit var myNickname: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        Log.i("GameBoardActivity", "onCreate called")

        myNickname = intent.getStringExtra("USERNAME") ?: "Unknown"
        myId = intent.getIntExtra("PLAYER_ID", -1)

        val playersJson = intent.getStringExtra("players_json")
        if (playersJson != null) {
            val type = object : TypeToken<List<PlayerClient>>() {}.type
            val players = Gson().fromJson<List<PlayerClient>>(playersJson, type)
            players.forEach { GameStateClient.players[it.id] = it }
        }

        initViews()
        setupButtons()
        setupNetwork()
    }

    private fun initViews() {
        textCurrentTurn = findViewById(R.id.response_view)
        textDice = findViewById(R.id.textDice)
        textCash = findViewById(R.id.textCash)
        textTile = findViewById(R.id.textTile)
        overlay = findViewById(R.id.textCurrentTurnBig)

        btnRollDice = findViewById(R.id.rollDiceBtn)
        btnBuy = findViewById(R.id.buybtn)
        btnBuildHouse = findViewById(R.id.buildHouseBtn)
        btnBuildHotel = findViewById(R.id.buildHotelBtn)
        btnShowOwnership = findViewById(R.id.btnShowOwnership)
        btnViewField = findViewById(R.id.btnViewField)

        hideActionButtons()
        overlay.visibility = View.GONE
    }

    private fun setupButtons() {
        btnRollDice.setOnClickListener {
            val payload = GameController.buildPayload("playerId", myId)
            gameStomp.sendGameMessage(GameMessage(LobbyClient.lobbyId, GameMessageType.ROLL_DICE, payload))
        }

        btnShowOwnership.setOnClickListener {
            val props = GameController.getOwnedTileNames(myId)
            showDialog("Besitz", props.joinToString("\n").ifBlank { "Keine" })
        }

        btnViewField.setOnClickListener {
            val tileIndex = GameController.getCurrentFieldIndex(myId)
            val tile = ClientBoardMap.getTile(tileIndex)
            if (tile != null) {
                TileInfoDialog(this, tile).show()
            } else {
                showDialog("Feldinfo", "Feld nicht gefunden.")
            }
        }
    }

    private fun setupNetwork() {
        gameClientHandler = GameClientHandler(this)
        gameStomp = GameStomp(gameClientHandler, LobbyClient.lobbyId)
        gameStomp.setOnConnectedListener { gameStomp.requestGameState() }
        gameStomp.connect()
    }

    fun updateTurnView(currentPlayerId: Int, nickname: String) {
        runOnUiThread {
            textCurrentTurn.text = "$nickname ist am Zug"
            if (currentPlayerId == myId) {
                showOverlayMessage("$nickname ist am Zug")
                enableDiceButton()
            } else {
                disableDiceButton()
                hideActionButtons()
            }
        }
    }

    fun updateDice(diceValue: Int) {
        runOnUiThread {
            textDice.text = "Gewürfelt: $diceValue"
        }
    }

    fun updateTile(tileName: String) {
        runOnUiThread {
            textTile.text = "Gelandet auf: $tileName"
        }
    }

    fun updateCashDisplay(cash: Int) {
        runOnUiThread {
            textCash.text = "Geld: $cash €"
        }
    }

    fun showEventCard(title: String, description: String) {
        runOnUiThread {
            overlay.visibility = View.GONE
            if (title.contains("Bank", true)) {
                BankCardDialog(this, title, description).show()
            } else {
                RiskCardDialog(this, title, description).show()
            }
        }
    }

    fun showGameOverDialog(ranking: String) {
        showDialog("Spiel beendet", ranking)
    }

    private fun showOverlayMessage(msg: String) {
        runOnUiThread {
            overlay.text = msg
            overlay.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                overlay.visibility = View.GONE
            }, 2000)
        }
    }

    private fun showDialog(title: String, message: String) {
        runOnUiThread {
            android.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    fun showBuyOptions(tilePos: Int, tileName: String, canBuy: Boolean, canBuildHouse: Boolean, canBuildHotel: Boolean) {
        runOnUiThread {
            btnBuy.visibility = if (canBuy) View.VISIBLE else View.GONE
            btnBuildHouse.visibility = if (canBuildHouse) View.VISIBLE else View.GONE
            btnBuildHotel.visibility = if (canBuildHotel) View.VISIBLE else View.GONE

            btnBuy.setOnClickListener {
                val payload = GameController.buildPayload("playerId", myId, "tilePos", tilePos)
                gameStomp.sendGameMessage(GameMessage(LobbyClient.lobbyId, GameMessageType.BUY_PROPERTY, payload))
                btnBuy.visibility = View.GONE
            }

            btnBuildHouse.setOnClickListener {
                val payload = GameController.buildPayload("playerId", myId, "tilePos", tilePos)
                gameStomp.sendGameMessage(GameMessage(LobbyClient.lobbyId, GameMessageType.BUILD_HOUSE, payload))
                btnBuildHouse.visibility = View.GONE
            }

            btnBuildHotel.setOnClickListener {
                val payload = GameController.buildPayload("playerId", myId, "tilePos", tilePos)
                gameStomp.sendGameMessage(GameMessage(LobbyClient.lobbyId, GameMessageType.BUILD_HOTEL, payload))
                btnBuildHotel.visibility = View.GONE
            }
        }
    }

    fun enableDiceButton() {
        runOnUiThread {
            btnRollDice.visibility = View.VISIBLE
        }
    }

    fun disableDiceButton() {
        runOnUiThread {
            btnRollDice.visibility = View.GONE
        }
    }

    fun hideActionButtons() {
        runOnUiThread {
            btnBuy.visibility = View.GONE
            btnBuildHouse.visibility = View.GONE
            btnBuildHotel.visibility = View.GONE
        }
    }
}
