package at.aau.serg.websocketbrokerdemo.lobby

object LobbyClient {
    var playerName: String = ""
    private val players = mutableListOf<String>()

    fun setPlayers(newPlayers: List<String>) {
        players.clear()
        players.addAll(newPlayers)
    }

    fun allPlayers(): List<String> {
        return players
    }
}
