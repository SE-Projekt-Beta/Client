package at.aau.serg.websocketbrokerdemo.network

import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO

interface LobbyMessageListener {
    fun onLobbyUpdate(players: List<PlayerDTO>)
    fun onStartGame()
}