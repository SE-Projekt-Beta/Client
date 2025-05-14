package at.aau.serg.websocketbrokerdemo.lobby

import android.content.Intent
import android.util.Log
import at.aau.serg.websocketbrokerdemo.LoginHandler
import at.aau.serg.websocketbrokerdemo.UsernameActivity
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoginHandlerTest {

    private lateinit var activity: UsernameActivity
    private lateinit var handler: LoginHandler

    @BeforeEach
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0

        // Mocks
        activity = mockk(relaxed = true)
        val mockIntent = mockk<Intent>(relaxed = true)

        // Mock putExtra method for all required types
        every { mockIntent.putExtra(any<String>(), any<String>()) } returns mockIntent
        every { mockIntent.putExtra(any<String>(), any<Int>()) } returns mockIntent
        every { mockIntent.putExtra(any<String>(), any<Boolean>()) } returns mockIntent

        // Mock startActivity with the mocked Intent
        every { activity.startActivity(mockIntent) } just Runs

        // Activity-Mocks
        every { activity.intent } returns mockIntent
        every { activity.finish() } just Runs

        handler = LoginHandler(activity)
    }

    @Test
    fun testOnLobbyUpdate_userNotInList_doesNothing() {
        // Arrange
        LobbyClient.username = "Unknown Player"
        val players = listOf(PlayerDTO(1, "Player 2"))

        // Act
        handler.onLobbyUpdate(players)

        // Assert
        verify(exactly = 0) { activity.startActivity(any()) }
        verify(exactly = 0) { activity.finish() }
    }

    @Test
    fun onLobbyListUpdate_userLobbyFound_startsLobbyActivity() {
        // Arrange
        LobbyClient.username = "Player 1"
        val lobbies = listOf(
            LobbyDTO(1, name = "Lobby for Player 1", playerCount = 5),
            LobbyDTO(2, name = "Another Lobby", playerCount = 3)
        )

        // Act
        handler.onLobbyListUpdate(lobbies)

        // Assert
        verify { activity.startActivity(any()) }
        verify { activity.finish() }
    }

    @Test
    fun onLobbyListUpdate_userLobbyNotFound_doesNothing() {
        // Arrange
        LobbyClient.username = "Player 1"
        val lobbies = listOf(
            LobbyDTO(1, name = "Lobby for Player 2", playerCount = 4),
            LobbyDTO(2, name = "Another Lobby", playerCount = 3)
        )

        // Act
        handler.onLobbyListUpdate(lobbies)

        // Assert
        verify(exactly = 0) { activity.startActivity(any()) }
        verify(exactly = 0) { activity.finish() }
    }

    @Test
    fun onLobbyUpdate_userInList_startsLobbyActivity() {
        // Arrange
        LobbyClient.username = "Player 1"
        val players = listOf(PlayerDTO(1, "Player 1"), PlayerDTO(2, "Player 2"))

        // Act
        handler.onLobbyUpdate(players)

        // Assert
        verify { activity.startActivity(any()) }
        verify { activity.finish() }
        assert(LobbyClient.playerId == 1)
    }

    @Test
    fun onLobbyUpdate_userNotInList_doesNothing() {
        // Arrange
        LobbyClient.username = "Player 3"
        val players = listOf(PlayerDTO(1, "Player 1"), PlayerDTO(2, "Player 2"))

        // Act
        handler.onLobbyUpdate(players)

        // Assert
        verify(exactly = 0) { activity.startActivity(any()) }
        verify(exactly = 0) { activity.finish() }
    }

}
