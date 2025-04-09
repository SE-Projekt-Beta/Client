package at.aau.serg.websocketbrokerdemo.lobby

import android.content.Intent
import android.util.Log
import at.aau.serg.websocketbrokerdemo.LobbyActivity
import at.aau.serg.websocketbrokerdemo.MainActivity
import at.aau.serg.websocketbrokerdemo.dkt.GameMessage
import io.mockk.*
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LobbyHandlerTest {
    private lateinit var mockActivity: LobbyActivity
    private lateinit var lobbyHandler: LobbyHandler
    private lateinit var mockIntent: Intent

    @BeforeEach
    fun setUp() {
        // Mock Android Log class
        mockkStatic(Log::class)
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0

        // Mock Intent class
        mockIntent = mockk(relaxed = true)
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().putExtra(any(), any<String>()) } returns mockIntent

        // Mock LobbyActivity
        mockActivity = mockk(relaxed = true)

        // Mock static methods in LobbyClient
        mockkObject(LobbyClient)
        every { LobbyClient.setPlayers(any()) } just Runs
        every { LobbyClient.playerName } returns "TestPlayer"

        lobbyHandler = LobbyHandler(mockActivity)
    }

    @Test
    fun testHandleLobbyUpdate() {
        // Create simple JSON with players
        val jsonPayload = """{"players":["Player1","Player2"]}"""
        val message = GameMessage("lobby_update", jsonPayload)

        // Execute
        lobbyHandler.handle(message)

        // Verify
        verify { Log.i("LobbyHandler", "Empfange Nachricht: lobby_update") }
        verify { LobbyClient.setPlayers(listOf("Player1", "Player2")) }
        verify { mockActivity.showLobby() }
    }

    @Test
    fun testHandleStartGame() {
        // Setup
        val message = GameMessage("start_game", "")

        // Execute
        lobbyHandler.handle(message)

        // Verify
        verify { Log.i("LobbyHandler", "Empfange Nachricht: start_game") }
        verify { anyConstructed<Intent>().putExtra("PLAYER_NAME", "TestPlayer") }
        verify { mockActivity.startActivity(any()) }
        verify { mockActivity.finish() }
    }

    @Test
    fun testHandleUnknownMessage() {
        // Execute
        lobbyHandler.handle(GameMessage("unknown_type", ""))

        // Verify warning is logged
        verify { Log.i("LobbyHandler", "Empfange Nachricht: unknown_type") }
        verify { Log.w("LobbyHandler", "Unbekannter Nachrichtentyp: unknown_type") }

        // Verify no other actions are taken
        verify(exactly = 0) { mockActivity.showLobby() }
        verify(exactly = 0) { mockActivity.startActivity(any()) }
        verify(exactly = 0) { mockActivity.finish() }
    }
}
