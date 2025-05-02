package at.aau.serg.websocketbrokerdemo.lobby
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO


object LobbyClient {
    var username: String = ""
    var lobbyId: String? = null
    private val players = mutableListOf<PlayerDTO>()

    fun setPlayers(newPlayers: List<PlayerDTO>) {
        players.clear()
        players.addAll(newPlayers)
    }

    fun allPlayers(): List<PlayerDTO> = players.toList()
}

