package at.aau.serg.websocketbrokerdemo.lobby

import android.content.Context
import at.aau.serg.websocketbrokerdemo.LobbyActivity
import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.JsonObject

class LobbyHandler(private val context: Context) : LobbyMessageListener {

    override fun onStartGame(payload: JsonObject) {
        if (context is LobbyActivity) {
            val orderJson = payload.getAsJsonArray("playerOrder")
            val order = orderJson.map {
                val obj = it.asJsonObject
                PlayerDTO(
                    id = obj.get("id").asInt,
                    nickname = obj.get("nickname").asString
                )
            }
            context.startGame(order)
        }
    }

    override fun onLobbyUpdate(lobbies: List<LobbyDTO>) {
        // Your Implementation here.
    }
}
