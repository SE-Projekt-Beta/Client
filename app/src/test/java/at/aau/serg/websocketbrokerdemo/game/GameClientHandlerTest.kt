package at.aau.serg.websocketbrokerdemo.game

import android.app.Activity
import android.app.Dialog
import android.util.Log
import at.aau.serg.websocketbrokerdemo.GameBoardActivity
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GameClientHandlerTest {
    private lateinit var mockActivity: GameBoardActivity
    private lateinit var handler: GameClientHandler

    @BeforeEach
    fun setup() {
        mockActivity = mockk(relaxed = true)
        handler = GameClientHandler(mockActivity)

        mockkStatic(Log::class)
        every { Log.i(any(), any<String>()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0

        mockkObject(GameController)
        mockkObject(LobbyClient)

        every { GameController.updateFromGameState(any()) } just Runs
        every { GameController.getCurrentPlayerId() } returns 1
        every { GameController.getCurrentPlayerName() } returns "Alice"
        every { GameController.getCurrentFieldIndex(1) } returns 4
        every { GameController.getTileName(4) } returns "Ringstraße"
        every { GameController.getCash(1) } returns 1500
        every { GameController.evaluateTileOptions(1, 4) } returns GameController.TileOptions(
            true,
            false,
            false
        )
        every { LobbyClient.playerId } returns 1
    }

    @Test
    fun testHandleGameState_myTurn() {
        val payload = JsonObject().apply { addProperty("dice", 5) }
        val message = GameMessage(0, GameMessageType.GAME_STATE, payload)

        handler.handle(message)

        verify { mockActivity.updateTurnView(1, "Alice") }
        verify { mockActivity.updateDice(5) }
        verify { mockActivity.updateTile("Ringstraße") }
        verify { mockActivity.updateCashDisplay(1500) }
        verify { mockActivity.enableDiceButton() }
        verify {
            mockActivity.showBuyOptions(4, "Ringstraße", true, false, false)
        }
    }

    @Test
    fun testHandleGameState_otherPlayersTurn() {
        every { LobbyClient.playerId } returns 2 // not my turn
        val payload = JsonObject().apply { addProperty("dice", 3) }
        val message = GameMessage(0, GameMessageType.GAME_STATE, payload)

        handler.handle(message)

        verify { mockActivity.disableDiceButton() }
        verify { mockActivity.hideActionButtons() }
    }

    @Test
    fun testHandleEventCard() {
        val payload = JsonObject().apply {
            addProperty("title", "Risiko!")
            addProperty("description", "Ziehe eine Karte.")
        }

        val message = GameMessage(0, GameMessageType.DRAW_RISK_CARD, payload)
        handler.handle(message)

        verify { mockActivity.showEventCard("Risiko!", "Ziehe eine Karte.") }
    }
    /*
    @Test
    fun testHandlePassStart() {
        every { mockActivity.runOnUiThread(any()) } answers {
            firstArg<Runnable>().run()
        }

        handler.handle(GameMessage(0, GameMessageType.PASS_START, JsonObject()))

        verify {
            mockActivity.runOnUiThread(any())
            // Nicht direkt testbar: StartBonusDialog(activity, ...).show()
        }
    }

     */
    /*
    @Test
    fun testHandleTax() {
        every { mockActivity.runOnUiThread(any()) } answers {
            firstArg<Runnable>().run()
        }

        handler.handle(GameMessage(0, GameMessageType.PAY_TAX, JsonObject()))

        verify {
            mockActivity.runOnUiThread(any())
            // TaxDialog(activity, ...).show() wird erwartet
        }
    }

     */
    /*
    @Test
    fun testHandleGoToJail() {
        every { mockActivity.runOnUiThread(any()) } answers {
            firstArg<Runnable>().run()
        }

        handler.handle(GameMessage(0, GameMessageType.GO_TO_JAIL, JsonObject()))

        verify {
            mockActivity.runOnUiThread(any())
            // RiskCardDialog(activity, ...).show() wird erwartet
        }
    }

     */

    @Test
    fun testHandleDiceRolled() {
        val payload = JsonObject().apply { addProperty("steps", 6) }

        handler.handle(GameMessage(0, GameMessageType.DICE_ROLLED, payload))

        verify { mockActivity.updateDice(6) }
    }

    @Test
    fun testHandleCashTask_myId() {
        every { LobbyClient.playerId } returns 1

        val payload = JsonObject().apply {
            addProperty("playerId", 1)
            addProperty("amount", -200)
            addProperty("newCash", 1300)
        }

        handler.handle(GameMessage(0, GameMessageType.CASH_TASK, payload))

        verify { mockActivity.updateCashDisplay(1300) }
        verify { Log.i(any(), match { it.contains("1300") }) }
    }

    @Test
    fun testHandlePlayerLost() {
        val payload = JsonObject().apply { addProperty("playerId", 3) }

        handler.handle(GameMessage(0, GameMessageType.PLAYER_LOST, payload))

        verify { mockActivity.showPlayerLost(3) }
        verify { Log.i(any(), match { it.contains("bankrott") }) }
    }

    @Test
    fun testHandleGameOver() {
        val payload = JsonObject().apply {
            add("ranking", JsonArray().apply {
                add("1. Alice")
                add("2. Bob")
            })
        }

        handler.handle(GameMessage(0, GameMessageType.GAME_OVER, payload))

        verify { mockActivity.showGameOverDialog("1. Alice\n2. Bob") }
    }

    @Test
    fun testHandleError() {
        val payload = JsonObject().apply {
            addProperty("message", "Etwas ist schiefgelaufen")
        }

        handler.handle(GameMessage(0, GameMessageType.ERROR, payload))

        verify { Log.e(any(), match { it.contains("Etwas ist schiefgelaufen") }) }
    }

    @Test
    fun testHandleDiceRolledMissingSteps() {
        val payload = JsonObject()

        handler.handle(GameMessage(0, GameMessageType.DICE_ROLLED, payload))

        verify(exactly = 0) { mockActivity.updateDice(any()) }
    }

    @Test
    fun testHandleCashTaskOtherPlayerId() {
        every { LobbyClient.playerId } returns 2

        val payload = JsonObject().apply {
            addProperty("playerId", 1)
            addProperty("amount", 100)
            addProperty("newCash", 2000)
        }

        handler.handle(GameMessage(0, GameMessageType.CASH_TASK, payload))

        verify(exactly = 0) { mockActivity.updateCashDisplay(any()) }
        verify { Log.i(any(), match { it.contains("2000") }) }
    }


}
