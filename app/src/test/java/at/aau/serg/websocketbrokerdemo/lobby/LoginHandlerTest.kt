package at.aau.serg.websocketbrokerdemo.lobby

import android.content.Intent
import android.util.Log
import at.aau.serg.websocketbrokerdemo.LoginHandler
import at.aau.serg.websocketbrokerdemo.UsernameActivity
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.Gson
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

        // Typisierte putExtra-Mock
        every { mockIntent.putExtra(any<String>(), any<String>()) } returns mockIntent

        // Activity-Mocks
        every { activity.intent } returns mockIntent
        every { activity.startActivity(any()) } just Runs
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
}
