package at.aau.serg.websocketbrokerdemo.dkt

import android.util.Log
import at.aau.serg.websocketbrokerdemo.MainActivity
import at.aau.serg.websocketbrokerdemo.network.GameMessage
import io.mockk.*
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DktClientHandlerTest {
    private lateinit var mockActivity: MainActivity
    private lateinit var dktClientHandler: DktClientHandler

    @BeforeEach
    fun setUp() {
        // Mock Android Log class
        mockkStatic(Log::class)
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0

        // Mock MainActivity
        mockActivity = mockk(relaxed = true)

        // Mock static methods in GameStateClient and OwnershipClient
        mockkObject(GameStateClient)
        every { GameStateClient.updatePosition(any(), any()) } just Runs

        mockkObject(OwnershipClient)
        every { OwnershipClient.addProperty(any(), any()) } just Runs

        dktClientHandler = DktClientHandler(mockActivity)
    }

    @Test
    fun testHandleDiceResult() {
        // Setup
        val payload = "6"
        val message = GameMessage("dice_result", payload)

        // Execute
        dktClientHandler.handle(message)

        // Verify
        verify { Log.i("DktClientHandler", "Gewürfelergebnis: $payload") }
        verify { mockActivity.showResponse("Gewürfelergebnis: $payload") }
    }

    @Test
    fun testHandleBuyProperty() {
        // Setup
        val payload = "Property details"
        val message = GameMessage("buy_property", payload)

        // Execute
        dktClientHandler.handle(message)

        // Verify
        verify { Log.i("DktClientHandler", "Kaufversuch: $payload") }
        verify { mockActivity.showResponse("Kaufversuch: $payload") }
    }

    @Test
    fun testHandlePlayerMoved() {
        // Setup
        val playerId = "player1"
        val pos = 5
        val dice = 3
        val tileName = "Parkstraße"
        val tileType = "property"

        val json = JSONObject().apply {
            put("playerId", playerId)
            put("pos", pos)
            put("dice", dice)
            put("tileName", tileName)
            put("tileType", tileType)
        }

        val message = GameMessage("player_moved", json.toString())

        // Execute
        dktClientHandler.handle(message)

        // Verify
        verify {
            Log.i("DktClientHandler", "$playerId hat $dice gewürfelt und ist auf Feld $pos gelandet: $tileName ($tileType)")
        }
        verify { mockActivity.showResponse("$playerId → $tileName ($tileType), gewürfelt: $dice") }
        verify { GameStateClient.updatePosition(playerId, pos) }
    }

    @Test
    fun testHandleCanBuyProperty() {
        // Setup
        val tileName = "Schlossallee"
        val tilePos = 39
        val playerId = "player1"

        val json = JSONObject().apply {
            put("tileName", tileName)
            put("tilePos", tilePos)
            put("playerId", playerId)
        }

        val message = GameMessage("can_buy_property", json.toString())

        // Execute
        dktClientHandler.handle(message)

        // Verify
        verify { Log.i("DktClientHandler", "$playerId darf $tileName kaufen!") }
        verify { mockActivity.showBuyButton(tileName, tilePos, playerId) }
        verify { mockActivity.showResponse("$playerId darf $tileName kaufen") }
    }

    @Test
    fun testHandlePropertyBought() {
        // Setup
        val playerId = "player1"
        val tileName = "Schlossallee"

        val json = JSONObject().apply {
            put("playerId", playerId)
            put("tileName", tileName)
        }

        val message = GameMessage("property_bought", json.toString())

        // Execute
        dktClientHandler.handle(message)

        // Verify
        verify { Log.i("DktClientHandler", "Feld erfolgreich gekauft: $tileName von $playerId") }
        verify { OwnershipClient.addProperty(playerId, tileName) }
        verify { mockActivity.showOwnership() }
        verify { mockActivity.showResponse("Kauf abgeschlossen: $tileName für $playerId") }
    }

    @Test
    fun testHandleDrawEventCard() {
        // Setup
        val payload = "Draw card details"
        val message = GameMessage("draw_event_card", payload)

        // Execute
        dktClientHandler.handle(message)

        // Verify
        verify { Log.i("DktClientHandler", "Ereigniskarte ziehen: $payload") }
        verify { mockActivity.showResponse("Ereigniskarte ziehen: $payload") }
    }

    @Test
    fun testHandleMustPayRent() {
        // Setup
        val playerId = "player1"
        val ownerId = "player2"
        val tileName = "Schlossallee"

        val json = JSONObject().apply {
            put("playerId", playerId)
            put("ownerId", ownerId)
            put("tileName", tileName)
        }

        val message = GameMessage("must_pay_rent", json.toString())

        // Execute
        dktClientHandler.handle(message)

        // Verify
        verify { Log.i("DktClientHandler", "$playerId muss Miete an $ownerId zahlen für $tileName") }
        verify { mockActivity.showResponse("$playerId muss Miete an $ownerId zahlen für $tileName") }
    }

    @Test
    fun testHandleEventCard() {
        // Setup
        val payload = "You won the lottery!"
        val message = GameMessage("event_card", payload)

        // Execute
        dktClientHandler.handle(message)

        // Verify
        verify { Log.i("DktClientHandler", "Ereigniskarte gezogen: $payload") }
        verify { mockActivity.showEventCard(payload) }
    }

    @Test
    fun testHandleUnknownMessage() {
        // Setup
        val message = GameMessage("unknown_type", "payload")

        // Execute
        dktClientHandler.handle(message)

        // Verify
        verify { Log.w("DktClientHandler", "Unbekannter Nachrichtentyp: unknown_type") }

        // Verify no other methods were called
        verify(exactly = 0) { mockActivity.showResponse(any()) }
        verify(exactly = 0) { mockActivity.showBuyButton(any(), any(), any()) }
        verify(exactly = 0) { mockActivity.showOwnership() }
        verify(exactly = 0) { mockActivity.showEventCard(any()) }
    }
}
