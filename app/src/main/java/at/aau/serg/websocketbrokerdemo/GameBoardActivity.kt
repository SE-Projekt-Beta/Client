package at.aau.serg.websocketbrokerdemo

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresPermission
import androidx.constraintlayout.widget.ConstraintLayout
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
import kotlin.math.sqrt
import androidx.core.graphics.toColorInt

class GameBoardActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var shakeTimestamp: Long = 0
    private var shakeThresholdGravity = 2.7f

    private lateinit var diceContainer: LinearLayout
    private lateinit var dice1Image: ImageView
    private lateinit var dice2Image: ImageView

    private lateinit var gameStomp: GameStomp
    private lateinit var gameClientHandler: GameClientHandler

    private lateinit var textCurrentTurn: TextView
    private lateinit var textDice: TextView
    private lateinit var textPlayersCash: LinearLayout
    private lateinit var overlay: TextView

    private lateinit var playerTokenManager: PlayerTokenManager
    private lateinit var ownershipOverlayManager: OwnershipOverlayManager

    private lateinit var btnRollDice: Button
    private lateinit var btnBuildHouse: Button
    private lateinit var btnShowHouses: Button

    private lateinit var tileOverlays: Map<Int, View>

    private var myId = -1
    private lateinit var myNickname: String
    private var lastShownTurnPlayerId: Int = -1
    private var isRolling = false
    private var hasBuiltHouseThisTurn = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        tileOverlays = mapOf(
            2 to findViewById(R.id.amtsplatz),
            4 to findViewById(R.id.kraftZentrale),
            5 to findViewById(R.id.murplatz),
            6 to findViewById(R.id.annenstrasse),
            7 to findViewById(R.id.joaneumring),
            8 to findViewById(R.id.eisenbahnWienGraz),
            10 to findViewById(R.id.josHaydnGasse),
            12 to findViewById(R.id.schlossgrund),
            13 to findViewById(R.id.dampfSchifffahrt),
            14 to findViewById(R.id.seilbahn),
            15 to findViewById(R.id.kaerntnerstrasse),
            16 to findViewById(R.id.mariahilferstrasse),
            17 to findViewById(R.id.kobenzlstrasse),
            18 to findViewById(R.id.eisenbahn),
            19 to findViewById(R.id.landstrasse),
            20 to findViewById(R.id.stifterstrasse),
            22 to findViewById(R.id.museumsstrasse),
            24 to findViewById(R.id.autobuslinie),
            25 to findViewById(R.id.mirabellplatz),
            26 to findViewById(R.id.westbahnstrasse),
            27 to findViewById(R.id.universitaetsplatz),
            29 to findViewById(R.id.burggasse),
            30 to findViewById(R.id.villacherStrasse),
            32 to findViewById(R.id.alterPlatz),
            34 to findViewById(R.id.flughafenWienVenedig),
            35 to findViewById(R.id.mariaTheresienStrasse),
            36 to findViewById(R.id.andreasHoferStrasse),
            37 to findViewById(R.id.boznerplatz),
            39 to findViewById(R.id.arlbergstrasse),
            40 to findViewById(R.id.rathausstrasse)
        )

        tileOverlays.forEach { (index, view) ->
            view.setOnClickListener {
                val tile = ClientBoardMap.getTile(index)
                if (tile != null) {
                    TileInfoDialog(this, tile).show()
                } else {
                    showDialog("Feldinfo", "Feld nicht gefunden.")
                }
            }
        }


        Log.i("GameBoardActivity", "onCreate called")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        myNickname = LobbyClient.username
        myId = LobbyClient.playerId

        val playersJson = intent.getStringExtra("players_json")
        if (playersJson != null) {
            val type = object : TypeToken<List<PlayerClient>>() {}.type
            val players = Gson().fromJson<List<PlayerClient>>(playersJson, type)
            players.forEach { GameStateClient.players[it.id] = it }
        }

        playerTokenManager = PlayerTokenManager(this)
        ownershipOverlayManager = OwnershipOverlayManager(this)

        initViews()
        updatePlayersCashDisplay()
        setupButtons()
        setupNetwork()

        val boardView = findViewById<View>(R.id.boardImage)
        boardView.post {
            val boardWidth = boardView.width.toFloat()
            val boardHeight = boardView.height.toFloat()
            Log.d("BoardSize", "Board width=$boardWidth, height=$boardHeight")

            playerTokenManager.setBoardSize(boardWidth, boardHeight)
            Log.d("TokenDebug", "setBoardSize was called")

            playerTokenManager.positionTokensOnStartTile()

//            ownershipOverlayManager.setBoardSize(boardWidth, boardHeight)
            ownershipOverlayManager.updateOwnershipOverlays(tileOverlays)
        }


    }

    private fun initViews() {
        diceContainer = findViewById(R.id.diceContainer)
        dice1Image = findViewById(R.id.diceImage1)
        dice2Image = findViewById(R.id.diceImage2)

        textCurrentTurn = findViewById(R.id.response_view)
        textDice = findViewById(R.id.textDice)
        textPlayersCash = findViewById(R.id.textPlayersCash)

        overlay = findViewById(R.id.textCurrentTurnBig)

        btnRollDice = findViewById(R.id.rollDiceBtn)
        btnBuildHouse = findViewById(R.id.buildHouseBtn)
        btnShowHouses = findViewById(R.id.btnShowHouses)


        hideActionButtons()
        overlay.visibility = View.GONE
    }

    private fun setupButtons() {
        btnRollDice.setOnClickListener {
            btnRollDice.isEnabled = false
            val payload = GameController.buildPayload("playerId", myId)
            gameStomp.sendGameMessage(GameMessage(LobbyClient.lobbyId, GameMessageType.ROLL_DICE, payload))
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


    fun updateTurnView(currentPlayerId: Int, nickname: String) {
        Log.i("GameBoardActivity", "Aktueller Spieler: $nickname (ID: $currentPlayerId)")

        runOnUiThread {
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

                textCurrentTurn.text = getString(R.string.your_turn)

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
                textCurrentTurn.text = getString(R.string.nickname_turn, nickname)
                disableDiceButton()
                hideActionButtons()
                btnBuildHouse.visibility = View.GONE
            }

            val color = playerTokenManager.getPlayerColor(currentPlayerId)
            textCurrentTurn.setTextColor(color)

            // (optional) Kartenlogik vorbereiten
            val fieldIndex = GameController.getCurrentFieldIndex(currentPlayerId)
            val tileType = ClientBoardMap.getTile(fieldIndex)?.type
            if (currentPlayerId == myId) {
                val payload = GameController.buildPayload("playerId", myId)
                // ggf. weitere Logik
            }
        }
    }

    fun updatePlayersCashDisplay() {
        runOnUiThread {
            textPlayersCash.removeAllViews()
            for ((id, player) in GameStateClient.players) {
                val tv = TextView(this)
                val color = playerTokenManager.getPlayerColor(id)
                tv.setTextColor(color)
                tv.text = "${player.nickname}: ${player.cash}$"
                textPlayersCash.addView(tv)
            }
        }
    }

    fun updateDice(dice1: Int, dice2: Int) {
        runOnUiThread {
            textDice.text = getString(R.string.rolled_values, dice1, dice2)

            showDice(dice1, dice2)
        }
    }

    fun onRollFinished() {
        isRolling = false;
    }

    fun setPlayerTokenPosition(playerId: Int, position: Int) {
        playerTokenManager.setPlayerTokenPosition(playerId, position)
    }

    fun updateTokenPosition(steps: Int) {
        val currentPlayerId = GameStateClient.currentPlayerId
        playerTokenManager.movePlayerToken(currentPlayerId, steps)
    }

    fun updateCashDisplay(cash: Int) {
        updatePlayersCashDisplay()
//        runOnUiThread {
//            textCash.text = getString(R.string.geld_text, cash)
//        }
    }


    fun showGameOverDialog(winnerName: String) {
        runOnUiThread {
            android.app.AlertDialog.Builder(this)
                .setTitle("Spiel beendet")
                .setMessage("Der Gewinner ist $winnerName. Möchten Sie zum Lobby-Bildschirm zurückkehren?")
                .setPositiveButton("Ja") { _, _ ->
                    LobbyClient.lobbyId = -1 // Lobby-ID zurücksetzen
                    LobbyClient.lobbyName = "" // Lobby-Name zurücksetzen
                    LobbyClient.playerId = -1 // Spieler-ID zurücksetzen
                    finish() // Schließt die Activity
                }
                .setNegativeButton("Nein", null)
                .show()
        }

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
                   ownershipOverlayManager.updateOwnershipOverlays(tileOverlays)

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
            // Nur anzeigen, wenn in dieser Runde noch kein Haus gebaut wurde:
            btnBuildHouse.visibility = if (!hasBuiltHouseThisTurn && canBuildHouse) View.VISIBLE else View.GONE

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
        }
    }

    fun updateOwnershipOverlays() {
        ownershipOverlayManager.updateOwnershipOverlays(tileOverlays)
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

    fun enableDiceView() {
        runOnUiThread {
            dice1Image.visibility = View.VISIBLE
            dice2Image.visibility = View.VISIBLE
            diceContainer.visibility = View.VISIBLE
        }
    }

    fun disableDiceView() {
        runOnUiThread {
            dice1Image.visibility = View.GONE
            dice2Image.visibility = View.GONE
            diceContainer.visibility = View.GONE
        }
    }

    private val sensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Nicht benötigt
        }

        @RequiresPermission(Manifest.permission.VIBRATE)
        override fun onSensorChanged(event: SensorEvent?) {
            if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val gX = x / SensorManager.GRAVITY_EARTH
                val gY = y / SensorManager.GRAVITY_EARTH
                val gZ = z / SensorManager.GRAVITY_EARTH

                val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

                if (gForce > shakeThresholdGravity) {
                    val now = System.currentTimeMillis()
                    if (shakeTimestamp + 1000 > now) {
                        return // Verhindert mehrfaches Triggern
                    }
                    shakeTimestamp = now

                    onShakeDetected()
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun onShakeDetected() {
        val lobbyId = LobbyClient.lobbyId
        if (lobbyId < 0) {
            Log.w("Shake", "Ungültige Lobby-ID beim Shake: $lobbyId")
            return
        }

        if (isRolling) return // Wenn schon gwürfelt wird, abbrechen

        if (GameStateClient.currentPlayerId == myId && btnRollDice.visibility == View.VISIBLE && btnRollDice.isEnabled) {
            isRolling = true // Wurf startet
            runOnUiThread {
                btnRollDice.performClick()
            }
            vibrateOnShake()
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun vibrateOnShake() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as? Vibrator
        if (vibrator?.hasVibrator() == true) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        }

    }

    private fun showDice(dice1: Int, dice2: Int) {
        Log.d("Dice", "Zeige Würfel: $dice1, $dice2")

        // Würfel-Bilder aktualisieren
        val diceRes1 = getDiceDrawable(dice1)
        val diceRes2 = getDiceDrawable(dice2)

        dice1Image.setImageResource(diceRes1)
        dice2Image.setImageResource(diceRes2)

        // Dice-Button ausblenden
//        disableDiceButton()

        // Würfel sichtbar machen
        enableDiceView()

        onRollFinished()

    }

    private fun getDiceDrawable(value: Int): Int {
        return when (value)     {
            1 -> R.drawable.view_1
            2 -> R.drawable.view_2
            3 -> R.drawable.view_3
            4 -> R.drawable.view_4
            5 -> R.drawable.view_5
            6 -> R.drawable.view_6
            else -> R.drawable.view_1
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
            btnBuildHouse.visibility = View.GONE
        }
    }
}
