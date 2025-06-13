package at.aau.serg.websocketbrokerdemo.game

import android.graphics.Color
import androidx.core.graphics.toColorInt

object ColorManager {
    private val predefinedColors = listOf(
        "#4D00a8f3", //blue
        "#4D5ae74b", //green
        "#4Dec1c24", //red
        "#4Dff7f27", //orange
        "#4Db83dba", //purple
        "#4Dffaec8"  //pink
    ).map { it.toColorInt() }

    // Zuweisung Spieler-ID -> Farbe
    val playerColors: MutableMap<Int, Int> = mutableMapOf()

    fun assignColorsToPlayers(playerIds: List<Int>) {
        playerColors.clear()
        playerIds.forEachIndexed { index, playerId ->
            val color = predefinedColors.getOrNull(index) ?: Color.TRANSPARENT
            playerColors[playerId] = color
        }
    }
}