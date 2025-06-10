package at.aau.serg.websocketbrokerdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.game.GameStateClient
import at.aau.serg.websocketbrokerdemo.game.PlayerClient
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.model.ClientBoardMap
import at.aau.serg.websocketbrokerdemo.model.ClientTile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import at.aau.serg.websocketbrokerdemo.game.GameClientHandler
import at.aau.serg.websocketbrokerdemo.game.GameController
import at.aau.serg.websocketbrokerdemo.game.OwnershipClient
import at.aau.serg.websocketbrokerdemo.network.GameStomp
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import com.google.gson.JsonArray

val playerColors = mutableMapOf<Int, Color>()

class GameBoardActivity : ComponentActivity() {

    private lateinit var gameStomp: GameStomp
    private lateinit var gameClientHandler: GameClientHandler

    private lateinit var viewModel: GameBoardViewModel

    private var myId = -1
    private lateinit var myNickname: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize viewModel property here (once)
        viewModel = GameBoardViewModel()

        myNickname = LobbyClient.username
        myId = LobbyClient.playerId

        val playersJson = intent.getStringExtra("players_json")
        if (playersJson != null) {
            val type = object : TypeToken<List<PlayerClient>>() {}.type
            val players = Gson().fromJson<List<PlayerClient>>(playersJson, type)
            players.forEach { GameStateClient.players[it.id] = it }
        }

        gameClientHandler = GameClientHandler(this)
        gameStomp = GameStomp(gameClientHandler, LobbyClient.lobbyId)
        gameStomp.setOnConnectedListener { gameStomp.requestGameStateWithRetry() }
        gameStomp.connect()

        // Setup player colors and positions inside the already initialized viewModel
        val playerCount = GameStateClient.players.size
        GameStateClient.players.values.forEach { player ->
            playerColors[player.id] = when (player.id % playerCount) {
                1 -> Color.Red
                2 -> Color.Blue
                3 -> Color.Green
                4 -> Color.Yellow
                else -> Color.Gray
            }
            viewModel.updatePlayerPosition(player.id, player.position)
        }

        viewModel.onRollDice = {
            val payload = GameController.buildPayload("playerId", myId)
            gameStomp.sendGameMessage(GameMessage(LobbyClient.lobbyId, GameMessageType.ROLL_DICE, payload))
        }

        setContent {
            GameBoardScreen(myNickname, myId, viewModel)
        }
    }

    fun updatePlayerPositions(players: List<PlayerClient>) {
        players.forEach { player ->
            viewModel.updatePlayerPosition(player.id, player.position)
        }
    }

