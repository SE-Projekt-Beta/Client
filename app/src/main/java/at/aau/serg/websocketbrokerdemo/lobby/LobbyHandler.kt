package at.aau.serg.websocketbrokerdemo.lobby


import at.aau.serg.websocketbrokerdemo.LobbyActivity
import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import android.util.Log


class LobbyHandler(private val activity: LobbyActivity) : LobbyMessageListener {

    override fun onLobbyUpdate(players: List<PlayerDTO>) {
        Log.i("LobbyHandler", "onLobbyUpdate called with players: $players")
        LobbyClient.setPlayers(players)
        activity.updateLobby(players.map { it.username })
    }

    override fun onStartGame() {
        activity.startGame()
    }
}


