package at.aau.serg.websocketbrokerdemo.model.tiles

import at.aau.serg.websocketbrokerdemo.model.Tile

class Street(position: Int, name: String?, val price: Int, val rent: Int, val houseCost: Int) :
    Tile(position, name)
