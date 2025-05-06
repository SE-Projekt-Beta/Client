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
        // Mock the static Log class to suppress log output
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0

        // Mock the LobbyActivity
        activity = mockk(relaxed = true)
        handler = LobbyHandler(activity)
    }

    @Test
    fun testOnStartGame_startsMainActivityWithCorrectOrder() {
        // Arrange: Create the list of players
        val playerList = listOf(
            PlayerDTO(1, "Alice"),
            PlayerDTO(2, "Bob")
        )

        val orderArray = JsonArray().apply {
            playerList.forEach { player ->
                add(JsonObject().apply {
                    addProperty("id", player.id)
                    addProperty("nickname", player.nickname)
                })
            }
        }

        val payload = JsonObject().apply {
            add("playerOrder", orderArray)
        }

        // Act: Call onStartGame
        handler.onStartGame(payload)

        // Assert: Verify that startGame was called with the correct order
        verify {
            activity.startGame(match {
                it.size == 2 &&
                        it[0].id == 1 && it[0].nickname == "Alice" &&
                        it[1].id == 2 && it[1].nickname == "Bob"
            })
        }
    }

    @Test
    fun testOnLobbyUpdate_updatesLobbyCorrectly() {
        // Arrange: Create the list of players
        val players = listOf(
            PlayerDTO(1, "Thomas"),
            PlayerDTO(2, "David")
        )

        // Log the players to ensure the correct list
        println("Players before calling onLobbyUpdate: ${players.map { it.nickname }}")

        // Act: Call onLobbyUpdate (this should invoke the overridden method in LobbyActivity)
        handler.onLobbyUpdate(players)

        // Assert: Verify that updateLobby was called with the correct list of player names
        verify {
            activity.updateLobby(listOf("Thomas", "David"))
        }
    }
}
