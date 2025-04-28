package at.aau.serg.websocketbrokerdemo.lobby

import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LobbyClientTest {

    @Test
    fun testSetAndGetPlayers() {
        val players = listOf(
            PlayerDTO(1, "Anna"),
            PlayerDTO(2, "Ben")
        )

        LobbyClient.setPlayers(players)
        val result = LobbyClient.allPlayers()

        assertEquals(2, result.size)
        assertEquals("Anna", result[0].nickname)
        assertEquals("Ben", result[1].nickname)
    }

    @Test
    fun testSetUsername() {
        LobbyClient.username = "Thomas"
        assertEquals("Thomas", LobbyClient.username)
    }
}
