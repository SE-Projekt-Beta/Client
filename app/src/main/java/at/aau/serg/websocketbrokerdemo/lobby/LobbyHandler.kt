package at.aau.serg.websocketbrokerdemo.lobby

import android.content.Context
import android.util.Log
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
            Log.i("LobbyHandler", "StartGame received, order: $order")
            context.startGame(order)
        }
    }


    override fun onLobbyListUpdate(lobbies: List<LobbyDTO>) {
        // Optional: Wenn du z. B. eine ListLobbyActivity hast, dort aktualisieren
    }

    override fun onLobbyUpdate(players: List<PlayerDTO>) {
        LobbyClient.setPlayers(players)

        // ID für den aktuellen Benutzer speichern
        val myId = players.firstOrNull { it.nickname == LobbyClient.username }?.id
        if (myId != null) {
            LobbyClient.playerId = myId
        }

        if (context is LobbyActivity) {
            context.updateLobby(players.map { it.nickname })
        }
    }

}