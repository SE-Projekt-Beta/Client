package at.aau.serg.websocketbrokerdemo.lobby

import at.aau.serg.websocketbrokerdemo.LobbyActivity
import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.JsonObject

class LobbyHandler(private val activity: LobbyActivity) : LobbyMessageListener {

    override fun onStartGame(payload: JsonObject) {
        val orderJson = payload.getAsJsonArray("playerOrder")
        val order = orderJson.map {
            val obj = it.asJsonObject
            PlayerDTO(
                id = obj.get("id").asInt,
                nickname = obj.get("nickname").asString
            )
        }
        activity.startGame(order)
    }

    override fun onLobbyUpdate(lobbies: List<LobbyDTO>) {
        //Your Implementation here.

    }
}