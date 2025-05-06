package at.aau.serg.websocketbrokerdemo.lobby

import android.content.Intent
import android.util.Log
import at.aau.serg.websocketbrokerdemo.LobbyActivity
import at.aau.serg.websocketbrokerdemo.LoginHandler
import at.aau.serg.websocketbrokerdemo.UsernameActivity
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class LoginHandlerTest {

    private lateinit var activity: UsernameActivity
    private lateinit var handler: LoginHandler
    private lateinit var mockIntent: Intent
    private val intentSlot = slot<Intent>()
    private val gson = Gson()

    @BeforeEach
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0

        // Mock the LobbyClient
        mockkObject(LobbyClient)
        every { LobbyClient.setPlayers(any()) } just Runs

        // Mocks
        activity = mockk(relaxed = true)
        mockIntent = mockk(relaxed = true)

        // Activity-Mocks
        every { activity.intent } returns mockIntent
        every { activity.startActivity(capture(intentSlot)) } just Runs
        every { activity.finish() } just Runs

        handler = LoginHandler(activity)
    }

    @Test
    fun testOnLobbyUpdate_userNotInList_doesNothing() {
        // Arrange
        every { LobbyClient.username } returns "Unknown Player"
        val players = listOf(PlayerDTO(1, "Player 2"))

        // Act
        handler.onLobbyUpdate(players)

        // Assert
        verify(exactly = 0) { activity.startActivity(any()) }
        verify(exactly = 0) { activity.finish() }
    }

    @Test
    fun testOnLobbyListUpdate_lobbyNotFound_doesNothing() {
        // Arrange
        every { LobbyClient.username } returns "TestUser"
        val lobbies = listOf(
            LobbyDTO("Other Lobby", "active", 0),
            LobbyDTO("Another Lobby", "active", 0)
        )

        // Act
        handler.onLobbyListUpdate(lobbies)

        // Assert
        verify(exactly = 0) { activity.startActivity(any()) }
        verify(exactly = 0) { activity.finish() }
    }

    @Test
    fun testOnStartGame_doesNothing() {
        // Arrange
        val jsonObject = JsonObject()

        // Act
        handler.onStartGame(jsonObject)

        // Assert - since the method is empty, verify no interactions with activity
        verify(exactly = 0) { activity.startActivity(any()) }
        verify(exactly = 0) { activity.finish() }
    }

    @Test
    fun testOnLobbyListUpdate_withEmptyList_doesNothing() {
        // Arrange
        every { LobbyClient.username } returns "TestUser"
        val lobbies = emptyList<LobbyDTO>()

        // Act
        handler.onLobbyListUpdate(lobbies)

        // Assert
        verify(exactly = 0) { activity.startActivity(any()) }
        verify(exactly = 0) { activity.finish() }
    }

    @Test
    fun testOnLobbyUpdate_withEmptyList_doesNothing() {
        // Arrange
        every { LobbyClient.username } returns "TestUser"
        val players = emptyList<PlayerDTO>()

        // Act
        handler.onLobbyUpdate(players)

        // Assert
        verify(exactly = 0) { activity.startActivity(any()) }
        verify(exactly = 0) { activity.finish() }
    }
}
