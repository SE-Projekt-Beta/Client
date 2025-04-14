package at.aau.serg.websocketbrokerdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.lobby.LobbyHandler
import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.LobbyStomp
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyMessage
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyMessageType
import com.example.myapplication.R
import com.google.gson.JsonObject

class LobbyActivity : AppCompatActivity() {

    private lateinit var lobbyStomp: LobbyStomp
    private lateinit var lobbyHandler: LobbyHandler
    private lateinit var playersTextView: TextView
    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        playersTextView = findViewById(R.id.playerListView)
        startButton = findViewById(R.id.buttonStart)

        lobbyHandler = LobbyHandler(this)
        lobbyStomp = LobbyStomp(lobbyHandler)
        lobbyStomp.connect()

        startButton.setOnClickListener {
            lobbyStomp.sendStartGame()
        }
    }

    fun updateLobby(players: List<String>) {
        runOnUiThread {
            Log.i("LobbyActivity", "Updating lobby with players: $players")
            playersTextView.text = players.joinToString("\n") { "- $it" }
            startButton.visibility = if (players.size >= 2) View.VISIBLE else View.GONE
        }
    }

    fun startGame() {
        runOnUiThread {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}