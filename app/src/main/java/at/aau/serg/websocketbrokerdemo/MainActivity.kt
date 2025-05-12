/*import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import at.aau.serg.websocketbrokerdemo.R
import at.aau.serg.websocketbrokerdemo.game.GameClientHandler
import at.aau.serg.websocketbrokerdemo.game.Player
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.network.GameStomp

class MainActivity : AppCompatActivity() {

    private lateinit var gameStomp: GameStomp
    private lateinit var dktHandler: GameClientHandler
    private lateinit var lobby: LobbyClient
    private lateinit var gameId: String //hä??
    private lateinit var player: Player


    private lateinit var usernameEditText: EditText
    private lateinit var enterButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_fullscreen)

        val rollDiceBtn = findViewById<Button>(R.id.rollDiceBtn)

        gameStomp = GameStomp(dktHandler, lobby.lobbyId, )
        gameStomp = GameStomp()
        gameStomp.connect()

        rollDiceBtn.setOnClickListener {
            //TODO: implement what happens if rollDice is clicked
        }
    }
}
*/