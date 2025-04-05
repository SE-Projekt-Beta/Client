package at.aau.serg.websocketbrokerdemo

import MyStomp
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import at.aau.serg.websocketbrokerdemo.dkt.DktClientHandler
import at.aau.serg.websocketbrokerdemo.dkt.GameMessage
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.lobby.LobbyHandler
import com.example.myapplication.R
import org.json.JSONObject

class LobbyActivity : AppCompatActivity() {

    private lateinit var playerListView: TextView
    private lateinit var buttonJoin: Button
    private lateinit var buttonStart: Button

    private lateinit var mystomp: MyStomp
    private lateinit var lobbyHandler: LobbyHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

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
        val playerName = generatePlayerName()

        val joinPayload = JSONObject().apply {
            put("playerName", playerName)
        }

        LobbyClient.playerName = playerName
        mystomp.sendGameMessage(GameMessage("join_lobby", joinPayload.toString()))
    }

    private fun generatePlayerName(): String {
        return "Player" + (1..1000).random()
    }

    fun showLobby() {
        runOnUiThread {
            playerListView.text = "Lobby:\n" + LobbyClient.allPlayers().joinToString("\n")
            buttonStart.visibility = if (LobbyClient.allPlayers().size >= 2) View.VISIBLE else View.GONE
        }
    }

    private fun startGame() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
