package at.aau.serg.websocketbrokerdemo

import android.content.Intent
import android.util.Log
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.Gson

class LoginHandler(private val activity: UsernameActivity) : LobbyMessageListener {

    override fun onLobbyUpdate(players: List<PlayerDTO>) {
        Log.i("LoginHandler", "Lobby update received during login: $players")

        // Nur fortfahren, wenn der eigene Benutzername dabei ist
        val ownUsername = LobbyClient.username
        val isOwnUserPresent = players.any { it.username == ownUsername }

        if (isOwnUserPresent) {
            Log.i("LoginHandler", "Username $ownUsername is in lobby, starting LobbyActivity")

            // Spieler in LobbyClient speichern
            LobbyClient.setPlayers(players)

            // Spieler-Liste als JSON an LobbyActivity übergeben
            val gson = Gson()
            val playersJson = gson.toJson(players)

            val intent = Intent(activity, LobbyActivity::class.java).apply {
                putExtra("players_json", playersJson)
            }
            activity.startActivity(intent)
            activity.finish()
        } else {
            Log.i("LoginHandler", "Own user $ownUsername not in list yet, ignoring update.")
        }
    }


    override fun onStartGame() {
        // nicht benötigt hier
    }
}
