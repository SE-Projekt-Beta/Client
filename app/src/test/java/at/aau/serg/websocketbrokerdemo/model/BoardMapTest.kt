package at.aau.serg.websocketbrokerdemo.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class BoardMapTest {

    @Test
    fun testTileListHasCorrectSize() {
        assertEquals(40, BoardMap.tiles.size)
    }

    @Test
    fun testFirstTileIsStart() {
        val startTile = BoardMap.tiles[0]
        assertEquals("Los", startTile.name)
        assertEquals(TileType.START, startTile.type)
    }

    @Test
    fun testTileAtPosition10IsJail() {
        val jailTile = BoardMap.tiles[10]
        assertEquals("Gefängnis", jailTile.name)
        assertEquals(TileType.JAIL, jailTile.type)
    }

    @Test
    fun testAllTilesNotNull() {
        BoardMap.tiles.forEach { tile ->
            assertNotNull(tile.name)
            assertNotNull(tile.type)
        }
    }
}
