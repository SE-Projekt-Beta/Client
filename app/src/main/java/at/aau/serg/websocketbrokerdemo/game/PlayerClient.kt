package at.aau.serg.websocketbrokerdemo.game

data class PlayerClient(
    val id: Int,
    val nickname: String,
    var cash: Int,
    var position: Int,
    var alive: Boolean,
    var suspended: Boolean,
    var hasEscapeCard: Boolean,
    var houseCounts: MutableMap<Int, Int> = mutableMapOf(),
    var properties: MutableList<Int> = mutableListOf()
)

