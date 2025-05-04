//package at.aau.serg.websocketbrokerdemo
//
//import android.os.Bundle
//import android.widget.Button
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import at.aau.serg.websocketbrokerdemo.game.GameClientHandler
//import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
//import at.aau.serg.websocketbrokerdemo.network.GameStomp
//import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
//import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
//import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
//import com.example.myapplication.R
//import com.google.gson.Gson
//import com.google.gson.JsonObject
//import com.google.gson.reflect.TypeToken
//
//class GameBoardActivity : AppCompatActivity(), GameClientHandler {
//
//    private lateinit var tvUsername: TextView
//    private lateinit var tvPlayerOrder: TextView
//    private lateinit var tvTurnIndicator: TextView
//    private lateinit var btnRollDice: Button
//    private lateinit var tvDiceResult: TextView
//
//    private lateinit var gameStomp: GameStomp
//    private lateinit var playerOrder: List<PlayerDTO>
//    private var currentPlayerIndex = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_game)
//
//        // wire up views
//        tvUsername      = findViewById(R.id.tvUsername)
//        tvPlayerOrder   = findViewById(R.id.tvPlayerOrder)
//        tvTurnIndicator = findViewById(R.id.tvTurnIndicator)
//        btnRollDice     = findViewById(R.id.btnRollDice)
//        tvDiceResult    = findViewById(R.id.tvDiceResult)
//
//        // read extras
//        val username = intent.getStringExtra("USERNAME") ?: "Unknown"
//        tvUsername.text = "Player: $username"
//
//        val orderJson = intent.getStringExtra("ORDER")
//        playerOrder = if (orderJson != null) {
//            val type = object : TypeToken<List<PlayerDTO>>() {}.type
//            Gson().fromJson(orderJson, type)
//        } else emptyList()
//
//        // show turn order
//        tvPlayerOrder.text = "Order: " + playerOrder.joinToString { it.nickname }
//
//        // figure out who's up first
//        currentPlayerIndex = playerOrder.indexOfFirst { it.nickname == username }
//        if (currentPlayerIndex == -1) currentPlayerIndex = 0
//        updateTurnIndicator()
//
//        // set up STOMP for this lobby
//        gameStomp = GameStomp(this, LobbyClient.lobbyId)
//        gameStomp.connect()
//
//        // roll‐dice button
//        btnRollDice.setOnClickListener {
//            val payload = JsonObject().apply {
//                addProperty("playerId", playerOrder[currentPlayerIndex].id)
//
