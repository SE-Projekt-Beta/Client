package at.aau.serg.websocketbrokerdemo.network.dto

data class InitPlayerPayload(
    var playerId: Int = 0,
    var nickname: String = ""
)