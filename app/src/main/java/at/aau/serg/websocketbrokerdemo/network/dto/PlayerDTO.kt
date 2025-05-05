package at.aau.serg.websocketbrokerdemo.network.dto

data class PlayerDTO(
    val id: Int,
    val nickname: String,
    var position: Int,
    var money: Int,
    var properties: List<Int>? = null,  // IDs der gekauften Tiles
    var inJail: Boolean = false,
    var hasEscapedCard: Boolean = false
)