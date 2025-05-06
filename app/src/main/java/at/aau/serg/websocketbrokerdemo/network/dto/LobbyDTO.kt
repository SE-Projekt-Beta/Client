package at.aau.serg.websocketbrokerdemo.network.dto

data class LobbyDTO(
    val id: String,
    val name: String,
    val playerCount: Int,
)