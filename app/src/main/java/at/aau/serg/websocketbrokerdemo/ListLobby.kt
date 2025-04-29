package at.aau.serg.websocketbrokerdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.websocketbrokerdemo.lobby.LobbyAdapter
import at.aau.serg.websocketbrokerdemo.lobby.LobbyHandler
import at.aau.serg.websocketbrokerdemo.network.LobbyMessageListener
import at.aau.serg.websocketbrokerdemo.network.LobbyStomp
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO
import com.example.myapplication.R
import com.google.gson.JsonObject

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
            override fun onLobbyUpdate(lobbies: List<LobbyDTO>) {
                Log.i("ListLobby", "Lobby update received: $lobbies")
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

        lobbyRecyclerView.setHasFixedSize(true)
        lobbyRecyclerView.layoutManager = LinearLayoutManager(this)

        newLobbyButton.setOnClickListener {
            val intent = Intent(this, CreateLobbyActivity::class.java)
            startActivity(intent)
            finish()
        }

        lobbyStomp.connect()

        // wait one secon
        Thread.sleep(1000)

        // Request the list of lobbies
        lobbyStomp.sendListLobbies()

    }

    private fun joinLobby(lobby: LobbyDTO) {
        // Handle joining the lobby
    }
}