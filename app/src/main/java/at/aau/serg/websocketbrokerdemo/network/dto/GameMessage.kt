package at.aau.serg.websocketbrokerdemo.network.dto

import com.google.gson.JsonElement

data class GameMessage(
    val type: GameMessageType,
    val payload: JsonElement
)
