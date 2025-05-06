package at.aau.serg.websocketbrokerdemo



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.network.LobbyStomp
import at.aau.serg.websocketbrokerdemo.R
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient.lobbyId

class UsernameActivity : AppCompatActivity() {

    private lateinit var lobbyStomp: LobbyStomp
    private lateinit var loginHandler: LoginHandler
    private lateinit var usernameEditText: EditText
    private lateinit var enterButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username)

        usernameEditText = findViewById(R.id.usernameEditText)
        enterButton = findViewById(R.id.enterButton)

        loginHandler = LoginHandler(this)
        lobbyStomp = LobbyStomp(loginHandler)
        lobbyStomp.connect()

        enterButton.setOnClickListener {
            lobbyId = LobbyClient.lobbyId
            val username = usernameEditText.text.toString().trim()
            if (username.isNotEmpty()) {
                LobbyClient.username = username
                Log.i("UsernameActivity", "Username set: $username")

                // check if lobbyId is not null
                if (lobbyId == null) {
                    Toast.makeText(this, "Lobby ID is null", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // join the lobby
                lobbyStomp.sendJoinLobby(lobbyId!!, username)

                // Navigate to the lobby screen
                val intent = Intent(this, ListLobbyActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

