package at.aau.serg.websocketbrokerdemo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp


// Dummy player colors map (playerId -> Color)
val playerColors = mapOf(
    1 to Color.Red,
    2 to Color.Blue,
    3 to Color.Green,
    4 to Color.Magenta
)

// Example ownership: tile index -> playerId
val tileOwnership = mapOf(
    1 to 1,
    2 to 3,
    4 to 2,
    5 to 1,
    10 to 4,
    39 to 2,
    40 to 3
)

class GameBoardActivity : ComponentActivity() {

    private var myId = -1
    private lateinit var myNickname: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myNickname = LobbyClient.username
        myId = LobbyClient.playerId

        val playersJson = intent.getStringExtra("players_json")
        if (playersJson != null) {
            val type = object : TypeToken<List<PlayerClient>>() {}.type
            val players = Gson().fromJson<List<PlayerClient>>(playersJson, type)
            players.forEach { GameStateClient.players[it.id] = it }
        }

        // Network setup removed for brevity or add here as before

        setContent {
            GameBoardScreen(myNickname, myId)
        }
    }

    // Empty placeholder functions (as requested)
    fun updateTestView(message: String) {}
    fun updateTurnView(currentPlayerId: Int, nickname: String) {}
    fun updateDice(roll1: Int, roll2: Int) {}
    fun onRollFinished() {}
    fun updateTile(tileName: String, tileIndex: Int) {}
    fun updateTokenPosition(steps: Int) {}
    fun updateCashDisplay(cash: Int) {}
    fun showGameOverDialog(winnerName: String) {}
    fun showDialog(title: String, message: String) {}
    fun showBuyDialog(tilePos: Int, tileName: String) {}
    fun showRollPrisonDialog(dice1: Int, dice2: Int) {}
    fun showPayPrisonDialog() {}
    fun showBuyOptions(tilePos: Int, tileName: String, canBuy: Boolean, canBuildHouse: Boolean, canBuildHotel: Boolean) {}
    fun enableDiceButton() {}
    fun disableDiceButton() {}
    fun showPlayerLost(playerId: Int) {}
    fun hideActionButtons() {}
}

val tileSizeDp = 60.dp

fun tileAt(row: Int, col: Int): ClientTile? {
    val size = 11
    val tiles = ClientBoardMap.tiles

    // Bottom row, right to left (Start at [size-1][size-1] to [size-1][0])
    if (row == size - 1 && col in 0 until size) {
        return tiles.getOrNull(size - col - 1)
    }

    // Left column, bottom to top (after bottom row, [size-2][0] to [0][0])
    if (col == 0 && row in 0 until size - 1) {
        return tiles.getOrNull(size + row)
    }

    // Top row, left to right ([0][1] to [0][size-1])
    if (row == 0 && col in 1 until size) {
        return tiles.getOrNull(2 * size - 1 + col - 1)
    }

    // Right column, top to bottom ([1][size-1] to [size-2][size-1])
    if (col == size - 1 && row in 1 until size - 1) {
        return tiles.getOrNull(3 * size - 2 + row - 1)
    }

    return null
}


@Composable
fun MonopolyTile(
    tile: ClientTile,
    ownedByPlayerId: Int?,
    modifier: Modifier = Modifier
) {
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
fun GameBoardScreen(nickname: String, playerId: Int) {
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
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(gridSize) { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(gridSize) { col ->
                            val tile = tileAt(row, col)
                            when {
                                row == 5 && col == 5 -> {
                                    // Center: Roll Dice button
                                    Box(
                                        modifier = Modifier
                                            .width(tileWidth)
                                            .height(tileHeight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Button(onClick = { /* TODO: roll dice */ }) {
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
        }
    }
}

