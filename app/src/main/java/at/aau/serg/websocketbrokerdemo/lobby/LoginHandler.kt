import android.content.Intent
import android.util.Log
import at.aau.serg.websocketbrokerdemo.LobbyActivity
import at.aau.serg.websocketbrokerdemo.UsernameActivity
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.lobby.LobbyHandler
import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyMessage
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyMessageType
import com.google.gson.JsonObject

class LoginHandler(private val activity: UsernameActivity) : LobbyMessageListener {

    override fun onLobbyUpdate(usernames: List<String>) {
        Log.i("LoginHandler", "Lobby update received during login: $usernames")
        if (LobbyClient.username.isEmpty()) return

        if (LobbyClient.username in usernames) {
            Log.i("LoginHandler", "Username ${LobbyClient.username} is in lobby, starting LobbyActivity")
            activity.runOnUiThread {
                activity.startLobbyActivity()
            }
        }
    }

    override fun onStartGame() {
        // nicht relevant im Login
    }
}