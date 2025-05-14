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
        gameBoardActivity = mockk(relaxed = true)
        // Mock für findViewById - jedes Mal ein neues ImageView mocken
        mockImageViews = List(6) { mockk(relaxed = true) }

        every { gameBoardActivity.findViewById<ImageView>(R.id.player_token1) } returns mockImageViews[0]
        every { gameBoardActivity.findViewById<ImageView>(R.id.player_token2) } returns mockImageViews[1]
        every { gameBoardActivity.findViewById<ImageView>(R.id.player_token3) } returns mockImageViews[2]
        every { gameBoardActivity.findViewById<ImageView>(R.id.player_token4) } returns mockImageViews[3]
        every { gameBoardActivity.findViewById<ImageView>(R.id.player_token5) } returns mockImageViews[4]
        every { gameBoardActivity.findViewById<ImageView>(R.id.player_token6) } returns mockImageViews[5]

        playerTokenManager = PlayerTokenManager(gameBoardActivity)

        // Mock GameStateClient.players statisch (falls nötig)
        mockkObject(GameStateClient)
        every {
            GameStateClient.players
        } returns mutableMapOf(
            0 to PlayerClient(
                id = 0,
                nickname = "Alice",
                cash = 1000, // Beispielwert für cash
                position = 1,
                alive = true,  // Beispielwert für alive
                suspended = false,  // Beispielwert für suspended
                hasEscapeCard = false // Beispielwert für hasEscapeCard
            ),
            1 to PlayerClient(
                id = 1,
                nickname = "Bob",
                cash = 1000, // Beispielwert für cash
                position = 1,
                alive = true,  // Beispielwert für alive
                suspended = false,  // Beispielwert für suspended
                hasEscapeCard = false // Beispielwert für hasEscapeCard
            )
        )
    }

    @Test
    fun testPositionTokensOnStartTile() {
        playerTokenManager.positionTokensOnStartTile()

        // Verify tokens moved to start position (ClientBoardMap.getTile(1))
        val startTile = ClientBoardMap.getTile(1)!!

        verify {
            gameBoardActivity.runOnUiThread(captureLambda())
        }

        // Simuliere den runOnUiThread-Block
        val lambda = slot<() -> Unit>()
        verify { gameBoardActivity.runOnUiThread(capture(lambda)) }
        lambda.captured.invoke()

        // Verifiziere, dass Tokens gesetzt wurden
        mockImageViews.take(2).forEach {
            verify { it.visibility = ImageView.VISIBLE }
            verify { it.translationX = startTile.position!!.x }
            verify { it.translationY = startTile.position!!.y }
        }
    }

    @Test
    fun testMovePlayerToken() {
        val playerId = 0
        val steps = 3
        val newTile = ClientBoardMap.getTile(4)!!

        // Execute
        playerTokenManager.movePlayerToken(playerId, steps)

        // Capture runOnUiThread block
        val lambda = slot<() -> Unit>()
        verify { gameBoardActivity.runOnUiThread(capture(lambda)) }
        lambda.captured.invoke()

        // Verify token position updated
        verify { mockImageViews[0].translationX = newTile.position!!.x }
        verify { mockImageViews[0].translationY = newTile.position!!.y }

        // Verify player position updated
        assertEquals(4, GameStateClient.players[playerId]?.position)
    }
}