package at.aau.serg.websocketbrokerdemo.game

import android.graphics.PointF
import android.view.View
import at.aau.serg.websocketbrokerdemo.GameBoardActivity
import at.aau.serg.websocketbrokerdemo.model.ClientBoardMap
import android.graphics.Color

class OwnershipOverlayManager(private val gameBoardActivity: GameBoardActivity) {

    private var boardWidth: Float = 0f
    private var boardHeight: Float = 0f
    private val originalBoardWidth = 2048f
    private val originalBoardHeight = 2048f

    fun setBoardSize(width: Float, height: Float) {
        boardWidth = width
        boardHeight = height
    }

    private fun scalePosition(original: PointF): PointF {
        val scaleX = boardWidth / originalBoardWidth
        val scaleY = boardHeight / originalBoardHeight
        return PointF(original.x * scaleX, original.y * scaleY)
    }

    fun updateOwnershipOverlays(tileOverlays: Map<Int, View>) {
        for ((tileIndex, overlayView) in tileOverlays) {
            val tile = ClientBoardMap.getTile(tileIndex)
            val ownerId = OwnershipClient.getOwnerId(tileIndex)

            if (tile?.position != null && ownerId != null) {
                val pos = scalePosition(tile.position!!)
                val color = ColorManager.playerColors[ownerId] ?: Color.TRANSPARENT

                gameBoardActivity.runOnUiThread {
                    overlayView.setBackgroundColor(color)
                    overlayView.x = pos.x
                    overlayView.y = pos.y
                    overlayView.visibility = View.VISIBLE
                }
            } else {
                gameBoardActivity.runOnUiThread {
                    overlayView.visibility = View.GONE
                }
            }
        }
    }
}
