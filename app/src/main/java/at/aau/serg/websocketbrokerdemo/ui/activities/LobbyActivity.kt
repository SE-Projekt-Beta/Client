package at.aau.serg.websocketbrokerdemo.ui.activities

import MyStomp
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import at.aau.serg.websocketbrokerdemo.dkt.GameMessage
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.lobby.LobbyHandler
import com.example.myapplication.R
import org.json.JSONObject
import androidx.appcompat.app.AppCompatDelegate

class LobbyActivity : AppCompatActivity() {

    private lateinit var playerListView: TextView
    private lateinit var buttonJoin: Button
    private lateinit var buttonStart: Button

    private lateinit var mystomp: MyStomp
    private lateinit var lobbyHandler: LobbyHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        initViews()
        setupNetwork()
        setupButtons()
    }

    private fun initViews() {
        playerListView = findViewById(R.id.playerListView)
        buttonJoin = findViewById(R.id.buttonJoin)
        buttonStart = findViewById(R.id.buttonStart)
        buttonStart.visibility = View.GONE
    }

    private fun setupNetwork() {
        lobbyHandler = LobbyHandler(this)
        mystomp = MyStomp(lobbyHandler = lobbyHandler)
        mystomp.connect()
    }

    private fun setupButtons() {
        buttonJoin.setOnClickListener { sendJoinLobby() }
        buttonStart.setOnClickListener { startGame() }
    }

    private fun sendJoinLobby() {
        if (LobbyClient.playerName.isNotEmpty()) {
            // Schon beigetreten
            return
        }

        // Kein PlayerName mehr erzeugen!
        val joinPayload = JSONObject() // einfach leer

        mystomp.sendGameMessage(GameMessage("join_lobby", joinPayload.toString()))

        // Button deaktivieren, damit man nicht doppelt klickt
        runOnUiThread {
            buttonJoin.isEnabled = false
        }
    }



    private fun generatePlayerName(): String {
        return "Player" + (1..1000).random()
    }

    fun showLobby() {
        runOnUiThread {
            playerListView.text = LobbyClient.allPlayers().joinToString("\n")
            buttonStart.visibility = if (LobbyClient.allPlayers().size >= 2) View.VISIBLE else View.GONE
        }
    }

    private fun startGame() {
        // Sende Start-Befehl an den Server
        mystomp.sendGameMessage(GameMessage("start_game", ""))
    }

}
