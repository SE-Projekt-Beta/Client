package at.aau.serg.websocketbrokerdemo.lobby


import android.content.Intent
import at.aau.serg.websocketbrokerdemo.LobbyActivity
import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import android.util.Log
import at.aau.serg.websocketbrokerdemo.MainActivity
import com.google.gson.Gson
import com.google.gson.JsonObject


class LobbyHandler(private val activity: LobbyActivity) : LobbyMessageListener {

    override fun onStartGame(payload: JsonObject) {
        val orderJson = payload.getAsJsonArray("playerOrder")
        val order = orderJson.map {
            val obj = it.asJsonObject
            PlayerDTO(
                id = obj.get("id").asInt,
                nickname = obj.get("nickname").asString,
                position = obj.get("position").asInt,
                money = obj.get("money").asInt,
                properties = obj.get("properties").asJsonArray.map{it.asInt},
                inJail = obj.get("inJail").asBoolean,
                hasEscapedCard = obj.get("hasEscapedCard").asBoolean
            )
        }
        activity.startGame(order)
    }

    override fun onLobbyUpdate(players: List<PlayerDTO>) {
        LobbyClient.setPlayers(players)
        activity.updateLobby(players.map { it.nickname })
    }

}


