package at.aau.serg.websocketbrokerdemo.game

data class PlayerState(
    val id: String,
    var name: String,
    var position: Int,
    var money: Int,
    var properties: List<Int>,  // IDs der gekauften Tiles
    var inJail: Boolean
)