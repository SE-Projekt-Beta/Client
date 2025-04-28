
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
import at.aau.serg.websocketbrokerdemo.network.LobbyStomp
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.example.myapplication.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

        // Spieler direkt beim Start aus übergebenem JSON anzeigen
        val gson = Gson()
        val json = intent.getStringExtra("players_json")

        if (json != null) {
            val type = object : TypeToken<List<PlayerDTO>>() {}.type
            val players: List<PlayerDTO> = gson.fromJson(json, type)
            LobbyClient.setPlayers(players)
            updateLobby(players.map { it.nickname })
        } else {
            // Fallback, falls keine Liste übergeben wurde
            val existingPlayers = LobbyClient.allPlayers()
            if (existingPlayers.isNotEmpty()) {
                updateLobby(existingPlayers.map { it.nickname })
            } else {
                updateLobby(emptyList())
            }
        }

        startButton.setOnClickListener {
            lobbyStomp.sendStartGame()
        }
    }

    fun updateLobby(playerNames: List<String>) {
        runOnUiThread {
            Log.i("LobbyActivity", "Updating lobby with players: $playerNames")
            playersTextView.text = if (playerNames.isNotEmpty()) {
                playerNames.joinToString("\n") { "- $it" }
            } else {
                "Waiting for players..."
            }

            startButton.visibility = if (playerNames.size >= 2) View.VISIBLE else View.GONE
        }
    }

    fun startGame(order: List<PlayerDTO>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("USERNAME", LobbyClient.username)
            putExtra("ORDER", Gson().toJson(order))
        }
        startActivity(intent)
        finish()
    }

}
