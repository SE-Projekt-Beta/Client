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


}