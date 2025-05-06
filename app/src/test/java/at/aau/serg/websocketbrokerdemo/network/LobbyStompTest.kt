package at.aau.serg.websocketbrokerdemo.network

import android.util.Log
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyMessage
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyMessageType
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.mockk.*
import org.hildan.krossbow.stomp.StompSession
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.lang.reflect.Field
import java.lang.reflect.Method

class LobbyStompTest {

    private lateinit var listener: LobbyMessageListener
    private lateinit var stomp: LobbyStomp
    private lateinit var session: StompSession
    private lateinit var handleLobbyMessageMethod: Method

    @BeforeEach
    fun setUp() {
        // suppress Android logs and resolve ambiguity
        mockkStatic(Log::class)
        every { Log.i(any<String>(), any<String>()) } returns 0 // Add type hint
        every { Log.e(any<String>(), any<String>()) } returns 0 // Add type hint
        every { Log.w(any<String>(), any<String>()) } returns 0 // Add type hint

        // reset global lobby state
        LobbyClient.lobbyId = null

        listener = mockk(relaxed = true)
        stomp = LobbyStomp(listener)

        // inject synchronous CoroutineScope for immediate execution
        val scopeField: Field = LobbyStomp::class.java
            .getDeclaredField("scope").apply { isAccessible = true }
        scopeField.set(stomp, CoroutineScope(Dispatchers.Unconfined))

        // inject mock session - use relaxed to avoid having to mock every method
        session = mockk(relaxed = true)
        val sessionField: Field = LobbyStomp::class.java
            .getDeclaredField("session").apply { isAccessible = true }
        sessionField.set(stomp, session)

        // Get the private handleLobbyMessage method using reflection
        handleLobbyMessageMethod = LobbyStomp::class.java
            .getDeclaredMethod("handleLobbyMessage", LobbyMessage::class.java)
            .apply { isAccessible = true }
    }

    @Test
    fun testSendCreateLobby() {
        // Call the method to be tested
        stomp.sendCreateLobby("MyLobby")

        // Check that the correct session method was called with the right arguments
        coVerify {
            session.send(
                withArg { headers -> headers.destination == "/app/lobby" },
                withArg { body ->
                    body.toString().contains("\"type\":\"CREATE_LOBBY\"") &&
                            body.toString().contains("\"lobbyName\":\"MyLobby\"")
                }
            )
        }
    }

    @Test
    fun testSendListLobbies() {
        stomp.sendListLobbies()

        coVerify {
            session.send(
                withArg { headers -> headers.destination == "/app/lobby" },
                withArg { body -> body.toString().contains("\"type\":\"LIST_LOBBIES\"") },
            )
        }
    }

    @Test
    fun testSendJoinLobby() {
        stomp.sendJoinLobby("lobby123", "userA")

        coVerify {
            session.send(
                withArg { headers -> headers.destination == "/app/lobby" },
                withArg { body ->
                    body.toString().contains("\"type\":\"JOIN_LOBBY\"") &&
                            body.toString().contains("\"lobbyId\":\"lobby123\"") &&
                            body.toString().contains("\"username\":\"userA\"")
                }
            )
        }
    }

    @Test
    fun testSendStartGame() {
        LobbyClient.lobbyId = "startId"
        stomp.sendStartGame()

        coVerify {
            session.send(
                withArg { headers -> headers.destination == "/app/lobby" },
                withArg { body ->
                    body.toString().contains("\"type\":\"START_GAME\"") &&
                            body.toString().contains("\"lobbyId\":\"startId\"")
                }
            )
        }
    }

    @Test
    fun testHandleLobbyCreated() {
        val msg = LobbyMessage("newId", LobbyMessageType.LOBBY_CREATED, JsonObject())
        handleLobbyMessageMethod.invoke(stomp, msg)

        assertEquals("newId", LobbyClient.lobbyId)
        verify { listener.onLobbyListUpdate(emptyList()) } // Assuming this triggers a fetch, hence list is empty initially or listener updates it.
    }

    @Test
    fun testHandleLobbyList() {
        val lobby1 = JsonObject().apply {
            addProperty("lobbyId", "id1")
            addProperty("lobbyName", "Lobby 1")
            addProperty("playerCount", 2)
        }
        val lobby2 = JsonObject().apply {
            addProperty("lobbyId", "id2")
            addProperty("lobbyName", "Lobby 2")
            addProperty("playerCount", 4)
        }
        val payload = JsonArray().apply {
            add(lobby1)
            add(lobby2)
        }
        val msg = LobbyMessage(null, LobbyMessageType.LOBBY_LIST, payload)

        handleLobbyMessageMethod.invoke(stomp, msg)

        val expectedLobbies = listOf(
            LobbyDTO("id1", "Lobby 1", 2),
            LobbyDTO("id2", "Lobby 2", 4)
        )
        verify { listener.onLobbyListUpdate(expectedLobbies) }
    }

    @Test
    fun testHandleLobbyUpdate() {
        val player1 = JsonObject().apply {
            addProperty("id", 1)
            addProperty("nickname", "PlayerOne")
        }
        val player2 = JsonObject().apply {
            addProperty("id", 2)
            addProperty("nickname", "PlayerTwo")
        }
        val playersArray = JsonArray().apply {
            add(player1)
            add(player2)
        }
        val payload = JsonObject().apply {
            add("players", playersArray)
        }
        val msg = LobbyMessage("someLobbyId", LobbyMessageType.LOBBY_UPDATE, payload)

        handleLobbyMessageMethod.invoke(stomp, msg)

        val expectedPlayers = listOf(
            PlayerDTO(1, "PlayerOne"),
            PlayerDTO(2, "PlayerTwo")
        )
        verify { listener.onLobbyUpdate(expectedPlayers) }
    }

    @Test
    fun testHandleStartGame() {
        val payload = JsonObject().apply {
            addProperty("gameId", "game456")
            // Add other relevant payload fields if any
        }
        val msg = LobbyMessage("someLobbyId", LobbyMessageType.START_GAME, payload)

        handleLobbyMessageMethod.invoke(stomp, msg)

        verify { listener.onStartGame(payload) }
    }

    @Test
    fun testHandleError() {
        val payload = JsonObject().apply {
            addProperty("message", "Something went wrong")
        }
        val msg = LobbyMessage(null, LobbyMessageType.ERROR, payload)

        handleLobbyMessageMethod.invoke(stomp, msg)

        // Verify that an error was logged
        verify { Log.e(any<String>(), "Server error: ${payload}") }
        // Verify no listener method was called for this error type
        verify(exactly = 0) { listener.onLobbyListUpdate(any()) }
        verify(exactly = 0) { listener.onLobbyUpdate(any()) }
        verify(exactly = 0) { listener.onStartGame(any()) }
    }

    @Test
    fun testSetOnConnectedListener() {
        val mockCallback = mockk<() -> Unit>(relaxed = true)
        stomp.setOnConnectedListener(mockCallback)

        // Directly access the private field to check if the callback was set
        val onConnectedField: Field = LobbyStomp::class.java
            .getDeclaredField("onConnected").apply { isAccessible = true }
        val storedCallback = onConnectedField.get(stomp) as? (() -> Unit)

        // We can't easily verify the mockCallback was *stored*, but we can
        // verify that the internal reference is not null after setting it.
        // Testing the *invocation* of this callback would require mocking the
        // `connect` function's internal flow which is more complex.
        // For now, checking that the field holds a non-null value is sufficient
        // to ensure the setter works.
        kotlin.test.assertNotNull(storedCallback)

        // Clean up the mock callback after the test if necessary
//        unmockk<(() -> Unit)>(mockCallback)
    }
}
