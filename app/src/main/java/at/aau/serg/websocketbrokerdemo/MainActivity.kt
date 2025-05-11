import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import at.aau.serg.websocketbrokerdemo.R
import at.aau.serg.websocketbrokerdemo.network.GameStomp

class MainActivity : AppCompatActivity() {

    private lateinit var gameStomp: GameStomp
    private lateinit var gameId: String
    private lateinit var playerName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_fullscreen)

        val rollDiceBtn = findViewById<Button>(R.id.rollDiceBtn)

        rollDiceBtn.setOnClickListener {
            //TODO: implement what happens if rollDice is clicked
        }
    }
}
