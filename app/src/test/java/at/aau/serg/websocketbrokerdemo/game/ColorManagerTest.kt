package at.aau.serg.websocketbrokerdemo.game

import org.junit.jupiter.api.Test
import android.graphics.Color
import org.junit.Assert.assertEquals

class ColorManagerTest {

    @Test
    fun assignColorsToPlayersassignspredefinedcolorstoplayerIDs() {
        val playerIds = listOf(1, 2, 3, 4, 5, 6)
        ColorManager.assignColorsToPlayers(playerIds)

        assertEquals(ColorManager.playerColors[1], Color.parseColor("#6600a8f3"))
        assertEquals(ColorManager.playerColors[2], Color.parseColor("#665ae74b"))
        assertEquals(ColorManager.playerColors[3], Color.parseColor("#66ec1c24"))
        assertEquals(ColorManager.playerColors[4], Color.parseColor("#66ff7f27"))
        assertEquals(ColorManager.playerColors[5], Color.parseColor("#66b83dba"))
        assertEquals(ColorManager.playerColors[6], Color.parseColor("#66ffaec8"))
    }

    @Test
    fun `assignColorsToPlayers assigns transparent color when predefined colors are exhausted`() {
        val playerIds = listOf(1, 2, 3, 4, 5, 6, 7)
        ColorManager.assignColorsToPlayers(playerIds)

        assertEquals(ColorManager.playerColors[7], Color.TRANSPARENT)
    }
}