//    fun updateTileOwnership(players: List<PlayerClient>) {
//        viewModel.updateTileOwnership(players)
//    }

    fun showBuyDialog(tilePos: Int, tileName: String) {
        runOnUiThread {
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Buy Property")
                .setMessage("Do you want to buy $tileName at position $tilePos?")
                .setPositiveButton("Buy", null)
                .setNegativeButton("Cancel", null)
                .create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.show()
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
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
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
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


    fun updateTurnView(currentPlayerId: Int, nickname: String) {
        runOnUiThread {
            // Show turn info (could be a Toast, Snackbar, or update a TextView if you add one)
            // For now, just log
            Log.i("GameBoardActivity", "Aktueller Spieler: $nickname (ID: $currentPlayerId)")
        }
    }

    fun updateDice(roll1: Int, roll2: Int) {
        runOnUiThread {
            Log.i("GameBoardActivity", "Dice rolled: $roll1, $roll2")
        }
    }

    fun onRollFinished() {
        // Could be used to re-enable UI, etc.
        Log.i("GameBoardActivity", "Roll finished")
    }

    fun updateTile(tileName: String, tileIndex: Int) {
        runOnUiThread {
            Log.i("GameBoardActivity", "Landed on $tileName ($tileIndex)")
        }
    }

    fun updateTokenPosition(steps: Int) {
        // Not visualized in Compose version, but could be animated
        Log.i("GameBoardActivity", "Token moved $steps steps")
    }

    fun updateCashDisplay(cash: Int) {
        runOnUiThread {
            Log.i("GameBoardActivity", "Cash: $cash")
        }
    }

    fun showGameOverDialog(winnerName: String) {
        runOnUiThread {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("The winner is $winnerName. Return to lobby?")
                .setPositiveButton("Yes") { _, _ ->
                    LobbyClient.lobbyId = -1
                    LobbyClient.lobbyName = ""
                    LobbyClient.playerId = -1
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    fun showDialog(title: String, message: String) {
        runOnUiThread {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    fun showRollPrisonDialog(dice1: Int, dice2: Int) {
        runOnUiThread {
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Prison Roll")
                .setMessage("You rolled $dice1 and $dice2.")
                .setNeutralButton("OK", null)
                .create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.show()
        }
    }

    fun showPayPrisonDialog() {
        runOnUiThread {
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Pay Prison Fee")
                .setMessage("Do you want to pay 50€ to get out of jail?")
                .setPositiveButton("Yes") { _, _ ->
                    val payload = GameController.buildPayload("playerId", myId)
                    gameStomp.sendGameMessage(
                        GameMessage(
                            LobbyClient.lobbyId,
                            GameMessageType.PAY_PRISON,
                            payload
                        )
                    )
                }
                .setNegativeButton("No") { _, _ ->
                    val payload = GameController.buildPayload("playerId", myId)
                    gameStomp.sendGameMessage(
                        GameMessage(
                            LobbyClient.lobbyId,
                            GameMessageType.ROLL_PRISON,
                            payload
                        )
                    )
                }
                .create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.show()
        }
    }

    fun showBuyOptions(tilePos: Int, tileName: String, canBuy: Boolean, canBuildHouse: Boolean, canBuildHotel: Boolean) {
        // In Compose, you would show/hide buttons in the UI state, but for now just log
        Log.i("GameBoardActivity", "Buy options: canBuy=$canBuy, canBuildHouse=$canBuildHouse, canBuildHotel=$canBuildHotel")
    }

    fun enableDiceButton() {
        // In Compose, you would update state to enable the button
        Log.i("GameBoardActivity", "Dice button enabled")
    }

    fun disableDiceButton() {
        // In Compose, you would update state to disable the button
        Log.i("GameBoardActivity", "Dice button disabled")
    }

    fun showPlayerLost(playerId: Int) {
        val nickname = GameStateClient.getNickname(playerId) ?: "A player"
        runOnUiThread {
            showDialog("Bankrupt", "$nickname is bankrupt and out of the game.")
        }
    }

    fun hideActionButtons() {
        // In Compose, you would update state to hide action buttons
        Log.i("GameBoardActivity", "Action buttons hidden")
    }
}

fun tileAt(row: Int, col: Int): ClientTile? {
    val size = 11
    val tiles = ClientBoardMap.tiles

    return when {
        row == size - 1 && col in 0 until size -> tiles.getOrNull(size - col - 1)
        col == 0 && row in 0 until size - 1 -> tiles.getOrNull(2 * size - 2 - row)
        row == 0 && col in 1 until size -> tiles.getOrNull(2 * size - 1 + col - 1)
        col == size - 1 && row in 1 until size - 1 -> tiles.getOrNull(3 * size - 2 + row - 1)
        else -> null
    }
}

fun findTileCoordinates(tileIndex: Int): Pair<Int, Int>? {
    val size = 11
    val correctedIndex = (tileIndex - 1 + 40) % 40 // Fix: show position one less, wrap around
    return when (correctedIndex) {
        in 0..10 -> size - 1 to size - 1 - correctedIndex // bottom
        in 11..19 -> 2 * size - 2 - correctedIndex to 0 // left
        in 20..29 -> 0 to correctedIndex - 20 // top
        in 30..39 -> correctedIndex - 30 to size - 1 // right (fixed: remove +1)
        else -> null
    }
}

@Composable
fun MonopolyTile(tile: ClientTile, ownedByPlayerId: Int?, modifier: Modifier = Modifier) {
    val ownerColor = ownedByPlayerId?.let { playerColors[it] } ?: Color(0xFFF0F0F0)
    Box(
        modifier = modifier
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .background(ownerColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = tile.name,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            if (tile.price != null) {
                Text(
                    text = "${tile.price} \$",
                    fontSize = 8.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun GameBoardScreen(nickname: String, playerId: Int, viewModel: GameBoardViewModel) {
    val playerPositions = viewModel.playerPositions
    val tileOwnership = viewModel.tileOwnership // <-- Use ViewModel's tileOwnership
    val gridSize = 11


    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Welcome, $nickname (ID: $playerId)",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            val tileWidth = maxWidth / gridSize
            val tileHeight = maxHeight / gridSize

            Box {
                Column {
                    repeat(gridSize) { row ->
                        Row {
                            repeat(gridSize) { col ->
                                val tile = tileAt(row, col)
                                when {
                                    row == 5 && col == 5 -> {
                                        Box(
                                            modifier = Modifier
                                                .width(tileWidth)
                                                .height(tileHeight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Button(onClick = {
                                                viewModel.rollDice()
                                            }) {
                                                Text("Roll Dice")
                                            }
                                        }
                                    }

                                    tile != null -> {
                                        MonopolyTile(
                                            tile = tile,
                                            ownedByPlayerId = tileOwnership[tile.index],
                                            modifier = Modifier
                                                .width(tileWidth)
                                                .height(tileHeight)
                                        )
                                    }

                                    else -> {
                                        Spacer(
                                            modifier = Modifier
                                                .width(tileWidth)
                                                .height(tileHeight)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                PlayerTokensOverlay(playerPositions, tileWidth, tileHeight)
            }
        }
    }
}

class GameBoardViewModel : ViewModel() {
    val playerPositions = mutableStateMapOf<Int, Int>()
    var onRollDice: (() -> Unit)? = null

    // Use OwnershipClient.streetOwners directly for tile ownership
    val tileOwnership: Map<Int, Int?>
        get() = OwnershipClient.streetOwners

    fun updatePlayerPosition(playerId: Int, newTile: Int) {
        playerPositions[playerId] = newTile
    }

    fun updatePlayerPositions(players: List<PlayerClient>) {
        players.forEach { player ->
            updatePlayerPosition(player.id, player.position)
        }
    }

    fun rollDice() {
        onRollDice?.invoke()
    }

}


@Composable
fun PlayerTokensOverlay(
    playerPositions: Map<Int, Int>,
    tileWidth: Dp,
    tileHeight: Dp
) {
    Box(modifier = Modifier.fillMaxSize()) {
        for ((playerId, tileIndex) in playerPositions) {
            val coords = findTileCoordinates(tileIndex)
            if (coords != null) {
                val (row, col) = coords
                Box(
                    modifier = Modifier
                        .offset(
                            x = tileWidth * col,
                            y = tileHeight * row
                        )
                        .size(14.dp)
                        .background(
                            color = playerColors[playerId] ?: Color.Black,
                            shape = RoundedCornerShape(50)
                        )
                        .border(1.dp, Color.Black, RoundedCornerShape(50))
                )
            }
        }
    }
}
