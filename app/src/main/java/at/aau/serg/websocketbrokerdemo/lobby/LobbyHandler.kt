package at.aau.serg.websocketbrokerdemo.lobby

import android.content.Intent
import android.util.Log
import at.aau.serg.websocketbrokerdemo.LobbyActivity
import at.aau.serg.websocketbrokerdemo.MainActivity
import at.aau.serg.websocketbrokerdemo.UsernameActivity
import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyMessage
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyMessageType
import com.google.gson.JsonObject



class LobbyHandler(private val activity: LobbyActivity) : LobbyMessageListener {

    override fun onLobbyUpdate(usernames: List<String>) {
        activity.updateLobby(usernames)
    }

    override fun onStartGame() {
        activity.startGame()
    }
}