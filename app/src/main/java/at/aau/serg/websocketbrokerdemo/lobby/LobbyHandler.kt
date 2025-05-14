package at.aau.serg.websocketbrokerdemo.lobby

import android.content.Context
import android.util.Log
import at.aau.serg.websocketbrokerdemo.LobbyActivity
import at.aau.serg.websocketbrokerdemo.game.GameStateClient
import at.aau.serg.websocketbrokerdemo.game.PlayerClient
import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.JsonObject

class LobbyHandler(private val context: Context) : LobbyMessageListener {

    override fun onLobbyListUpdate(lobbies: List<LobbyDTO>) {
        // Wird nur von ListLobbyActivity genutzt
    }

    override fun onLobbyUpdate(players: List<PlayerDTO>) {
        LobbyClient.setPlayers(players)

        // Eigene Spieler-ID setzen
        val myId = players.firstOrNull { it.nickname == LobbyClient.username }?.id
        if (myId != null) {
            LobbyClient.playerId = myId
            Log.i("LobbyHandler", "Set playerId: $myId")
        }

        if (context is LobbyActivity) {
            context.updateLobby(players.map { it.nickname })
        }
    }

    override fun onStartGame(payload: JsonObject) {
        Log.i("LobbyHandler", "Received START_GAME payload: $payload")

        // Reihenfolge extrahieren
        val orderJson = payload.getAsJsonArray("playerOrder")
        val order = orderJson.map {
            val obj = it.asJsonObject
            PlayerDTO(
                id = obj["id"].asInt,
                nickname = obj["nickname"].asString
            )
        }

        // Spielerobjekte neu registrieren
        GameStateClient.players.clear()
        order.forEach {
            val newPlayer = PlayerClient(
                id = it.id,
                nickname = it.nickname,
                cash = 0,
                position = 0,
                alive = true,
                suspended = false,
                hasEscapeCard = false
            )
            GameStateClient.players[it.id] = newPlayer
        }

        // Current Player setzen
        val firstId = order.first().id
        GameStateClient.currentPlayerId = firstId

        if (context is LobbyActivity) {
            context.startGame(order)
        }
    }
}
