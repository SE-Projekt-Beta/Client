package at.aau.serg.websocketbrokerdemo.lobby

import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LobbyClientTest {

    @Test
    fun testSetAndGetPlayers() {
        val players = listOf(
            PlayerDTO("id1", "Anna"),
            PlayerDTO("id2", "Ben")
        )

        LobbyClient.setPlayers(players)
        val result = LobbyClient.allPlayers()

        assertEquals(2, result.size)
        assertEquals("Anna", result[0].username)
        assertEquals("Ben", result[1].username)
    }

    @Test
    fun testSetUsername() {
        LobbyClient.username = "Thomas"
        assertEquals("Thomas", LobbyClient.username)
    }
}
