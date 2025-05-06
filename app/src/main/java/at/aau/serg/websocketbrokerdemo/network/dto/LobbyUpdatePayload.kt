package at.aau.serg.websocketbrokerdemo.network.dto

data class LobbyUpdatePayload(
    var players: List<PlayerLobbyEntry> = emptyList()
)