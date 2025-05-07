package at.aau.serg.websocketbrokerdemo.network.dto

data class LobbyDTO(
    val id: Int,
    val name: String,
    val playerCount: Int,
)