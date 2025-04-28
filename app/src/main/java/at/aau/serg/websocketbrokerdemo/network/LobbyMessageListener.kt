package at.aau.serg.websocketbrokerdemo.network

import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import com.google.gson.JsonObject

interface LobbyMessageListener {
    fun onLobbyUpdate(lobbies: List<LobbyDTO>)
    fun onStartGame(payload: JsonObject)
}