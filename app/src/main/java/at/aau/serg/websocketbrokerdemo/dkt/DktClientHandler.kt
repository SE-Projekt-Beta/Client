package at.aau.serg.websocketbrokerdemo.dkt

import org.json.JSONObject
import android.util.Log

class DktClientHandler {
    fun handle(message: GameMessage) {
        when (message.type) {
            "test" -> handleTest(message.payload)
            "dice_result" -> handleDiceResult(message.payload)
            "buy_property" -> handleBuyProperty(message.payload)
            "player_moved" -> handlePlayerMoved(message.payload)
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

    private fun handlePlayerMoved(payload: String) {
        val json = JSONObject(payload)
        val playerId = json.getString("playerId")
        val pos = json.getInt("pos")
        val dice = json.getInt("dice")
        val tileName = json.getString("tileName")
        val tileType = json.getString("tileType")

        Log.i("DktClientHandler", "$playerId hat $dice gewürfelt und ist auf Feld $pos gelandet: $tileName ($tileType)")
        GameStateClient.updatePosition(playerId, pos)
    }

}