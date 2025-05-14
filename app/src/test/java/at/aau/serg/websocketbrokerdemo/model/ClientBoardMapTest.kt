package at.aau.serg.websocketbrokerdemo.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ClientBoardMapTest {

    @Test
    fun testTileListHasCorrectSize() {
        assertEquals(40, ClientBoardMap.tiles.size)
    }

    @Test
    fun testFirstTileIsStart() {
        val startTile = ClientBoardMap.tiles[0]
        assertEquals("Los", startTile.name)
        assertEquals(TileType.START, startTile.type)
    }

    @Test
    fun testTileAtPosition10IsJail() {
        val jailTile = ClientBoardMap.tiles[10]
        assertEquals("Polizeikontrolle", jailTile.name)
        assertEquals(TileType.GOTO_JAIL, jailTile.type)
    }

    @Test
    fun testAllTilesNotNull() {
        ClientBoardMap.tiles.forEach { tile ->
            assertNotNull(tile.name)
            assertNotNull(tile.type)
        }
    }
}
