package at.aau.serg.websocketbrokerdemo.lobby
import android.util.Log
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO


object LobbyClient {
    var username: String = ""
    var playerId: Int = -1

    var lobbyId: Int = -1
        set(value) {
            Log.d("LobbyClient", "lobbyId geändert: $field → $value")
            field = value
            if (value == -1) {
                Log.w("LobbyClient", "⚠️ Lobby-ID wurde auf -1 gesetzt!")
                Thread.dumpStack() // Gibt den Stacktrace aus, der das verursacht hat
            }
        }

    var lobbyName: String = ""
    private val players = mutableListOf<PlayerDTO>()

    fun setPlayers(newPlayers: List<PlayerDTO>) {
        players.clear()
        players.addAll(newPlayers)
    }

    fun allPlayers(): List<PlayerDTO> = players.toList()
}