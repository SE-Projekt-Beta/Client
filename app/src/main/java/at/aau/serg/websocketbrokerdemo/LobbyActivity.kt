package at.aau.serg.websocketbrokerdemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R

class LobbyActivity : AppCompatActivity() {

    private lateinit var playerListView: TextView
    private lateinit var buttonJoin: Button
    private lateinit var buttonStart: Button

    private val players = mutableListOf<String>() // Spieler-Namen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        playerListView = findViewById(R.id.playerListView)
        buttonJoin = findViewById(R.id.buttonJoin)
        buttonStart = findViewById(R.id.buttonStart)

        buttonStart.visibility = View.GONE // Start-Button am Anfang ausblenden

        buttonJoin.setOnClickListener {
            joinLobby()
        }

        buttonStart.setOnClickListener {
            startGame()
        }
    }

    private fun joinLobby() {
        // Spieler hinzufügen
        val playerName = "Player${players.size + 1}"
        players.add(playerName)
        updatePlayerList()

        // Wenn mindestens 2 Spieler da sind, Start-Button einblenden
        if (players.size >= 2) {
            buttonStart.visibility = View.VISIBLE
        }
    }

    private fun updatePlayerList() {
        val text = "Lobby:\n" + players.joinToString("\n")
        playerListView.text = text
    }

    private fun startGame() {
        // Wechsel zu MainActivity (Spielbrett)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Lobby schließen
    }
}