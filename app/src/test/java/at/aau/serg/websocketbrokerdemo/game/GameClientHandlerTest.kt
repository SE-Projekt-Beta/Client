package at.aau.serg.websocketbrokerdemo.game

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
        val payload = JsonObject()  // Kein "dice" notwendig

        handler.handle(GameMessage(0, GameMessageType.GAME_STATE, payload))

        verify { mockActivity.updateTurnView(1, "Alice") }
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
    fun testHandleDiceRolled() {
        val payload = JsonObject().apply {
            addProperty("roll1", 3)
            addProperty("roll2", 4)
        }

        handler.handle(GameMessage(0, GameMessageType.DICE_ROLLED, payload))

        verify { mockActivity.updateDice(3, 4) }
        verify { mockActivity.onRollFinished() }
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
            addProperty("winnerId", 1)
            addProperty("winnerName", "Alice")
        }

        handler.handle(GameMessage(0, GameMessageType.GAME_OVER, payload))

        verify { mockActivity.showGameOverDialog("Alice") }
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

        verify(exactly = 0) { mockActivity.updateDice(any(), any()) }
        verify(exactly = 0) { mockActivity.updateTokenPosition(any()) }
        verify(exactly = 0) { mockActivity.onRollFinished() }
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

    @Test
    fun testHandleAskBuyProperty_myTurn() {
        every { LobbyClient.playerId } returns 1
        every { GameController.getTileName(7) } returns "Hauptstraße"
        every { GameController.getTilePrice(7) } returns 300
        every { GameController.evaluateTileOptions(1, 7) } returns GameController.TileOptions(true, true, false)

        val payload = JsonObject().apply {
            addProperty("playerId", 1)
            addProperty("fieldIndex", 7)
        }
        handler.handle(GameMessage(0, GameMessageType.ASK_BUY_PROPERTY, payload))

        verify { mockActivity.showBuyOptions(7, "Hauptstraße", true, true, false) }
        verify { mockActivity.showBuyDialog(7, "Hauptstraße") }
    }

    @Test
    fun testHandleAskBuyProperty_otherPlayer() {
        every { LobbyClient.playerId } returns 1
        val payload = JsonObject().apply {
            addProperty("playerId", 2)
            addProperty("fieldIndex", 7)
        }
        handler.handle(GameMessage(0, GameMessageType.ASK_BUY_PROPERTY, payload))

        verify(exactly = 0) { mockActivity.showBuyDialog(any(), any()) }
        verify(exactly = 0) { mockActivity.showBuyOptions(any(), any(), any(), any(), any()) }
    }

    @Test
    fun testHandleRolledPrison_myId() {
        every { LobbyClient.playerId } returns 1
        val payload = JsonObject().apply {
            addProperty("playerId", 1)
            addProperty("roll1", 2)
            addProperty("roll2", 4)
        }
        handler.handle(GameMessage(0, GameMessageType.ROLLED_PRISON, payload))
        verify { mockActivity.showRollPrisonDialog(2, 4) }
    }

    @Test
    fun testHandleRolledPrison_otherPlayer() {
        every { LobbyClient.playerId } returns 1
        val payload = JsonObject().apply {
            addProperty("playerId", 3)
            addProperty("roll1", 2)
            addProperty("roll2", 6)
        }
        handler.handle(GameMessage(0, GameMessageType.ROLLED_PRISON, payload))
        // Only logs, nothing to verify on UI for other player
        verify(exactly = 0) { mockActivity.showRollPrisonDialog(any(), any()) }
    }

    @Test
    fun testHandleAskPayPrison_myId() {
        every { LobbyClient.playerId } returns 5
        val payload = JsonObject().apply { addProperty("playerId", 5) }
        handler.handle(GameMessage(0, GameMessageType.ASK_PAY_PRISON, payload))
        verify { mockActivity.showPayPrisonDialog() }
    }

    @Test
    fun testHandleAskPayPrison_otherPlayer() {
        every { LobbyClient.playerId } returns 1
        val payload = JsonObject().apply { addProperty("playerId", 4) }
        handler.handle(GameMessage(0, GameMessageType.ASK_PAY_PRISON, payload))
        verify(exactly = 0) { mockActivity.showPayPrisonDialog() }
    }

    @Test
    fun testHandleBankCardDrawn_myId() {
        // Mock GameStateClient for nickname resolution
        mockkObject(GameStateClient)
        every { GameStateClient.getNickname(1) } returns "Alice"

        every { LobbyClient.playerId } returns 1
        val payload = JsonObject().apply {
            addProperty("playerId", 1)
            addProperty("amount", 300)
            addProperty("newCash", 1800)
            addProperty("description", "You received 300")
        }

        handler.handle(GameMessage(0, GameMessageType.DRAW_BANK_CARD, payload))

        verify { mockActivity.updateCashDisplay(1800) }
        unmockkObject(GameStateClient)
    }

    @Test
    fun testHandleBankCardDrawn_otherPlayer() {
        mockkObject(GameStateClient)
        every { GameStateClient.getNickname(2) } returns "Bob"
        every { LobbyClient.playerId } returns 1

        val payload = JsonObject().apply {
            addProperty("playerId", 2)
            addProperty("amount", 100)
            addProperty("newCash", 800)
            addProperty("description", "Other gets 100")
        }

        handler.handle(GameMessage(0, GameMessageType.DRAW_BANK_CARD, payload))

        verify(exactly = 0) { mockActivity.updateCashDisplay(any()) }
        unmockkObject(GameStateClient)
    }

    @Test
    fun testHandleRiskCardDrawn() {
        mockkObject(GameStateClient)
        every { GameStateClient.getNickname(3) } returns "Eve"

        val payload = JsonObject().apply {
            addProperty("playerId", 3)
            addProperty("title", "Risiko!")
            addProperty("description", "Gehe 3 Felder zurück")
        }

        handler.handle(GameMessage(0, GameMessageType.DRAW_RISK_CARD, payload))
        unmockkObject(GameStateClient)
        // Dialog is shown, can't verify dialog call directly here without custom dialog mocking
    }

    @Test
    fun testHandlePassStart_landed_true() {
        mockkObject(GameStateClient)
        every { GameStateClient.getNickname(1) } returns "Alice"
        every { LobbyClient.playerId } returns 1

        val payload = JsonObject().apply {
            addProperty("playerId", 1)
            addProperty("amount", 200)
            addProperty("landed", true)
        }
        handler.handle(GameMessage(0, GameMessageType.PASS_START, payload))

        // Only dialog is shown; verify dialog if you mock dialog constructor, here for completeness
        unmockkObject(GameStateClient)
    }

    @Test
    fun testHandlePassStart_landed_false_myId() {
        every { LobbyClient.playerId } returns 2
        mockkObject(GameController)
        every { GameController.getCash(2) } returns 999

        val payload = JsonObject().apply {
            addProperty("playerId", 2)
            addProperty("amount", 100)
            addProperty("landed", false)
        }
        handler.handle(GameMessage(0, GameMessageType.PASS_START, payload))

        verify { mockActivity.updateCashDisplay(999) }
    }

    @Test
    fun testHandleTax_myId() {
        mockkObject(GameStateClient)
        every { LobbyClient.playerId } returns 1
        every { GameStateClient.getNickname(1) } returns "Alice"
        val payload = JsonObject().apply {
            addProperty("playerId", 1)
            addProperty("tileName", "Luxussteuer")
            addProperty("amount", 150)
            addProperty("newCash", 1200)
        }
        handler.handle(GameMessage(0, GameMessageType.PAY_TAX, payload))
        verify { mockActivity.updateCashDisplay(1200) }
        unmockkObject(GameStateClient)
    }

    @Test
    fun testHandleTax_otherPlayer() {
        mockkObject(GameStateClient)
        every { LobbyClient.playerId } returns 2
        every { GameStateClient.getNickname(3) } returns "Bob"
        val payload = JsonObject().apply {
            addProperty("playerId", 3)
            addProperty("tileName", "Luxussteuer")
            addProperty("amount", 100)
            addProperty("newCash", 1000)
        }
        handler.handle(GameMessage(0, GameMessageType.PAY_TAX, payload))
        verify(exactly = 0) { mockActivity.updateCashDisplay(any()) }
        unmockkObject(GameStateClient)
    }

    @Test
    fun testHandleGoToJail_myId() {
        mockkObject(GameStateClient)
        every { LobbyClient.playerId } returns 5
        every { GameStateClient.getNickname(5) } returns "Carol"

        val payload = JsonObject().apply { addProperty("playerId", 5) }
        handler.handle(GameMessage(0, GameMessageType.GO_TO_JAIL, payload))
        unmockkObject(GameStateClient)
        // Dialog is shown, can't verify dialog call directly here without custom dialog mocking
    }

    @Test
    fun testHandleGoToJail_otherPlayer() {
        every { LobbyClient.playerId } returns 1
        val payload = JsonObject().apply { addProperty("playerId", 3) }
        handler.handle(GameMessage(0, GameMessageType.GO_TO_JAIL, payload))
        // Only logs, nothing to verify on UI
    }

    @Test
    fun testHandleUnknownMessageType() {
        val unknownType = GameMessageType.valueOf("EXTRA_MESSAGE").ordinal + 100
        val unknownMessage = GameMessage(0, GameMessageType.values().firstOrNull { it.ordinal == unknownType }
            ?: GameMessageType.ERROR, JsonObject())
        // Manipuliere zur Sicherheit den Typ direkt via Reflection
        val message = spyk(unknownMessage)
        every { message.type } returns GameMessageType.values().maxByOrNull { it.ordinal }!!.let {
            GameMessageType.valueOf(it.name)
        }

        handler.handle(message)
        // Keine Exception → Test besteht
    }



    @Test
    fun testHandleExtraMessage_myId() {
        val payload = JsonObject().apply {
            addProperty("playerId", 1)
            addProperty("title", "Hinweis")
            addProperty("message", "Du bist dran!")
        }

        every { LobbyClient.playerId } returns 1

        handler.handle(GameMessage(0, GameMessageType.EXTRA_MESSAGE, payload))

        verify { mockActivity.showDialog("Hinweis", "Du bist dran!") }
    }




}
