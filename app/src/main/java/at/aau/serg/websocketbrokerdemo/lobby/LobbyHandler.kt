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

    override fun onLobbyUpdate(players: List<PlayerDTO>) {
        Log.i("LobbyHandler", "onLobbyUpdate called with players: $players")
        LobbyClient.setPlayers(players)
        activity.updateLobby(players.map { it.username })
    }

    override fun onStartGame(payload: JsonObject) {
        val playerOrderJson = payload.getAsJsonArray("playerOrder")
        val order = playerOrderJson.map {
            val obj = it.asJsonObject
            PlayerDTO(
                id = obj.get("id").asString,
                username = obj.get("username").asString
            )
        }

        LobbyClient.setPlayers(order) // Reihenfolge speichern

        val intent = Intent(activity, MainActivity::class.java).apply {
            putExtra("players_json", Gson().toJson(order))
            putExtra("USERNAME", LobbyClient.username)
        }
        activity.startActivity(intent)
        activity.finish()
    }

}


