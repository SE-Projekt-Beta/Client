package at.aau.serg.websocketbrokerdemo.lobby

import android.content.Intent
import android.util.Log
import at.aau.serg.websocketbrokerdemo.LobbyActivity
import at.aau.serg.websocketbrokerdemo.MainActivity
import at.aau.serg.websocketbrokerdemo.network.GameMessage
import at.aau.serg.websocketbrokerdemo.network.MessageType
import com.google.gson.JsonObject

class LobbyHandler(private val activity: LobbyActivity) {

    fun handle(message: GameMessage) {
        when (message.type) {
            MessageType.LOBBY_UPDATE -> handleLobbyUpdate(message.payload.asJsonObject)
            MessageType.START_GAME -> handleStartGame()
            else -> Log.w("LobbyHandler", "Unbekannter Typ: ${message.type}")
        }
    }

    private fun handleLobbyUpdate(payload: JsonObject) {
        val playersJson = payload.getAsJsonArray("players")

        val players = mutableListOf<String>()
        for (playerElement in playersJson) {
            players.add(playerElement.asString)
        }

        LobbyClient.setPlayers(players)
        activity.showLobby()
    }

    private fun handleStartGame() {
        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtra("PLAYER_NAME", LobbyClient.playerName)
        activity.startActivity(intent)
        activity.finish()
    }
}