package at.aau.serg.websocketbrokerdemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import at.aau.serg.websocketbrokerdemo.lobby.LobbyHandler
import at.aau.serg.websocketbrokerdemo.network.LobbyStomp
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO

class CreateLobbyActivity : ComponentActivity() {

    private lateinit var lobbyHandler: LobbyHandler
    private lateinit var lobbyNameInput: EditText
    private lateinit var createLobbyButton: Button
    private lateinit var lobbyStomp: LobbyStomp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_lobby)

        val username = intent.getStringExtra("USERNAME") ?: "unknown"

        lobbyNameInput = findViewById(R.id.lobbyNameInput)
        createLobbyButton = findViewById(R.id.createLobbyButton)

        lobbyHandler = LobbyHandler(this)
        lobbyStomp = LobbyStomp(lobbyHandler)
        lobbyStomp.connect()

        createLobbyButton.setOnClickListener {
            val lobbyName = lobbyNameInput.text.toString()
            if (lobbyName.isNotEmpty()) {
                lobbyStomp.sendCreateLobby(lobbyName)
            }
            val intent = Intent(this, ListLobbyActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
            finish()
        }

    }

    private fun joinLobby(lobby: LobbyDTO) {
        // Handle joining the lobby
    }
}