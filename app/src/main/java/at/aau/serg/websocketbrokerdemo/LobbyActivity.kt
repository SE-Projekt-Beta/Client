// ✅ LobbyActivity.kt
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LobbyActivity : AppCompatActivity() {

    private lateinit var lobbyStomp: LobbyStomp
    private lateinit var lobbyHandler: LobbyHandler
    private lateinit var playersTextView: TextView
    private lateinit var titleLobby: TextView
    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        titleLobby = findViewById(R.id.titleLobby)
        playersTextView = findViewById(R.id.playerListView)
        startButton = findViewById(R.id.buttonStart)

        val lobbyName = LobbyClient.lobbyName
        val lobbyId = LobbyClient.lobbyId
        titleLobby.text = getString(R.string.lobby_name, lobbyName, lobbyId)

        lobbyHandler = LobbyHandler(this)
        lobbyStomp = LobbyStomp(lobbyHandler)
        lobbyStomp.connect()

        val gson = Gson()
        val json = intent.getStringExtra("players_json")

        if (json != null) {
            val type = object : TypeToken<List<PlayerDTO>>() {}.type
            val players: List<PlayerDTO> = gson.fromJson(json, type)
            LobbyClient.setPlayers(players)
            updateLobby(players.map { it.nickname })
        } else {
            val existingPlayers = LobbyClient.allPlayers()
            updateLobby(existingPlayers.map { it.nickname })
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
        val intent = Intent(this, GameBoardActivity::class.java).apply {
            putExtra("USERNAME", LobbyClient.username)
            putExtra("players_json", Gson().toJson(order)) // ✅ fixed key name
        }
        startActivity(intent)
        finish()
    }
}