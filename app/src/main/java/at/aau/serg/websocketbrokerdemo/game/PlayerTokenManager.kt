package at.aau.serg.websocketbrokerdemo.game

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

    fun positionTokensOnStartTile() {
        val startTile = ClientBoardMap.getTile(1) ?: return

        gameBoardActivity.runOnUiThread {
            playerTokens.forEachIndexed { index, token ->
                val player = GameStateClient.players.values.elementAtOrNull(index)
                player?.let {
                    token.visibility = ImageView.VISIBLE
                    token.translationX = startTile.position!!.x
                    token.translationY = startTile.position.y
                }
            }
        }
    }

    // Funktion, um einen Spieler nach dem Würfeln zu verschieben
    fun movePlayerToken(playerId: Int, steps: Int) {
        val player = GameStateClient.players[playerId] ?: return
        val newPosition = player.position + steps

        val tile = ClientBoardMap.getTile(newPosition) ?: return

        // Hole das Token des Spielers
        val token = playerTokens.getOrNull(playerId)
        token?.let {
            gameBoardActivity.runOnUiThread {
                it.translationX = tile.position!!.x
                it.translationY = tile.position.y
            }
        }

        player.position = newPosition
    }
}