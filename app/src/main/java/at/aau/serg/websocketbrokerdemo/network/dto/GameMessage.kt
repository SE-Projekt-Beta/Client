package at.aau.serg.websocketbrokerdemo.network.dto

import com.google.gson.JsonElement

/**
 * Now carries a lobbyId so we know which game scope it belongs to.
 */
data class GameMessage(
    val lobbyId: Int?,
    val type: GameMessageType,
    val payload: JsonElement
)