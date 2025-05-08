package at.aau.serg.websocketbrokerdemo.lobby

import android.content.Context
import android.util.Log
import at.aau.serg.websocketbrokerdemo.LobbyActivity
import at.aau.serg.websocketbrokerdemo.game.GameStateClient
import at.aau.serg.websocketbrokerdemo.game.OwnershipClient
import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.JsonObject

class LobbyHandler(private val context: Context) : LobbyMessageListener {

    override fun onLobbyListUpdate(lobbies: List<LobbyDTO>) {
        // Wird bei ListLobbyActivity verwendet
    }

    override fun onLobbyUpdate(players: List<PlayerDTO>) {
        LobbyClient.setPlayers(players)

        // Setze Player-ID, falls Benutzer vorhanden
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

        // 🔁 Spielreihenfolge extrahieren
        val orderJson = payload.getAsJsonArray("playerOrder")
        val order = orderJson.map {
            val obj = it.asJsonObject
            PlayerDTO(
                id = obj.get("id").asInt,
                nickname = obj.get("nickname").asString
            )
        }

        // 🧼 Wichtig: Reset Clients vor Spielstart
        OwnershipClient.reset()
        GameStateClient.setCurrentPlayerId(-1)

        if (context is LobbyActivity) {
            context.startGame(order)
        }
    }
}
