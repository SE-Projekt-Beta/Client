package at.aau.serg.websocketbrokerdemo.game.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import at.aau.serg.websocketbrokerdemo.R

class TaxDialog(
    context: Context,
    private val message: String
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_tax)

        findViewById<TextView>(R.id.taxText).text = message
        findViewById<Button>(R.id.okTaxBtn).setOnClickListener { dismiss() }
    }
}