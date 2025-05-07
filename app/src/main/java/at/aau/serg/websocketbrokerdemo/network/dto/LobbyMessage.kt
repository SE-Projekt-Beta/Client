package at.aau.serg.websocketbrokerdemo.network.dto

import com.google.gson.JsonElement

/**
 * Now carries an optional lobbyId (null for global ops).
 */
data class LobbyMessage(
    val lobbyId: Int ?= null,
    val type: LobbyMessageType,
    val payload: JsonElement
)