package at.aau.serg.websocketbrokerdemo.game

import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class GameStateClientTest {

    @BeforeEach
    fun setup() {
        println("Resetting GameStateClient state before test")
        // Reset the state before each test
        GameStateClient.getAllPositions().keys.forEach { player ->
            GameStateClient.updatePosition(player, 0)
        }
        println("State after reset: ${GameStateClient.getAllPositions()}")
    }

    @AfterEach
    fun cleanup() {
        println("Cleaning up GameStateClient state after test")
        GameStateClient.getAllPositions().keys.forEach { player ->
            GameStateClient.updatePosition(player, 0)
        }
        println("State after cleanup: ${GameStateClient.getAllPositions()}")
    }

    @Test
    fun testUpdateAndGetPosition() {
        println("Running testUpdateAndGetPosition")

        // Update position
        GameStateClient.updatePosition("player1", 5)

        // Retrieve and print position
        val result = GameStateClient.getPosition("player1")
        println("Expected: 5, Actual: $result")

        assertEquals(5, result)
    }

    @Test
    fun testDefaultPositionForUnknownPlayer() {
        println("Running testDefaultPositionForUnknownPlayer")

        // Retrieve position for unknown player
        val result = GameStateClient.getPosition("unknown")
        println("Expected: 0, Actual: $result")

        assertEquals(0, result)
    }

    @Test
    fun testAllPositions() {
        println("Running testAllPositions")

        // Update positions
        GameStateClient.updatePosition("playerA", 3)
        GameStateClient.updatePosition("playerB", 7)

        // Retrieve and print all positions
        val all = GameStateClient.getAllPositions()
        println("Expected: playerA -> 3, playerB -> 7, Actual: $all")

        // Assertions
        assertEquals(3, all["playerA"])
        assertEquals(7, all["playerB"])
    }
}
