package at.aau.serg.websocketbrokerdemo

import at.aau.serg.websocketbrokerdemo.dkt.GameMessage
import at.aau.serg.websocketbrokerdemo.dkt.GameStateClient
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ClientLogicTest {
    @Test
    fun testGameMessageSerialization() {
        val msg = GameMessage("test", "Hallo")
        assertEquals("test", msg.type)
        assertEquals("Hallo", msg.payload)
    }

    @Test
    fun testGameStateClientStoresPosition() {
        GameStateClient.updatePosition("player1", 7)
        val pos = GameStateClient.getPosition("player1")
        assertEquals(7, pos)
    }

    @Test
    fun testGameStateClientDefaultPosition() {
        val pos = GameStateClient.getPosition("unknown")
        assertEquals(0, pos) // Standardwert, wenn Spieler unbekannt
    }

    @Test
    fun testRollDiceMessageFormat() {
        val playerId = "player1"
        val json = JSONObject()
        json.put("playerId", playerId)

        val msg = GameMessage("roll_dice", json.toString())

        val parsed = JSONObject(msg.payload)
        assertEquals(playerId, parsed.getString("playerId"))
    }

    @Test
    fun testBuyPropertyMessageStructure() {
        val json = JSONObject()
        json.put("playerId", "player1")
        json.put("tilePos", 3)

        val msg = GameMessage("buy_property", json.toString())
        val parsed = JSONObject(msg.payload)

        assertEquals("player1", parsed.getString("playerId"))
        assertEquals(3, parsed.getInt("tilePos"))
    }

    @Test
    fun testPlayerMovedMessageIncludesFields() {
        val payload = JSONObject()
        payload.put("playerId", "p1")
        payload.put("pos", 5)
        payload.put("dice", 3)
        payload.put("tileName", "Bahnhof West")
        payload.put("tileType", "station")

        val msg = GameMessage("player_moved", payload.toString())
        val parsed = JSONObject(msg.payload)

        assertTrue(parsed.has("playerId"))
        assertTrue(parsed.has("pos"))
        assertTrue(parsed.has("tileName"))
        assertEquals("station", parsed.getString("tileType"))
    }

    @Test
    fun testCanBuyPropertyPayloadIncludesCorrectInfo() {
        val payload = JSONObject()
        payload.put("playerId", "p1")
        payload.put("tilePos", 4)
        payload.put("tileName", "Opernring")

        val msg = GameMessage("can_buy_property", payload.toString())
        val parsed = JSONObject(msg.payload)

        assertEquals("p1", parsed.getString("playerId"))
        assertEquals(4, parsed.getInt("tilePos"))
        assertEquals("Opernring", parsed.getString("tileName"))
    }

    @Test
    fun testMustPayRentIncludesOwner() {
        val payload = JSONObject()
        payload.put("playerId", "p2")
        payload.put("ownerId", "p1")
        payload.put("tileName", "Bahnhof West")
        payload.put("tilePos", 3)

        val msg = GameMessage("must_pay_rent", payload.toString())
        val parsed = JSONObject(msg.payload)

        assertEquals("p2", parsed.getString("playerId"))
        assertEquals("p1", parsed.getString("ownerId"))
        assertEquals("Bahnhof West", parsed.getString("tileName"))
    }
    @Test
    fun testEventCardMessageContainsText() {
        val messageText = "Zahle 200€ Strafe"
        val msg = GameMessage("event_card", messageText)

        assertEquals("event_card", msg.type)
        assertEquals(messageText, msg.payload)
        assertTrue(msg.payload.contains("€"))
    }

}