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

        activity = mockk(relaxed = true)
        handler = LoginHandler(activity)
    }

    @Test
    fun testOnLobbyUpdate_userFound_startsLobbyActivity() {
        // Vorbereitung
        LobbyClient.username = "Thomas"
        val players = listOf(
            PlayerDTO("id1", "Thomas"),
            PlayerDTO("id2", "Eva")
        )

        handler.onLobbyUpdate(players)

        verify { activity.startActivity(withArg { intent ->
            val json = intent.getStringExtra("players_json")
            val deserialized = Gson().fromJson(json, Array<PlayerDTO>::class.java).toList()
            assert(deserialized.any { it.username == "Thomas" })
        }) }

        verify { activity.finish() }
    }

    @Test
    fun testOnLobbyUpdate_userNotInList_doesNothing() {
        LobbyClient.username = "Unbekannt"
        val players = listOf(PlayerDTO("id1", "Eva"))

        handler.onLobbyUpdate(players)

        verify(exactly = 0) { activity.startActivity(any()) }
        verify(exactly = 0) { activity.finish() }
    }
}
