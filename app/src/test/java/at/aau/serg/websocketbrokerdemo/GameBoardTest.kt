package at.aau.serg.websocketbrokerdemo

import at.aau.serg.websocketbrokerdemo.model.GameBoard
//import kotlin.test.assertEquals
//import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull



class GameBoardTest {

    @Test
    fun testFirstTileIsStart() {
        val firstTile = GameBoard.tiles[0]
        assertEquals("Los", firstTile.name)
        assertEquals(TileType.START, firstTile.type)
    }

    @Test
    fun testTileAtPosition10IsJail() {
        val jailTile = GameBoard.tiles[10]
        assertEquals("Gefängnis", jailTile.name)
        assertEquals(TileType.JAIL, jailTile.type)
    }

    @Test
    fun testTileListHasCorrectSize() {
        assertEquals(40, GameBoard.tiles.size)
    }

    @Test
    fun testAllTilesAreNotNull() {
        GameBoard.tiles.forEach {
            assertNotNull(it.name)
            assertNotNull(it.type)
        }
    }
}