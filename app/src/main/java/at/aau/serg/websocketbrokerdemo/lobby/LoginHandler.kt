package at.aau.serg.websocketbrokerdemo

import android.content.Intent
import android.util.Log
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.Gson
import com.google.gson.JsonObject

class LoginHandler(private val activity: UsernameActivity) : LobbyMessageListener {

    override fun onLobbyListUpdate(lobbies: List<LobbyDTO>) {
        Log.i("LoginHandler", "Lobby update received: $lobbies")

        val ownNickname = LobbyClient.username
        val userLobby = lobbies.find { lobby ->
            lobby.name.contains(ownNickname, ignoreCase = true)
        }

        if (userLobby != null) {
            Log.i("LoginHandler", "User's lobby found: ${userLobby.name}, starting LobbyActivity")

            val gson = Gson()
            val lobbyJson = gson.toJson(userLobby)

            val intent = Intent(activity, LobbyActivity::class.java).apply {
                putExtra("lobby_json", lobbyJson)
            }
            activity.startActivity(intent)
            activity.finish()
        } else {
            Log.i("LoginHandler", "User's lobby not found, ignoring update.")
        }
    }

    override fun onLobbyUpdate(players: List<PlayerDTO>) {
        Log.i("LoginHandler", "Lobby update received during login: $players")

        val ownNickname = LobbyClient.username
        val isOwnUserPresent = players.any { it.nickname == ownNickname }

        if (isOwnUserPresent) {
            Log.i("LoginHandler", "Nickname $ownNickname is in lobby, starting LobbyActivity")

            LobbyClient.setPlayers(players)

            val gson = Gson()
            val playersJson = gson.toJson(players)

            val intent = Intent(activity, LobbyActivity::class.java).apply {
                putExtra("players_json", playersJson)
            }
            activity.startActivity(intent)
            activity.finish()
        } else {
            Log.i("LoginHandler", "Own nickname $ownNickname not in list yet, ignoring update.")
        }
    }


    override fun onStartGame(payload: JsonObject) {

    }

}
