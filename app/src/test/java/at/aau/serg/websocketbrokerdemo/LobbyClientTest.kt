package at.aau.serg.websocketbrokerdemo

import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test

class LobbyClientTest {
    @Test
    fun testSetAndRetrievePlayers() {
        val players = listOf("Player1", "Player2")
        LobbyClient.setPlayers(players)

        val result = LobbyClient.allPlayers()
        assertEquals(2, result.size)
        assertEquals("Player1", result[0])
        assertEquals("Player2", result[1])
    }
}