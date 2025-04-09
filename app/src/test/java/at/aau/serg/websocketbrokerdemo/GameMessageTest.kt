package at.aau.serg.websocketbrokerdemo

import at.aau.serg.websocketbrokerdemo.dkt.GameMessage
import junit.framework.TestCase.assertEquals
import org.json.JSONObject
import org.junit.jupiter.api.Test

class GameMessageTest {
    @Test
    fun `should correctly create a GameMessage`() {
        val message = GameMessage("test_type", "test_payload")
        assertEquals("test_type", message.type)
        assertEquals("test_payload", message.payload)
    }

    @Test
    fun `should serialize and deserialize playerMoved message`() {
        val payload = JSONObject().apply {
            put("playerId", "player1")
            put("pos", 5)
            put("dice", 3)
            put("tileName", "Opernring")
            put("tileType", "street")
        }.toString()

        val message = GameMessage("player_moved", payload)
        val parsed = JSONObject(message.payload)

        assertEquals("player1", parsed.getString("playerId"))
        assertEquals(5, parsed.getInt("pos"))
        assertEquals("Opernring", parsed.getString("tileName"))
    }
}