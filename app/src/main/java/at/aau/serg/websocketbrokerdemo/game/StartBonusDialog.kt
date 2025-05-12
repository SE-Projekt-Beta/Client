package at.aau.serg.websocketbrokerdemo.game

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import at.aau.serg.websocketbrokerdemo.R

class StartBonusDialog(
    context: Context,
    private val message: String
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_pass_start)

        findViewById<TextView>(R.id.startText).text = message
        findViewById<Button>(R.id.okStartBtn).setOnClickListener { dismiss() }
    }
}
