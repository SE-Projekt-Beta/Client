package at.aau.serg.websocketbrokerdemo.game

import android.graphics.Color
import androidx.core.graphics.toColorInt

object ColorManager {
    private val predefinedColors = listOf(
        "#6600a8f3", //blue
        "#665ae74b", //green
        "#66ec1c24", //red
        "#66ff7f27", //orange
        "#66b83dba", //purple
        "#66ffaec8"  //pink
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