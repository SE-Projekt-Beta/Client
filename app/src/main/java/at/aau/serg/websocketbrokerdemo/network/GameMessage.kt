package at.aau.serg.websocketbrokerdemo.network

import com.google.gson.JsonElement

data class GameMessage(
    val type: MessageType,
    val payload: JsonElement
)
