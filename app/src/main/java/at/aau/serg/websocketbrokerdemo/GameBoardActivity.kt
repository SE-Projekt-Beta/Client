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
import at.aau.serg.websocketbrokerdemo.model.TileType
import at.aau.serg.websocketbrokerdemo.network.GameStomp
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import com.google.gson.Gson
import com.google.gson.JsonObject
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
    private var lastShownTurnPlayerId: Int = -1
    private var lastHandledTileIndex: Int? = null
    private var triggeredMessagesThisTurn = mutableSetOf<GameMessageType>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        myNickname = intent.getStringExtra("USERNAME") ?: "Unknown"
        myId = LobbyClient.playerId

        val playersJson = intent.getStringExtra("players_json")
        if (playersJson != null) {
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            val parsedList = Gson().fromJson<List<Map<String, Any>>>(playersJson, type)
            parsedList.forEach {
                val id = (it["id"] as Double).toInt()
                val nickname = it["nickname"] as String
                val player = PlayerClient(id, nickname, 0, 0, true, false, false)
                GameStateClient.players[id] = player
            }
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
            btnRollDice.isEnabled = false
            lastHandledTileIndex = null
            triggeredMessagesThisTurn.clear()

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

    fun updateTurnView(currentPlayerId: Int) {
        runOnUiThread {
            val nickname = GameStateClient.getNickname(currentPlayerId) ?: "Spieler X"
            textCurrentTurn.text = "$nickname ist am Zug"

            if (currentPlayerId != lastShownTurnPlayerId) {
                overlay.text = "$nickname ist am Zug"
                overlay.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({ overlay.visibility = View.GONE }, 2000)
                lastShownTurnPlayerId = currentPlayerId
                triggeredMessagesThisTurn.clear()
            }

            val fieldIndex = GameController.getCurrentFieldIndex(currentPlayerId)
            val tile = ClientBoardMap.getTile(fieldIndex)
            val tileType = tile?.type

            val isSuspended = GameStateClient.players[myId]?.suspended == true

            if (currentPlayerId == myId) {
                if (isSuspended) disableDiceButton() else enableDiceButton()
            } else {
                disableDiceButton()
                hideActionButtons()
                lastHandledTileIndex = null
            }

            val ownsThisTile = GameStateClient.players[myId]?.properties?.contains(fieldIndex) == true
            if (currentPlayerId == myId && ownsThisTile) {
                GameStateClient.players[myId]?.visitedOwnedTiles?.add(fieldIndex)
            }

            if (currentPlayerId == myId && fieldIndex != lastHandledTileIndex) {
                lastHandledTileIndex = fieldIndex
                val payload = GameController.buildPayload("playerId", myId, "tilePos", fieldIndex)

                when (tileType) {
                    TileType.BANK -> sendOnce(GameMessageType.DRAW_BANK_CARD, payload)
                    TileType.RISK -> sendOnce(GameMessageType.DRAW_RISK_CARD, payload)
                    TileType.START -> sendOnce(GameMessageType.PASS_START, payload)
                    TileType.TAX -> {
                        showTaxDialog(tile?.name ?: "eine Steuer")
                        sendOnce(GameMessageType.PAY_TAX, payload)
                    }
                    TileType.GOTO_JAIL -> {
                        showDialog("Gefängnis", "Du musst ins Gefängnis gehen.")
                        sendOnce(GameMessageType.GO_TO_JAIL, payload)
                    }
                    TileType.STREET -> {
                        val ownerId = OwnershipClient.getOwnerId(fieldIndex)
                        if (ownerId != null && ownerId != myId) {
                            val ownerName = GameStateClient.getNickname(ownerId) ?: "ein Spieler"
                            showRentDialog(ownerName)
                            sendOnce(GameMessageType.PAY_RENT, payload)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun sendOnce(type: GameMessageType, payload: JsonObject) {
        if (!triggeredMessagesThisTurn.contains(type)) {
            triggeredMessagesThisTurn.add(type)
            gameStomp.sendGameMessage(GameMessage(LobbyClient.lobbyId, type, payload))
        }
    }


    fun updateDice(diceValue: Int) {
        runOnUiThread { textDice.text = "Gewürfelt: $diceValue" }
    }

    fun updateTile(tileName: String) {
        runOnUiThread { textTile.text = "Gelandet auf: $tileName" }
    }

    fun updateCashDisplay(cash: Int) {
        runOnUiThread { textCash.text = "Geld: $cash €" }
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

    private fun showDialog(title: String, message: String) {
        runOnUiThread {
            android.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    fun showTaxDialog(title: String) {
        runOnUiThread {
            TaxDialog(this, "Du musst $title zahlen.").show()
        }
    }

    fun showRentDialog(ownerName: String) {
        runOnUiThread {
            showDialog("Miete fällig", "Du musst $ownerName Miete zahlen.")
        }
    }

    fun showBuyOptions(tilePos: Int, tileName: String, canBuy: Boolean, canBuildHouse: Boolean, canBuildHotel: Boolean) {
        runOnUiThread {
            val hasVisitedBefore = GameStateClient.players[myId]?.visitedOwnedTiles?.contains(tilePos) == true
            btnBuy.visibility = if (canBuy) View.VISIBLE else View.GONE
            btnBuildHouse.visibility = if (canBuildHouse && hasVisitedBefore) View.VISIBLE else View.GONE
            btnBuildHotel.visibility = if (canBuildHotel && hasVisitedBefore) View.VISIBLE else View.GONE

            btnBuy.setOnClickListener {
                val payload = GameController.buildPayload("playerId", myId, "tilePos", tilePos)
                sendOnce(GameMessageType.BUY_PROPERTY, payload)
                btnBuy.visibility = View.GONE
            }

            btnBuildHouse.setOnClickListener {
                val payload = GameController.buildPayload("playerId", myId, "tilePos", tilePos)
                sendOnce(GameMessageType.BUILD_HOUSE, payload)
                btnBuildHouse.visibility = View.GONE
            }

            btnBuildHotel.setOnClickListener {
                val payload = GameController.buildPayload("playerId", myId, "tilePos", tilePos)
                sendOnce(GameMessageType.BUILD_HOTEL, payload)
                btnBuildHotel.visibility = View.GONE
            }
        }
    }

    fun enableDiceButton() {
        runOnUiThread {
            btnRollDice.visibility = View.VISIBLE
            btnRollDice.isEnabled = true
        }
    }

    fun disableDiceButton() {
        runOnUiThread { btnRollDice.visibility = View.GONE }
    }

    fun showPlayerLost(playerId: Int) {
        val nickname = GameStateClient.getNickname(playerId) ?: "Ein Spieler"
        runOnUiThread {
            showDialog("Bankrott", "$nickname ist bankrott und scheidet aus dem Spiel aus.")
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
