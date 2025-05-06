package at.aau.serg.websocketbrokerdemo.lobby

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import at.aau.serg.websocketbrokerdemo.R
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.assertEquals

class LobbyAdapterTest {

    private lateinit var adapter: LobbyAdapter
    private val testLobbies = listOf(
        LobbyDTO("1", "Lobby 1", 2),
        LobbyDTO("2", "Lobby 2", 3),
        LobbyDTO("3", "Empty Lobby", 0)
    )
    private val onLobbyClick: (LobbyDTO) -> Unit = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        adapter = LobbyAdapter(testLobbies, onLobbyClick)
    }

    @Test
    fun `getItemCount should return correct number of lobbies`() {
        assertEquals(testLobbies.size, adapter.itemCount)
    }

    @Test
    fun `onCreateViewHolder should inflate correct layout`() {
        // Mock dependencies
        val parent = mockk<ViewGroup>()
        val context = mockk<android.content.Context>()
        val inflater = mockk<LayoutInflater>()
        val mockView = mockk<View>()
        val mockLobbyNameTextView = mockk<TextView>()
        val mockPlayerCountTextView = mockk<TextView>()

        // Setup mocks
        every { parent.context } returns context
        mockkStatic(LayoutInflater::class)
        every { LayoutInflater.from(context) } returns inflater
        every { inflater.inflate(R.layout.item_lobby, parent, false) } returns mockView
        every { mockView.findViewById<TextView>(R.id.lobbyName) } returns mockLobbyNameTextView
        every { mockView.findViewById<TextView>(R.id.playerCount) } returns mockPlayerCountTextView

        // Execute
        adapter.onCreateViewHolder(parent, 0)

        // Verify
        verify { inflater.inflate(R.layout.item_lobby, parent, false) }
    }
}
