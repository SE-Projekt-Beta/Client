package at.aau.serg.websocketbrokerdemo.game

import android.view.View
import at.aau.serg.websocketbrokerdemo.GameBoardActivity
import android.graphics.Color

class OwnershipOverlayManager(private val gameBoardActivity: GameBoardActivity) {

    fun updateOwnershipOverlays(tileOverlays: Map<Int, View>) {
        for ((tileIndex, overlayView) in tileOverlays) {
            val ownerId = OwnershipClient.getOwnerId(tileIndex)
            val color = ColorManager.playerColors[ownerId] ?: Color.TRANSPARENT

            gameBoardActivity.runOnUiThread {
                if (ownerId != null) {
                    overlayView.setBackgroundColor(color)
                    overlayView.visibility = View.VISIBLE
                } else {
                    overlayView.visibility = View.GONE
                }
            }
        }
    }
}
