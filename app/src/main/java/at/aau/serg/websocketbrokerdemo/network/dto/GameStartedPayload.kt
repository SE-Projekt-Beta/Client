package at.aau.serg.websocketbrokerdemo.network.dto

data class GameStartedPayload(
    var playerOrder: List<PlayerLobbyEntry> = emptyList()
)