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
        assertEquals("Gefängnis", jailTile.name)
        assertEquals(TileType.PRISON, jailTile.type)
    }

    @Test
    fun testAllTilesNotNull() {
        ClientBoardMap.tiles.forEach { tile ->
            assertNotNull(tile.name)
            assertNotNull(tile.type)
        }
    }

    @Test
    fun testGetTileReturnsCorrectTile() {
        val tile = ClientBoardMap.getTile(1)
        assertNotNull(tile)
        assertEquals(1, tile?.index)
        assertEquals("Start", tile?.name)
        assertEquals(TileType.START, tile?.type)
    }

    @Test
    fun testGetTileReturnsNullForInvalidIndex() {
        val tile = ClientBoardMap.getTile(999) // gibt es nicht
        assertEquals(null, tile)
    }

    @Test
    fun testFirstTileHasCorrectPosition() {
        val startTile = ClientBoardMap.getTile(1)
        assertNotNull(startTile?.position)
        assertEquals(1937f, startTile?.position?.x)
        assertEquals(1937f, startTile?.position?.y)
    }
}
