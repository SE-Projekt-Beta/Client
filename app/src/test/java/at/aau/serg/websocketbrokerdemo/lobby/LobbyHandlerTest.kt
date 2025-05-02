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
    fun testOnStartGame_startsMainActivityWithCorrectOrder() {
        // Arrange: Baue ein passendes Payload
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

        // Act
        handler.onStartGame(payload)

        // Assert: Überprüfe, dass startGame aufgerufen wurde
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
        // Arrange: Erstelle eine Spieler-Liste
        val players = listOf(
            PlayerDTO(1, "Thomas"),
            PlayerDTO(2, "David")
        )

        // Act
        handler.onLobbyListUpdate(players)

        // Assert: Überprüfe, dass updateLobby aufgerufen wurde
        verify {
            activity.updateLobby(listOf("Thomas", "David"))
        }
    }
}
