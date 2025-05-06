package at.aau.serg.websocketbrokerdemo.network.dto

import com.google.gson.JsonElement

data class LobbyMessage(
    val type: LobbyMessageType,
    val payload: JsonElement
)