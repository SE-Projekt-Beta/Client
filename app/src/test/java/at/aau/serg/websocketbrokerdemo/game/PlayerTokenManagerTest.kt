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
        // Mock für findViewById - jedes Mal neues ImageView mocken
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
    }

    @Test
    fun testPositionTokensOnStartTile() {
        playerTokenManager.positionTokensOnStartTile()

        verify {
            mockImageViews[0].translationX = any()
            mockImageViews[0].translationY = any()
            mockImageViews[1].translationX = any()
            mockImageViews[1].translationY = any()
        }
    }

    @Test
    fun testMovePlayerToken() {
        val playerId = 0
        val steps = 3
        val newTile = ClientBoardMap.getTile(4)!!

        every { gameBoardActivity.runOnUiThread(any()) } answers {
            (firstArg() as Runnable).run()
        }

        playerTokenManager.movePlayerToken(playerId, steps)

        // Verify token position updated
        verify { mockImageViews[0].translationX = newTile.position!!.x }
        verify { mockImageViews[0].translationY = newTile.position!!.y }

        // Verify player position updated
        assertEquals(4, GameStateClient.players[playerId]?.position)
    }
}