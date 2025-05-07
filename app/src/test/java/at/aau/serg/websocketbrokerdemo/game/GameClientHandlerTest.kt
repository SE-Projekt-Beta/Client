package at.aau.serg.websocketbrokerdemo.game

import android.util.Log
import at.aau.serg.websocketbrokerdemo.GameBoardActivity
import at.aau.serg.websocketbrokerdemo.ListLobbyActivity
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import com.google.gson.JsonObject
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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

        mockkObject(GameStateClient)
        mockkObject(OwnershipClient)
        every { GameStateClient.updatePosition(any(), any()) } just Runs
        every { OwnershipClient.addProperty(any(), any()) } just Runs
    }

    @Test
    fun testHandleCurrentPlayer_isMyTurn() {
        every { mockActivity.getMyPlayerName() } returns "p1"
        val payload = JsonObject().apply { addProperty("playerId", "p1") }
        handler.handle(GameMessage(0, GameMessageType.CURRENT_PLAYER, payload))
        verify { mockActivity.enableDiceButton() }
    }

    @Test
    fun testHandleCurrentPlayer_notMyTurn() {
        every { mockActivity.getMyPlayerName() } returns "p2"
        val payload = JsonObject().apply { addProperty("playerId", "p1") }
        handler.handle(GameMessage(0, GameMessageType.CURRENT_PLAYER, payload))
        verify { mockActivity.disableDiceButton() }
    }

    @Test
    fun testHandlePlayerMoved() {
        val payload = JsonObject().apply {
            addProperty("playerId", "p1")
            addProperty("pos", 4)
            addProperty("dice", 2)
            addProperty("tileName", "Opernring")
            addProperty("tileType", "street")
        }
        handler.handle(GameMessage(0, GameMessageType.PLAYER_MOVED, payload))
        verify { GameStateClient.updatePosition("p1", 4) }
        verify { mockActivity.showResponse("p1 → Opernring (street), gewürfelt: 2") }
    }

    @Test
    fun testHandleCanBuyProperty() {
        val payload = JsonObject().apply {
            addProperty("tileName", "Opernring")
            addProperty("tilePos", 4)
            addProperty("playerId", "p1")
        }
        handler.handle(GameMessage(0, GameMessageType.CAN_BUY_PROPERTY, payload))
        verify { mockActivity.showBuyButton("Opernring", 4, "p1") }
        verify { mockActivity.showResponse("p1 darf Opernring kaufen") }
    }

    @Test
    fun testHandlePropertyBought() {
        val payload = JsonObject().apply {
            addProperty("tileName", "Opernring")
            addProperty("playerId", "p1")
            addProperty("tilePos", 5)
        }
        handler.handle(GameMessage(0, GameMessageType.PROPERTY_BOUGHT, payload))
        
        verify { GameStateClient.addProperty("p1", 5) }
        verify { mockActivity.showOwnership() }
        verify { mockActivity.showResponse("Kauf abgeschlossen: Opernring für p1") }
    }

    @Test
    fun testHandleMustPayRent() {
        val payload = JsonObject().apply {
            addProperty("playerId", "p1")
            addProperty("ownerId", "p2")
            addProperty("tileName", "Opernring")
        }
        handler.handle(GameMessage(0, GameMessageType.MUST_PAY_RENT, payload))
        verify { mockActivity.showResponse("p1 muss Miete an p2 zahlen für Opernring") }
    }


    @Test
    fun testHandleError() {
        handler.handle(GameMessage(0, GameMessageType.ERROR, com.google.gson.JsonParser.parseString("\"Etwas ist schiefgelaufen\"")))
        verify { Log.e(any(), match { it.contains("Etwas ist schiefgelaufen") }) }
    }
}
