package at.aau.serg.websocketbrokerdemo.dkt

import android.util.Log

class DktClientHandler {
    fun handle(message: GameMessage) {
        when (message.type) {
            "test" -> handleTest(message.payload)
            "dice_result" -> handleDiceResult(message.payload)
            "buy_property" -> handleBuyProperty(message.payload)
            else -> Log.w("DktClientHandler", "Unbekannter Nachrichtentyp: ${message.type}")
        }
    }

    private fun handleTest(payload: String) {
        Log.i("DktClientHandler", "Test empfangen: $payload")
    }

    private fun handleDiceResult(payload: String) {
        Log.i("DktClientHandler", "Würfelergebnis empfangen: $payload")
        // TODO: später Spielfigur bewegen oder UI aktualisieren
    }


    private fun handleBuyProperty(payload: String) {
        Log.i("DktClientHandler", "Straße kaufen: $payload")
        // TODO: Kaufdialog anzeigen
    }
}