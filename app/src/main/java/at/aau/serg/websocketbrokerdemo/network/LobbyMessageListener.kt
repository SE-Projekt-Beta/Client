package at.aau.serg.websocketbrokerdemo.network

import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.JsonObject

interface LobbyMessageListener {
    fun onLobbyListUpdate(lobbies: List<LobbyDTO>)
    fun onLobbyUpdate(players: List<PlayerDTO>)
    fun onStartGame(payload: JsonObject)
}