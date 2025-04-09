package at.aau.serg.websocketbrokerdemo.lobby

import android.content.Intent
import at.aau.serg.websocketbrokerdemo.LobbyActivity
import at.aau.serg.websocketbrokerdemo.MainActivity
import at.aau.serg.websocketbrokerdemo.dkt.GameMessage
import at.aau.serg.websocketbrokerdemo.ui.theme.MyLog
import org.json.JSONObject

class LobbyHandler(private val activity: LobbyActivity) {

    fun handle(message: GameMessage) {
        MyLog.i(TAG, "Empfange Nachricht: ${message.type}")
        when (message.type) {
            "lobby_update" -> handleLobbyUpdate(message.payload)
            "start_game" -> handleStartGame()
            else -> MyLog.w(TAG, "Unbekannter Nachrichtentyp: ${message.type}")
        }
    }

    private fun handleLobbyUpdate(payload: String) {
        val json = JSONObject(payload)
        val playersJson = json.getJSONArray("players")

        val players = mutableListOf<String>()
        for (i in 0 until playersJson.length()) {
            players.add(playersJson.getString(i))
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

    companion object {
        private const val TAG = "LobbyHandler"
    }
}