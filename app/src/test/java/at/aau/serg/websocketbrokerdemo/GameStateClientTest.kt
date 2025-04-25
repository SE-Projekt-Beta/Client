package at.aau.serg.websocketbrokerdemo

import at.aau.serg.websocketbrokerdemo.game.GameStateClient
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test

class GameStateClientTest {
    @Test
    fun testUpdateRetrievePosition() {
        GameStateClient.updatePosition("player1", 7)
        assertEquals(7, GameStateClient.getPosition("player1"))
    }

    @Test
    fun testDefaultPositionUnknownPlayer() {
        assertEquals(0, GameStateClient.getPosition("unknown"))
    }
}