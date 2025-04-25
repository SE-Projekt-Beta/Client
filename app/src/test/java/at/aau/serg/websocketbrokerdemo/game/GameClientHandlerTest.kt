package at.aau.serg.websocketbrokerdemo.game

import android.util.Log
import at.aau.serg.websocketbrokerdemo.MainActivity
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import com.google.gson.JsonObject
import io.mockk.*
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameClientHandlerTest {
    private lateinit var mockActivity: MainActivity
    private lateinit var handler: GameClientHandler

    @BeforeEach
    fun setup() {
        mockkStatic(Log::class)
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
    }


    @Test
    fun testHandleCurrentPlayer_isMyTurn() {
        every { mockActivity.getMyPlayerName() } returns "p1"
        val payload = JsonObject().apply { addProperty("playerId", "p1") }
        handler.handle(GameMessage(GameMessageType.CURRENT_PLAYER, payload))
        verify { mockActivity.enableDiceButton() }
    }

    @Test
    fun testHandleCurrentPlayer_notMyTurn() {
        every { mockActivity.getMyPlayerName() } returns "p2"
        val payload = JsonObject().apply { addProperty("playerId", "p1") }
        handler.handle(GameMessage(GameMessageType.CURRENT_PLAYER, payload))
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
        handler.handle(GameMessage(GameMessageType.PLAYER_MOVED, payload))
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
        handler.handle(GameMessage(GameMessageType.CAN_BUY_PROPERTY, payload))
        verify { mockActivity.showBuyButton("Opernring", 4, "p1") }
        verify { mockActivity.showResponse("p1 darf Opernring kaufen") }
    }

    @Test
    fun testHandlePropertyBought() {
        val payload = JsonObject().apply {
            addProperty("tileName", "Opernring")
            addProperty("playerId", "p1")
        }
        handler.handle(GameMessage(GameMessageType.PROPERTY_BOUGHT, payload))
        verify { OwnershipClient.addProperty("p1", "Opernring") }
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
        handler.handle(GameMessage(GameMessageType.MUST_PAY_RENT, payload))
        verify { mockActivity.showResponse("p1 muss Miete an p2 zahlen für Opernring") }
    }

    @Test
    fun testHandleDrawEventBankCard() {
        val payload = JSONObject().apply {
            put("eventTitle", "Bankkarte")
            put("eventDescription", "Du bekommst 100 Euro")
            put("eventAmount", 100)
        }.toString()
        handler.handle(GameMessage(GameMessageType.DRAW_EVENT_BANK_CARD, com.google.gson.JsonParser.parseString(payload)))
        verify { mockActivity.showEventCard("Bankkarte", "Du bekommst 100 Euro\nBetrag: 100€") }
    }

    @Test
    fun testHandleDrawEventRisikoCard() {
        val payload = JSONObject().apply {
            put("eventTitle", "Risikokarte")
            put("eventDescription", "Gehe 3 Felder zurück")
            put("eventAmount", -3)
        }.toString()
        handler.handle(GameMessage(GameMessageType.DRAW_EVENT_RISIKO_CARD, com.google.gson.JsonParser.parseString(payload)))
        verify { mockActivity.showEventCard("Risikokarte", "Gehe 3 Felder zurück\nBetrag: -3€") }
    }

    @Test
    fun testHandleGoToJail() {
        val payload = JSONObject().apply {
            put("playerId", "p1")
        }.toString()
        handler.handle(GameMessage(GameMessageType.GO_TO_JAIL, com.google.gson.JsonParser.parseString(payload)))
        verify { GameStateClient.updatePosition("p1", 10) }
        verify { mockActivity.showJailDialog("p1") }
    }

    @Test
    fun testHandleError() {
        handler.handle(GameMessage(GameMessageType.ERROR, com.google.gson.JsonParser.parseString("\"Etwas ist schiefgelaufen\"")))
        verify { Log.e(any(), match { it.contains("Etwas ist schiefgelaufen") }) }
    }
}

