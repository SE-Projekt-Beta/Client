package at.aau.serg.websocketbrokerdemo.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class GameStateClientTest {

    @BeforeEach
    fun setup() {
        // Clear state before each test
        GameStateClient.clear()
    }

    @Test
    fun testDefaultPositionForUnknown() {
        // Unknown players should start at position 0
        assertEquals(0, GameStateClient.getPosition("unknownPlayer"))
    }

    @Test
    fun testUpdateAndGetPosition() {
        // After updating, getPosition should return the new value
        GameStateClient.updatePosition("playerX", 5)
        assertEquals(5, GameStateClient.getPosition("playerX"))
    }

    @Test
    fun testAllPositions() {
        // Updating multiple players and retrieving the full map
        GameStateClient.updatePosition("playerA", 3)
        GameStateClient.updatePosition("playerB", 7)

        val all = GameStateClient.getAllPositions()
        assertEquals(3, all["playerA"])
        assertEquals(7, all["playerB"])
    }
}
