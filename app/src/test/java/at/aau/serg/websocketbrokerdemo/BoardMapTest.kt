package at.aau.serg.websocketbrokerdemo

import at.aau.serg.websocketbrokerdemo.model.BoardMap
import at.aau.serg.websocketbrokerdemo.model.TileType
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

class BoardMapTest {

    @Test
    fun testFirstTileIsStart() {
        val firstTile = BoardMap.tiles[0]
        assertEquals("Los", firstTile.name)
        assertEquals(TileType.START, firstTile.type)
    }

    @Test
    fun testTileAtPosition10IsJail() {
        val jailTile = BoardMap.tiles[10]
        assertEquals("Gefängnis", jailTile.name)
        assertEquals(TileType.JAIL, jailTile.type)
    }

    @Test
    fun testTileListHasCorrectSize() {
        assertEquals(40, BoardMap.tiles.size)
    }

    @Test
    fun testAllTilesAreNotNull() {
        BoardMap.tiles.forEach {
            assertNotNull(it.name)
            assertNotNull(it.type)
        }
    }
}