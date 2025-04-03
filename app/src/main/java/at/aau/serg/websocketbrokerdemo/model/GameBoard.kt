package at.aau.serg.websocketbrokerdemo.model

import at.aau.serg.websocketbrokerdemo.model.tiles.*

data class GameBoard(
    val tiles: List<Tile>
) {
    fun getTileAt(position: Int): Tile {
        return tiles[position % tiles.size]
    }
}