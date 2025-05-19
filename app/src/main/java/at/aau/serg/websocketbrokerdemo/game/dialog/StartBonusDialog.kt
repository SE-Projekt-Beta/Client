package at.aau.serg.websocketbrokerdemo.game.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import at.aau.serg.websocketbrokerdemo.R

class StartBonusDialog(
    context: Context,
    private val playerName: String,
    private val amount: Int
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_start_bonus)

        val titleView = findViewById<TextView>(R.id.startTitle)
        val textView = findViewById<TextView>(R.id.startText)
        val okButton = findViewById<Button>(R.id.okStartBtn)

        titleView.text = context.getString(R.string.startbonus)
        textView.text = "$playerName ist auf dem Startfeld gelandet und erhält $amount€."

        okButton.setOnClickListener { dismiss() }
    }
}
