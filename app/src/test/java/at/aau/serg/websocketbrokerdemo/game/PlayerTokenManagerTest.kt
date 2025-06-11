package at.aau.serg.websocketbrokerdemo.game

import android.widget.ImageView
import at.aau.serg.websocketbrokerdemo.GameBoardActivity
import at.aau.serg.websocketbrokerdemo.model.ClientBoardMap
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import io.mockk.*
import kotlin.test.assertEquals
import at.aau.serg.websocketbrokerdemo.R

class PlayerTokenManagerTest {
    private lateinit var gameBoardActivity: GameBoardActivity
    private lateinit var playerTokenManager: PlayerTokenManager
    private lateinit var mockImageViews: List<ImageView>

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)

        gameBoardActivity = mockk(relaxed = true)
        mockImageViews = List(6) { mockk(relaxed = true) }

        every { gameBoardActivity.findViewById<ImageView>(any()) } answers {
            val id = arg<Int>(0)
            when (id) {
                R.id.player_token1 -> mockImageViews[0]
                R.id.player_token2 -> mockImageViews[1]
                R.id.player_token3 -> mockImageViews[2]
                R.id.player_token4 -> mockImageViews[3]
                R.id.player_token5 -> mockImageViews[4]
                R.id.player_token6 -> mockImageViews[5]
                else -> mockk()
            }
        }

        every { gameBoardActivity.runOnUiThread(any()) } answers {
            val runnable = it.invocation.args[0] as Runnable
            runnable.run()
        }

        mockkObject(GameStateClient)
        every { GameStateClient.players } returns mutableMapOf(
            0 to PlayerClient(0, "Alice", 1500, 1, true, false, false),
            1 to PlayerClient(1, "Bob", 1500, 1, true, false, false)
        )

        playerTokenManager = PlayerTokenManager(gameBoardActivity)
        // Setze Boardgröße auf Original, damit Skalierung 1 ist
        playerTokenManager.setBoardSize(2048f, 2048f)
    }

    @Test
    fun testPositionTokensOnStartTile() {
        val startTile = ClientBoardMap.getTile(1)!!
        playerTokenManager.positionTokensOnStartTile()

        // Prüfe für Spieler 0
        verify { mockImageViews[0].x = startTile.position!!.x }
        verify { mockImageViews[0].y = startTile.position!!.y }

        // Prüfe für Spieler 1
        verify { mockImageViews[1].x = startTile.position!!.x }
        verify { mockImageViews[1].y = startTile.position!!.y }
    }

    @Test
    fun testMovePlayerToken() {
        val playerId = 0
        val steps = 3
        val newPosition = (GameStateClient.players[playerId]!!.position + steps) % 40
        val tileIndex = if (newPosition == 0) 40 else newPosition
        val newTile = ClientBoardMap.getTile(tileIndex)!!

        playerTokenManager.movePlayerToken(playerId, steps)

        verify { mockImageViews[0].x = newTile.position!!.x }
        verify { mockImageViews[0].y = newTile.position!!.y }

        // Verify player position updated
        assertEquals(newPosition, GameStateClient.players[playerId]?.position)
    }
}