package at.aau.serg.websocketbrokerdemo

import android.content.Intent
import at.aau.serg.websocketbrokerdemo.dkt.GameMessage
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.lobby.LobbyHandler
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LobbyHandlerTest {

    private lateinit var handler: LobbyHandler
    private val activity: LobbyActivity = mock()

    @BeforeEach
    fun setup() {
        handler = LobbyHandler(activity)
    }

    @Test
    fun handleLobbyUpdate_setsPlayersAndShowsLobby() {
        val playersList = listOf("Player1", "Player2")
        val payload = JSONObject().apply {
            put("players", JSONArray(playersList))
        }.toString()

        handler.handle(GameMessage("lobby_update", payload))

        verify(activity).showLobby()

        val players = LobbyClient.allPlayers()
        assertEquals(2, players.size)
        assertTrue(players.contains("Player1"))
        assertTrue(players.contains("Player2"))
    }

    @Test
    fun handleUnknownType_doesNotCrash() {
        // Tests the else-branch: unknown message type
        handler.handle(GameMessage("unknown_type", "{}"))
        // No crash = test passes
    }
}