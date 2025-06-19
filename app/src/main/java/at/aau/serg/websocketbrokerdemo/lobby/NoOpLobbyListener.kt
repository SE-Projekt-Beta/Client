package at.aau.serg.websocketbrokerdemo

import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.JsonObject

class NoOpLobbyListener(private val activity: UsernameActivity) : LobbyMessageListener {
    override fun onLobbyListUpdate(lobbies: List<LobbyDTO>) {
        // No-op implementation: This listener does not react to lobby list updates.
    }
    override fun onLobbyUpdate(players: List<PlayerDTO>) {
        // Intentionally left empty: handled elsewhere
    }
    override fun onStartGame(payload: JsonObject) {
        // Implementation not required for NoOpLobbyListener
    }

}


