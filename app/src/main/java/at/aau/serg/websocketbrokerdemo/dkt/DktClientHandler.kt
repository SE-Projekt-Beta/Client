package at.aau.serg.websocketbrokerdemo.dkt

import android.util.Log

class DktClientHandler {
    fun handle(message: GameMessage) {
        when (message.type) {
            "test" -> handleTest(message.payload)
            "roll_dice" -> handleRollDice(message.payload)
            "buy_property" -> handleBuyProperty(message.payload)
            else -> Log.w("DktClientHandler", "Unbekannter Nachrichtentyp: ${message.type}")
        }
    }

    private fun handleTest(payload: String) {
        Log.i("DktClientHandler", "Test empfangen: $payload")
    }

    private fun handleRollDice(payload: String) {
        Log.i("DktClientHandler", "Würfeln erhalten: $payload")
        // TODO: Spielfigur bewegen, UI aktualisieren
    }

    private fun handleBuyProperty(payload: String) {
        Log.i("DktClientHandler", "Straße kaufen: $payload")
        // TODO: Kaufdialog anzeigen
    }
}