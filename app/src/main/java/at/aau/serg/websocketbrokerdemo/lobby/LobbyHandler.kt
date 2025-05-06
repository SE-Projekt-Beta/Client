package at.aau.serg.websocketbrokerdemo.lobby


import android.content.Intent
import at.aau.serg.websocketbrokerdemo.LobbyActivity
import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import android.util.Log
import at.aau.serg.websocketbrokerdemo.MainActivity
import at.aau.serg.websocketbrokerdemo.network.dto.GameStartedPayload
import com.google.gson.Gson
import com.google.gson.JsonObject


class LobbyHandler(private val activity: LobbyActivity) : LobbyMessageListener {

    override fun onStartGame(payload: GameStartedPayload) {
        val order: List<PlayerDTO> = payload.playerOrder.map { entry ->
            PlayerDTO(
                id       = entry.playerId,
                nickname = entry.nickname
            )
        }
        activity.startGame(order)
    }

    override fun onLobbyUpdate(players: List<PlayerDTO>) {
        LobbyClient.setPlayers(players)
        activity.updateLobby(players.map { it.nickname })
    }

}


