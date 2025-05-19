package at.aau.serg.websocketbrokerdemo.game.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import at.aau.serg.websocketbrokerdemo.R

class BankCardDialog(
    context: Context,
    private val title: String,
    private val description: String,
    private val playerName: String
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_bank_card)

        findViewById<TextView>(R.id.bankCardTitle).text = title
        findViewById<TextView>(R.id.bankCardText).text = "$playerName: $description" // ANPASSUNG

        findViewById<Button>(R.id.okBankCardBtn).setOnClickListener {
            dismiss()
        }
    }
}
