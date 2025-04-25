package at.aau.serg.websocketbrokerdemo.lobby

import android.util.Log
import at.aau.serg.websocketbrokerdemo.LobbyActivity
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LobbyHandlerTest {

    private lateinit var activity: LobbyActivity
    private lateinit var handler: LobbyHandler

    @BeforeEach
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0

        activity = mockk(relaxed = true)
        handler = LobbyHandler(activity)
    }

    @Test
    fun testOnStartGame_startsMainActivityWithOrder() {
        val playerList = listOf(
            PlayerDTO("id1", "Alice"),
            PlayerDTO("id2", "Bob")
        )

        val array = JsonArray().apply {
            playerList.forEach {
                add(JsonObject().apply {
                    addProperty("id", it.id)
                    addProperty("username", it.username)
                })
            }
        }

        val payload = JsonObject().apply {
            add("playerOrder", array)
        }

        handler.onStartGame(payload)
        verify { activity.startGame(match { it.size == 2 && it[0].username == "Alice" }) }
    }

    @Test
    fun testOnLobbyUpdate_updatesLobbyDisplay() {
        val players = listOf(
            PlayerDTO("id1", "Thomas"),
            PlayerDTO("id2", "David")
        )

        handler.onLobbyUpdate(players)
        verify { activity.updateLobby(listOf("Thomas", "David")) }
    }
}
