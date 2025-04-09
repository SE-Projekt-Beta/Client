package at.aau.serg.websocketbrokerdemo.ui.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentGameViewBinding

class GameViewActivity : AppCompatActivity() {
    private lateinit var binding: FragmentGameViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding = FragmentGameViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, GameViewFragment())
            .commit()

        findViewById<Button>(R.id.backBtn).setOnClickListener {
            finish() // closes DiceActivity and goes back to MainActivity
        }
    }
}


