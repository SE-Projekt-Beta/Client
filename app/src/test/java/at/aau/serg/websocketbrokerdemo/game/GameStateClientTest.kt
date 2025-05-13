package at.aau.serg.websocketbrokerdemo.game

import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue

class GameStateClientTest {

    private fun createMockServerData(): JSONObject {
        return JSONObject().apply {
            put("currentPlayerId", 1)
            put("currentRound", 2)
            put("players", JSONArray().apply {
                put(JSONObject().apply {
                    put("id", 1)
                    put("nickname", "Alice")
                    put("cash", 1500)
                    put("position", 4)
                    put("alive", true)
                    put("suspended", false)
                    put("escapeCard", true)
                    put("properties", JSONArray().apply {
                        put(10)
                        put(12)
                    })
                    put("houseCounts", JSONObject().apply {
                        put("10", 2)
                        put("12", 3)
                    })
                })
                put(JSONObject().apply {
                    put("id", 2)
                    put("nickname", "Bob")
                    put("cash", 1000)
                    put("position", 7)
                    put("alive", false)
                    put("suspended", true)
                    put("escapeCard", false)
                    put("properties", JSONArray())
                    put("houseCounts", JSONObject())
                })
            })
        }
    }

    @BeforeEach
    fun setup() {
        // Clear state before each test
        GameStateClient.players.clear()
        GameStateClient.currentPlayerId = -1
        GameStateClient.currentRound = 1
    }

    @Test
    fun testUpdateFromServer() {
        val data = createMockServerData()
        GameStateClient.updateFromServer(data)

        assertEquals(1, GameStateClient.currentPlayerId)
        assertEquals(2, GameStateClient.currentRound)
        assertEquals(2, GameStateClient.players.size)

        val alice = GameStateClient.players[1]
        assertNotNull(alice)
        assertEquals("Alice", alice!!.nickname)
        assertEquals(1500, alice.cash)
        assertEquals(4, alice.position)
        assertTrue(alice.alive)
        assertTrue(alice.hasEscapeCard)
        assertEquals(listOf(10, 12), alice.properties)
        assertEquals(mapOf(10 to 2, 12 to 3), alice.houseCounts)
    }

    @Test
    fun testGetPlayerPosition() {
        GameStateClient.updateFromServer(createMockServerData())
        assertEquals(4, GameStateClient.getPlayerPosition(1))
        assertEquals(7, GameStateClient.getPlayerPosition(2))
        assertEquals(-1, GameStateClient.getPlayerPosition(999)) // nonexistent
    }

    @Test
    fun testGetCash() {
        GameStateClient.updateFromServer(createMockServerData())
        assertEquals(1500, GameStateClient.getCash(1))
        assertEquals(1000, GameStateClient.getCash(2))
        assertEquals(0, GameStateClient.getCash(999))  // nonexistent
    }

    @Test
    fun testGetNickname() {
        GameStateClient.updateFromServer(createMockServerData())
        assertEquals("Alice", GameStateClient.getNickname(1))
        assertEquals("Bob", GameStateClient.getNickname(2))
        assertNull(GameStateClient.getNickname(3))
    }

    @Test
    fun testGetPlayer() {
        GameStateClient.updateFromServer(createMockServerData())
        val player = GameStateClient.getPlayer(1)
        assertNotNull(player)
        assertEquals("Alice", player!!.nickname)
    }

    @Test
    fun testGetAllNicknames() {
        GameStateClient.updateFromServer(createMockServerData())
        val nicknames = GameStateClient.getAllNicknames()
        assertTrue(nicknames.contains("Alice"))
        assertTrue(nicknames.contains("Bob"))
    }

    @Test
    fun testRemainingPlayers() {
        GameStateClient.updateFromServer(createMockServerData())
        assertEquals(1, GameStateClient.remainingPlayers) // only Alice is alive
    }
}