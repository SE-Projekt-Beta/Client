package at.aau.serg.websocketbrokerdemo.lobby

import android.content.Intent
import android.util.Log
import at.aau.serg.websocketbrokerdemo.ui.activities.LobbyActivity
import at.aau.serg.websocketbrokerdemo.MainActivity
import at.aau.serg.websocketbrokerdemo.dkt.GameMessage
import org.json.JSONObject

class LobbyHandler(private val activity: LobbyActivity) {

    fun handle(message: GameMessage) {
        Log.i("LobbyHandler", "Empfange Nachricht: ${message.type}")
        when (message.type) {
            "lobby_update" -> handleLobbyUpdate(message.payload)
            "start_game" -> handleStartGame()
            else -> Log.w("LobbyHandler", "Unbekannter Nachrichtentyp: ${message.type}")
        }
    }




    private fun handleLobbyUpdate(payload: String) {
        val json = JSONObject(payload)
        val playersJson = json.getJSONArray("players")

        // Liste der Spieler aktualisieren
        val players = mutableListOf<String>()
        for (i in 0 until playersJson.length()) {
            players.add(playersJson.getString(i))
        }

        // Speichern
        LobbyClient.setPlayers(players)

        // Ansicht aktualisieren
        activity.showLobby()
    }

    private fun handleStartGame() {
        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtra("PLAYER_NAME", LobbyClient.playerName)
        activity.startActivity(intent)
        activity.finish()
    }





}