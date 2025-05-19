package at.aau.serg.websocketbrokerdemo.game.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import at.aau.serg.websocketbrokerdemo.R

class RiskCardDialog(
    context: Context,
    private val title: String,
    private val description: String,
    private val playerName: String // NEU
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_risk_card)

        findViewById<TextView>(R.id.riskCardTitle).text = title
        findViewById<TextView>(R.id.riskCardText).text = "$playerName: $description" // ANPASSUNG

        findViewById<Button>(R.id.okRiskCardBtn).setOnClickListener {
            dismiss()
        }
    }
}
