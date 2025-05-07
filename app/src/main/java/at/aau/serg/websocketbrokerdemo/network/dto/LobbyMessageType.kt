package at.aau.serg.websocketbrokerdemo.network.dto

enum class LobbyMessageType {
    CREATE_LOBBY,
    LIST_LOBBIES,
    JOIN_LOBBY,
    START_GAME,
    LOBBY_CREATED,
    LOBBY_LIST,
    LOBBY_UPDATE,
    ERROR
}