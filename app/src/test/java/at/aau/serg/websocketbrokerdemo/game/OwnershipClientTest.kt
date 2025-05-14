package at.aau.serg.websocketbrokerdemo.game

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OwnershipClientTest {

    @BeforeEach
    fun setup() {
        OwnershipClient.streetOwners.clear()
    }

    @Test
    fun testUpdateFromBoard_withOwners() {
        val board = JSONArray().apply {
            put(JSONObject().apply {
                put("type", "StreetTile")
                put("index", 0)
                put("ownerId", 1)
            })
            put(JSONObject().apply {
                put("type", "StreetTile")
                put("index", 1)
                put("ownerId", -1) // unbesetzt
            })
            put(JSONObject().apply {
                put("type", "EventTile")
                put("index", 2)
            })
        }

        OwnershipClient.updateFromBoard(board)

        assertEquals(1, OwnershipClient.getOwnerId(0))
        assertNull(OwnershipClient.getOwnerId(1))
        assertNull(OwnershipClient.getOwnerId(2)) // kein StreetTile, daher kein Eintrag
    }

    @Test
    fun testUpdateFromBoard_emptyArray() {
        val emptyBoard = JSONArray()

        OwnershipClient.updateFromBoard(emptyBoard)

        assertTrue(OwnershipClient.streetOwners.isEmpty())
    }

    @Test
    fun testGetOwnerId_nonExistingIndex() {
        // Kein Eintrag vorhanden
        assertNull(OwnershipClient.getOwnerId(99))
    }

    @Test
    fun testUpdateFromBoard_overwritesOldValues() {
        // Vorheriger Eintrag
        OwnershipClient.streetOwners[0] = 42

        val board = JSONArray().apply {
            put(JSONObject().apply {
                put("type", "StreetTile")
                put("index", 0)
                put("ownerId", 2)
            })
        }

        OwnershipClient.updateFromBoard(board)

        assertEquals(2, OwnershipClient.getOwnerId(0))
    }
}