package at.aau.serg.websocketbrokerdemo

import DktClientHandler
import at.aau.serg.websocketbrokerdemo.dkt.GameMessage
import at.aau.serg.websocketbrokerdemo.dkt.GameStateClient
import at.aau.serg.websocketbrokerdemo.dkt.OwnershipClient
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import kotlin.test.assertEquals

class DktClientHandlerTest {
    private lateinit var handler: DktClientHandler
    private val activity: MainActivity = mock()

    @BeforeEach
    fun setup() {
        handler = DktClientHandler(activity)
    }

    @Test
    fun handlePlayerMoved_updatesPositionAndShowsResponse() {
        val payload = JSONObject().apply {
            put("playerId", "player1")
            put("pos", 8)
            put("dice", 5)
            put("tileName", "Opernring")
            put("tileType", "street")
        }.toString()

        handler.handle(GameMessage("player_moved", payload))

        verify(activity).showResponse(check { it.contains("Opernring") })
        assertEquals(8, GameStateClient.getPosition("player1"))
    }

    @Test
    fun handleCanBuyProperty_showsBuyButtonAndResponse() {
        val payload = JSONObject().apply {
            put("tileName", "Opernring")
            put("tilePos", 8)
            put("playerId", "player1")
        }.toString()

        handler.handle(GameMessage("can_buy_property", payload))

        verify(activity).showBuyButton(eq("Opernring"), eq(8), eq("player1"))
        verify(activity).showResponse(check { it.contains("darf") })
    }

    @Test
    fun handlePropertyBought_addsOwnershipAndUpdatesView() {
        val payload = JSONObject().apply {
            put("playerId", "player1")
            put("tileName", "Opernring")
        }.toString()

        handler.handle(GameMessage("property_bought", payload))

        verify(activity).showOwnership()
        verify(activity).showResponse(check { it.contains("Kauf abgeschlossen") })

        val properties = OwnershipClient.getProperties("player1")
        assert(properties.contains("Opernring"))
    }

    @Test
    fun handleMustPayRent_showsRentMessage() {
        val payload = JSONObject().apply {
            put("playerId", "player1")
            put("ownerId", "player2")
            put("tileName", "Opernring")
        }.toString()

        handler.handle(GameMessage("must_pay_rent", payload))

        verify(activity).showResponse(check { it.contains("Miete") })
    }

    @Test
    fun handleEventCard_showsEventCardDialog() {
        val payload = "Du erhältst 200 Münzen!"

        handler.handle(GameMessage("event_card", payload))

        verify(activity).showEventCard(eq(payload))
    }

    @Test
    fun handleUnknownType_doesNotCrash() {
        handler.handle(GameMessage("unknown_type", "payload"))
        // No crash = test passes
    }
}