package at.aau.serg.websocketbrokerdemo.game

import android.graphics.PointF
import android.util.Log
import android.view.View
import android.widget.ImageView
import at.aau.serg.websocketbrokerdemo.GameBoardActivity
import at.aau.serg.websocketbrokerdemo.R
import at.aau.serg.websocketbrokerdemo.model.ClientBoardMap

class PlayerTokenManager(private val gameBoardActivity: GameBoardActivity) {

    private val playerTokens: List<ImageView> = listOf(
        gameBoardActivity.findViewById(R.id.player_token1),
        gameBoardActivity.findViewById(R.id.player_token2),
        gameBoardActivity.findViewById(R.id.player_token3),
        gameBoardActivity.findViewById(R.id.player_token4),
        gameBoardActivity.findViewById(R.id.player_token5),
        gameBoardActivity.findViewById(R.id.player_token6)
    )

    private val playerIdToTokenIndex = mutableMapOf<Int, Int>()

    private var boardWidth: Float = 0f
    private var boardHeight: Float = 0f
    private val originalBoardWidth = 2048f
    private val originalBoardHeight = 2048f

    fun setBoardSize(width: Float, height: Float) {
        this.boardWidth = width
        this.boardHeight = height
        Log.d("TokenDebug", "setBoardSize was executed")
    }

    private fun scalePosition(original: PointF): PointF {
        val scaleX = boardWidth/originalBoardWidth
        val scaleY = boardHeight/originalBoardHeight
        return PointF(original.x*scaleX, original.y*scaleY)
    }

    fun positionTokensOnStartTile() {
        val startTile = ClientBoardMap.getTile(1) ?: return

        gameBoardActivity.runOnUiThread {
            GameStateClient.players.values.forEachIndexed { index, player ->
                val token = playerTokens.getOrNull(index) ?: return@forEachIndexed
                playerIdToTokenIndex[player.id] = index // Mappe playerId -> token index

                val scaledPos = scalePosition(startTile.position!!)
                token.visibility = View.VISIBLE
                token.x = scaledPos.x
                token.y = scaledPos.y

                Log.d("TokenDebug", "Placing token at start x=${scaledPos.x}, y=${scaledPos.y}")
            }
        }
    }

    // Funktion, um einen Spieler nach dem Würfeln zu verschieben
    fun movePlayerToken(playerId: Int, steps: Int) {
        val player = GameStateClient.players[playerId] ?: return
        val newPosition = player.position % 40
        val tileIndex = if (newPosition == 0) 40 else newPosition

        val tile = ClientBoardMap.getTile(tileIndex) ?: return

        Log.d("TokenDebug", "Trying to move playerId=$playerId to tile=$tileIndex")

        // Hole das Token des Spielers
        val tokenIndex = playerIdToTokenIndex[playerId] ?: run {
            Log.w("TokenDebug", "No token index found for playerId=$playerId")
            return
        }

        val token = playerTokens.getOrNull(tokenIndex) ?: run {
            Log.w("TokenDebug", "No token view found for index=$tokenIndex")
            return
        }

        gameBoardActivity.runOnUiThread {
            val scaledPos = scalePosition(tile.position!!)
            token.x = scaledPos.x
            token.y = scaledPos.y
            Log.d("TokenDebug", "Placing token at scaled x=${scaledPos.x}, y=${scaledPos.y}")
        }

        player.position = newPosition
    }

    // Set a player's token to an exact board position (used for syncing with game state)
    fun setPlayerTokenPosition(playerId: Int, position: Int) {
        val tileIndex = if (position % 40 == 0) 40 else position % 40
        val tile = ClientBoardMap.getTile(tileIndex) ?: return

        // Hole das Token des Spielers
        val tokenIndex = playerIdToTokenIndex[playerId] ?: run {
            Log.w("TokenDebug", "No token index found for playerId=$playerId")
            return
        }
        val token = playerTokens.getOrNull(tokenIndex) ?: run {
            Log.w("TokenDebug", "No token view found for index=$tokenIndex")
            return
        }
        gameBoardActivity.runOnUiThread {
            val scaledPos = scalePosition(tile.position!!)
            token.x = scaledPos.x
            token.y = scaledPos.y
            Log.d("TokenDebug", "Set token for playerId=$playerId at scaled x=${scaledPos.x}, y=${scaledPos.y}")
        }
    }
}