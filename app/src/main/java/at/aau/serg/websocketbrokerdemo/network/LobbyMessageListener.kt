package at.aau.serg.websocketbrokerdemo.network

import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.JsonObject

interface LobbyMessageListener {
    fun onLobbyUpdate(players: List<PlayerDTO>)
    fun onStartGame(payload: JsonObject)
}