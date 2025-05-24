package at.aau.serg.websocketbrokerdemo

import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.JsonObject

class NoOpLobbyListener(private val activity: UsernameActivity) : LobbyMessageListener {
    override fun onLobbyListUpdate(lobbies: List<LobbyDTO>) {
    }
    override fun onLobbyUpdate(players: List<PlayerDTO>) {
    }
    override fun onStartGame(payload: JsonObject) {
    }

}


