package at.aau.serg.websocketbrokerdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.websocketbrokerdemo.lobby.LobbyAdapter
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient.username
import at.aau.serg.websocketbrokerdemo.lobby.LobbyHandler
import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.LobbyStomp
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.example.myapplication.R
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class ListLobby : ComponentActivity() {

    private lateinit var lobbyRecyclerView: RecyclerView
    private lateinit var newLobbyButton: Button
    private lateinit var lobbyStomp: LobbyStomp
    private lateinit var lobbyHandler: LobbyHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lobby_list)

        lobbyRecyclerView = findViewById(R.id.lobbyRecyclerView)
        newLobbyButton = findViewById(R.id.newLobbyButton)

        lobbyHandler = LobbyHandler(this)
        lobbyStomp = LobbyStomp(object : LobbyMessageListener {
            override fun onLobbyListUpdate(lobbies: List<LobbyDTO>) {
                Log.i("ListLobby", "Lobby update received: $lobbies")
                runOnUiThread {
                    lobbyRecyclerView.adapter = LobbyAdapter(lobbies) { lobby ->
                        joinLobby(lobby)
                    }
                }
            }
            override fun onLobbyUpdate(players: List<PlayerDTO>) {
                Log.i("ListLobby", "Lobby update received: $players")
                // Handle lobby update
            }

            override fun onStartGame(payload: JsonObject) {
                // Handle start game
            }
        })

        lobbyRecyclerView.setHasFixedSize(true)
        lobbyRecyclerView.layoutManager = LinearLayoutManager(this)

        newLobbyButton.setOnClickListener {
            val intent = Intent(this, CreateLobbyActivity::class.java)
            startActivity(intent)
        }
        // register before calling connect
        lobbyStomp.setOnConnectedListener {
            // this will only run once the socket is open
            lobbyStomp.sendListLobbies()
        }
        lobbyStomp.connect()

    }

    private fun joinLobby(lobby: LobbyDTO) {

        username = LobbyClient.username

        // send a joinlobby via lobbyStomp
        lobbyStomp.sendJoinLobby(username, lobby.id)

        //set the lobbyid
        LobbyClient.lobbyId = lobby.id

        //log it
        Log.i("ListLobby", "Joining lobby: ${lobby.id}")
        val intent = Intent(this, LobbyActivity::class.java)
        intent.putExtra("lobbyId", lobby.id)
        // start the lobby activity
        startActivity(intent)
    }
}