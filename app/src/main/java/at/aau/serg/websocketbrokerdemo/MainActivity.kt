import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.widget.Toast
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import at.aau.serg.websocketbrokerdemo.R
import at.aau.serg.websocketbrokerdemo.ListLobbyActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username)

        val enterButton = findViewById<Button>(R.id.enterButton)
        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)

        enterButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()

            if (username.isNotEmpty()) {
                val intent = Intent(this, ListLobbyActivity::class.java)
                intent.putExtra("USERNAME", username)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Bitte Username eingeben", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
