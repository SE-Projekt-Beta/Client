package at.aau.serg.websocketbrokerdemo

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.websocketbrokerdemo.lobby.LobbyAdapter
import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.LobbyStomp
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import com.example.myapplication.R
import com.google.gson.JsonObject

class MainActivity : ComponentActivity() {

    private lateinit var lobbyRecyclerView: RecyclerView
    private lateinit var createLobbyButton: Button
    private lateinit var lobbyStomp: LobbyStomp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lobbyRecyclerView = findViewById(R.id.lobbyRecyclerView)
        createLobbyButton = findViewById(R.id.createLobbyButton)

        lobbyRecyclerView.layoutManager = LinearLayoutManager(this)
        lobbyStomp = LobbyStomp(object : LobbyMessageListener {
            override fun onLobbyUpdate(lobbies: List<LobbyDTO>) {
                runOnUiThread {
                    lobbyRecyclerView.adapter = LobbyAdapter(lobbies) { lobby ->
                        joinLobby(lobby)
                    }
                }
            }

            override fun onStartGame(payload: JsonObject) {
                // Handle start game
            }
        })

        createLobbyButton.setOnClickListener {
            lobbyStomp.sendCreateLobby()
        }

        lobbyStomp.connect()
    }

    private fun joinLobby(lobby: LobbyDTO) {
        // Handle joining the lobby
    }
}