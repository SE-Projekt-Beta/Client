package at.aau.serg.websocketbrokerdemo.game

class Player(
    val id: Int,
    val nickname: String,
    var position: Int,
    var cash: Int,
    var properties: List<Int>?,  // IDs der gekauften Tiles
    var suspensionRounds: Int,
    var inJail: Boolean,
    var hasEscapedCard: Boolean,
    var cheatFlag: Boolean
)