package at.aau.serg.websocketbrokerdemo.game

data class Player(
    val id: String,
    val nickname: String,
    var position: Int = 0,
    var cash: Int = 1500,
    var properties: List<Int> = emptyList(),  // IDs der gekauften Tiles
    var suspensionRounds: Int = 0,
    var inJail: Boolean = false,
    var hasEscapedCard: Boolean = false,
    var cheatFlag: Boolean = false
)