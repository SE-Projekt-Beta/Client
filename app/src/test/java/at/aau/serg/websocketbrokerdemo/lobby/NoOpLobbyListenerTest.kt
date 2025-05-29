import at.aau.serg.websocketbrokerdemo.NoOpLobbyListener
import at.aau.serg.websocketbrokerdemo.UsernameActivity
import com.google.gson.JsonObject
import io.mockk.mockk
import org.junit.Test

class NoOpLobbyListenerTest {

    private val mockActivity = mockk<UsernameActivity>(relaxed = true)
    private val listener = NoOpLobbyListener(mockActivity)

    @Test
    fun testOnLobbyListUpdateDoesNothing() {
        listener.onLobbyListUpdate(listOf())
        // No assertion, just ensure it doesn't crash
    }

    @Test
    fun testOnLobbyUpdateDoesNothing() {
        listener.onLobbyUpdate(listOf())
    }

    @Test
    fun testOnStartGameDoesNothing() {
        val json = JsonObject()
        listener.onStartGame(json)
    }
}
