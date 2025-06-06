package at.aau.serg.websocketbrokerdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.network.LobbyStomp

class UsernameActivity : AppCompatActivity() {

    private lateinit var lobbyStomp: LobbyStomp
    private lateinit var noOpLobbyListener: NoOpLobbyListener
    private lateinit var usernameEditText: EditText
    private lateinit var enterButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username)

        usernameEditText = findViewById(R.id.usernameEditText)
        enterButton = findViewById(R.id.enterButton)

        noOpLobbyListener = NoOpLobbyListener(this)
        lobbyStomp = LobbyStomp(noOpLobbyListener)
        lobbyStomp.connect()

        enterButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            if (username.isNotEmpty()) {
                LobbyClient.username = username
                Log.i("UsernameActivity", "Username set: $username")

                lobbyStomp.sendCreateUser(username)

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

