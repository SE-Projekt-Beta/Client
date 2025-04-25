package at.aau.serg.websocketbrokerdemo.game

import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameStateClientTest {

    @BeforeEach
    fun setup() {
        // Rücksetzen des Zustands vor jedem Test
        GameStateClient.getAllPositions().keys.forEach { player ->
            GameStateClient.updatePosition(player, 0)
        }
    }

    @Test
    fun testUpdateAndGetPosition() {
        GameStateClient.updatePosition("player1", 5)
        val result = GameStateClient.getPosition("player1")
        assertEquals(5, result)
    }

    @Test
    fun testDefaultPositionForUnknownPlayer() {
        val result = GameStateClient.getPosition("unknown")
        assertEquals(0, result)
    }

    @Test
    fun testAllPositions() {
        GameStateClient.updatePosition("playerA", 3)
        GameStateClient.updatePosition("playerB", 7)

        val all = GameStateClient.getAllPositions()
        assertEquals(3, all["playerA"])
        assertEquals(7, all["playerB"])
    }
}