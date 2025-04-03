package at.aau.serg.websocketbrokerdemo.model.tiles

import at.aau.serg.websocketbrokerdemo.model.Tile

class Tax(position: Int, name: String?, amount: Int) : Tile(position, name) {
    var amount: Int = 0

    init {
        this.amount = amount
    }
}
