package at.aau.serg.websocketbrokerdemo

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import at.aau.serg.websocketbrokerdemo.game.*
import at.aau.serg.websocketbrokerdemo.game.dialog.BankCardDialog
import at.aau.serg.websocketbrokerdemo.game.dialog.RiskCardDialog
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.model.ClientBoardMap
import at.aau.serg.websocketbrokerdemo.game.dialog.TileInfoDialog
import at.aau.serg.websocketbrokerdemo.model.TileType
import at.aau.serg.websocketbrokerdemo.network.GameStomp
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GameBoardActivity : ComponentActivity() {

    private lateinit var gameStomp: GameStomp
    private lateinit var gameClientHandler: GameClientHandler

    private lateinit var textYouArePlayer: TextView
    private lateinit var textCurrentTurn: TextView
    private lateinit var textDice: TextView
    private lateinit var textCash: TextView
    private lateinit var textTile: TextView
    private lateinit var textTesting: TextView
    private lateinit var overlay: TextView

    private lateinit var playerTokenManager: PlayerTokenManager

    private lateinit var btnRollDice: Button
    private lateinit var btnBuy: Button
    private lateinit var btnBuildHouse: Button
    private lateinit var btnBuildHotel: Button
    private lateinit var btnShowOwnership: Button
    private lateinit var btnViewField: Button
    private lateinit var btnShowHouses: Button

    private lateinit var btnUpdateState: Button

    private var myId = -1
    private lateinit var myNickname: String
    private var lastShownTurnPlayerId: Int = -1
    private var isRolling = false
    private var hasBuiltHouseThisTurn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        Log.i("GameBoardActivity", "onCreate called")

        myNickname = LobbyClient.username
        myId = LobbyClient.playerId

        val playersJson = intent.getStringExtra("players_json")
        if (playersJson != null) {
            val type = object : TypeToken<List<PlayerClient>>() {}.type
            val players = Gson().fromJson<List<PlayerClient>>(playersJson, type)
            players.forEach { GameStateClient.players[it.id] = it }
        }

        playerTokenManager = PlayerTokenManager(this)

        initViews()
        setupButtons()
        setupNetwork()
        playerTokenManager.positionTokensOnStartTile()
    }

    private fun initViews() {
        textYouArePlayer = findViewById(R.id.textYouArePlayer)
        textYouArePlayer.text = getString(R.string.youArePlayer, myNickname, myId)

        textCurrentTurn = findViewById(R.id.response_view)
        textDice = findViewById(R.id.textDice)
        textCash = findViewById(R.id.textCash)
        textTile = findViewById(R.id.textTile)

        textTesting = findViewById(R.id.textTesting)

        overlay = findViewById(R.id.textCurrentTurnBig)

        btnRollDice = findViewById(R.id.rollDiceBtn)
        btnBuy = findViewById(R.id.buybtn)
        btnBuildHouse = findViewById(R.id.buildHouseBtn)
        btnBuildHotel = findViewById(R.id.buildHotelBtn)
        btnShowOwnership = findViewById(R.id.btnShowOwnership)
        btnViewField = findViewById(R.id.btnViewField)
        btnShowHouses = findViewById(R.id.btnShowHouses)

        btnUpdateState = findViewById(R.id.btnUpdateState)

        hideActionButtons()
        overlay.visibility = View.GONE
    }

    private fun setupButtons() {
        btnRollDice.setOnClickListener {
            btnRollDice.isEnabled = false
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

        btnUpdateState.setOnClickListener {
            val payload = GameController.buildPayload("playerId", myId)
            gameStomp.sendGameMessage(GameMessage(LobbyClient.lobbyId, GameMessageType.REQUEST_GAME_STATE, payload))
        }
        btnShowHouses.setOnClickListener {
            val overview = GameController.getHouseOverview()
            showDialog("Häuser aller Spieler", overview)
        }
    }

    private fun setupNetwork() {
        gameClientHandler = GameClientHandler(this)
        gameStomp = GameStomp(gameClientHandler, LobbyClient.lobbyId)
        gameStomp.setOnConnectedListener { gameStomp.requestGameStateWithRetry() }
        gameStomp.connect()
    }

    fun updateTestView(message: String) {
        runOnUiThread {
            textTesting.text = message
        }
    }

    fun updateTurnView(currentPlayerId: Int, nickname: String) {
        Log.i("GameBoardActivity", "Aktueller Spieler: $nickname (ID: $currentPlayerId)")

        runOnUiThread {
            textYouArePlayer.text = getString(R.string.youArePlayer, myNickname, myId)
            textCurrentTurn.text = getString(R.string.nickname_turn, nickname)

            // Overlay nur bei Spielerwechsel anzeigen
            if (currentPlayerId != lastShownTurnPlayerId) {
                overlay.text = getString(R.string.nickname_turn, nickname)
                overlay.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    overlay.visibility = View.GONE
                }, 2000)
                lastShownTurnPlayerId = currentPlayerId

                // Beim ersten Zug für diesen Spieler: Bau-Status zurücksetzen
                if (currentPlayerId == myId) {
                    hasBuiltHouseThisTurn = false
                }
            }

            if (currentPlayerId == myId) {
                enableDiceButton()

                // Nur anzeigen, wenn Hausbau erlaubt und noch nicht gebaut
                val buildableTiles = GameController.getBuildableHouseTiles(myId)
                if (!hasBuiltHouseThisTurn && buildableTiles.isNotEmpty()) {
                    btnBuildHouse.visibility = View.VISIBLE
                    btnBuildHouse.isEnabled = true
                } else {
                    btnBuildHouse.visibility = View.GONE
                }

            } else {
                disableDiceButton()
                hideActionButtons()
                btnBuildHouse.visibility = View.GONE
            }

            // (optional) Kartenlogik vorbereiten
            val fieldIndex = GameController.getCurrentFieldIndex(currentPlayerId)
            val tileType = ClientBoardMap.getTile(fieldIndex)?.type
            if (currentPlayerId == myId) {
                val payload = GameController.buildPayload("playerId", myId)
                // ggf. weitere Logik
            }
        }
    }

    fun updateDice(roll1: Int, roll2: Int) {
        runOnUiThread {
            textDice.text = getString(R.string.rolled_values, roll1, roll2)
        }
    }

    fun onRollFinished() {
        isRolling = false;
    }

    fun updateTile(tileName: String, tileIndex: Int) {
        runOnUiThread {
            textTile.text = getString(R.string.landed_on, tileName, tileIndex)
        }
    }

    fun updateTokenPosition(steps: Int) {
        val currentPlayerId = GameStateClient.currentPlayerId
        playerTokenManager.movePlayerToken(currentPlayerId, steps)
    }

    fun updateCashDisplay(cash: Int) {
        runOnUiThread {
            textCash.text = getString(R.string.geld_text, cash)
        }
    }


    fun showGameOverDialog(winnerName: String) {
        showDialog("Spiel beendet, Gewinner", winnerName)
    }

    fun showDialog(title: String, message: String) {
        runOnUiThread {
            android.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    fun showBuyDialog(tilePos: Int, tileName: String) {
        runOnUiThread {
            Log.i("GameBoardActivity", "showBuyDialog: $tileName")
            val dialog = android.app.AlertDialog.Builder(this)
                .setTitle("Kaufen?")
                .setMessage("Möchten Sie $tileName kaufen?")
                .setPositiveButton("Ja") { _, _ ->
                    val payload = GameController.buildPayload("playerId", myId, "tilePos", tilePos)
                    gameStomp.sendGameMessage(
                        GameMessage(
                            LobbyClient.lobbyId,
                            GameMessageType.BUY_PROPERTY,
                            payload
                        )
                    )

                }
                .setNegativeButton("Nein") { _, _ ->
                    Log.i("GameBoardActivity", "Kauf abgebrochen.")
                }
                .create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.show()
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val payload = GameController.buildPayload("playerId", myId, "tilePos", tilePos)
                gameStomp.sendGameMessage(
                    GameMessage(
                        LobbyClient.lobbyId,
                        GameMessageType.BUY_PROPERTY,
                        payload
                    )
                )
                dialog.dismiss()
            }
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                Log.i("GameBoardActivity", "Kauf abgebrochen.")
                val payload = GameController.buildPayload("playerId", myId, "tilePos", -1)
                gameStomp.sendGameMessage(
                    GameMessage(
                        LobbyClient.lobbyId,
                        GameMessageType.BUY_PROPERTY,
                        payload
                    )
                )
                dialog.dismiss()
            }
        }
    }

    fun showRollPrisonDialog(dice1: Int, dice2: Int) {
        runOnUiThread {
            val dialog = android.app.AlertDialog.Builder(this)
                .setTitle("Gefängniswurf")
                .setMessage("Sie haben $dice1 und $dice2 geworfen.")
                // close button just close the dialog
                .setNeutralButton("OK") { _, _ ->
                    Log.i("GameBoardActivity", "Dialog geschlossen.")
                }
                .create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.show()

        }
    }


    fun showPayPrisonDialog() {
        runOnUiThread {
            val dialog = android.app.AlertDialog.Builder(this)
                .setTitle("Gefängnisgeld")
                .setMessage("Möchten Sie 50 Euro zahlen, um aus dem Gefängnis zu kommen?")
                .setPositiveButton("Ja") { _, _ ->
                    val payload = GameController.buildPayload("playerId", myId)
                    gameStomp.sendGameMessage(
                        GameMessage(
                            LobbyClient.lobbyId,
                            GameMessageType.PAY_PRISON,
                            payload
                        )
                    )
                }
                .setNegativeButton("Nein") { _, _ ->
                    val payload = GameController.buildPayload("playerId", myId)
                    gameStomp.sendGameMessage(
                        GameMessage(
                            LobbyClient.lobbyId,
                            GameMessageType.ROLL_PRISON,
                            payload
                        )
                    )
                    Log.i("GameBoardActivity", "Zahlung abgebrochen.")
                }
                .create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.show()
        }
    }


    fun showBuyOptions(tilePos: Int, tileName: String, canBuy: Boolean, canBuildHouse: Boolean, canBuildHotel: Boolean) {
        runOnUiThread {
            btnBuy.visibility = if (canBuy) View.VISIBLE else View.GONE
            // Nur anzeigen, wenn in dieser Runde noch kein Haus gebaut wurde:
            btnBuildHouse.visibility = if (!hasBuiltHouseThisTurn && canBuildHouse) View.VISIBLE else View.GONE
            btnBuildHotel.visibility = if (canBuildHotel) View.VISIBLE else View.GONE

            btnBuy.setOnClickListener {
                val payload = GameController.buildPayload("playerId", myId, "tilePos", tilePos)
                gameStomp.sendGameMessage(GameMessage(LobbyClient.lobbyId, GameMessageType.BUY_PROPERTY, payload))
                btnBuy.visibility = View.GONE
            }

            btnBuildHouse.setOnClickListener {
                val buildableTiles = GameController.getBuildableHouseTiles(myId)
                if (buildableTiles.isEmpty()) {
                    showDialog("Keine Felder", "Du hast keine Grundstücke, auf denen du ein Haus bauen kannst.")
                    return@setOnClickListener
                }

                val tileNames = buildableTiles.map { ClientBoardMap.getTile(it)?.name ?: "Feld $it" }
                val tileIndices = buildableTiles.toIntArray()

                AlertDialog.Builder(this)
                    .setTitle("Grundstück auswählen")
                    .setItems(tileNames.toTypedArray()) { _, which ->
                        val selectedTilePos = tileIndices[which]
                        val payload = GameController.buildPayload("playerId", myId, "tilePos", selectedTilePos)
                        gameStomp.sendGameMessage(GameMessage(LobbyClient.lobbyId, GameMessageType.BUILD_HOUSE, payload))

                        // GENAU HIER → Button endgültig ausblenden:
                        hasBuiltHouseThisTurn = true
                        btnBuildHouse.visibility = View.GONE
                    }
                    .setNegativeButton("Abbrechen", null)
                    .show()
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
            btnRollDice.isEnabled = true
        }
    }

    fun disableDiceButton() {
        runOnUiThread {
            btnRollDice.visibility = View.GONE
        }
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
