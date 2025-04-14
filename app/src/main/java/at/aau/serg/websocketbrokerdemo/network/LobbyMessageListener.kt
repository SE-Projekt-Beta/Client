package at.aau.serg.websocketbrokerdemo.network

import at.aau.serg.websocketbrokerdemo.network.dto.LobbyMessage

interface LobbyMessageListener {
    fun onLobbyUpdate(usernames: List<String>)
    fun onStartGame()
}