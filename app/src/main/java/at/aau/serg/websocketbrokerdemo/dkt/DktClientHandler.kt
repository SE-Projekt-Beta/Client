package at.aau.serg.websocketbrokerdemo.dkt

import android.util.Log
import at.aau.serg.websocketbrokerdemo.MainActivity
import at.aau.serg.websocketbrokerdemo.dkt.GameStateClient
import at.aau.serg.websocketbrokerdemo.dkt.OwnershipClient
import org.json.JSONObject

class DktClientHandler(
    private val activity: MainActivity
) {

    fun handle(message: GameMessage) {
        when (message.type) {
            "dice_result" -> handleDiceResult(message.payload)
            "buy_property" -> handleBuyProperty(message.payload)
            "player_moved" -> handlePlayerMoved(message.payload)
            "can_buy_property" -> handleCanBuyProperty(message.payload)
            "property_bought" -> handlePropertyBought(message.payload)
            "draw_event_risiko_card" -> handleDrawEventRisikoCard(message.payload)
            "draw_event_bank_card" -> handleDrawEventBankCard(message.payload)
            "must_pay_rent" -> handleMustPayRent(message.payload)
            else -> Log.w(TAG, "Unbekannter Nachrichtentyp: ${message.type}")
        }
    }

    private fun handleDiceResult(payload: String) {
        logAndShow("Gewürfelergebnis", payload)
    }

    private fun handleBuyProperty(payload: String) {
        logAndShow("Kaufversuch", payload)
    }

    private fun handlePlayerMoved(payload: String) {
        val json = JSONObject(payload)
        val playerId = json.getString("playerId")
        val pos = json.getInt("pos")
        val dice = json.getInt("dice")
        val tileName = json.getString("tileName")
        val tileType = json.getString("tileType")

        Log.i(TAG, "$playerId hat $dice gewürfelt und ist auf Feld $pos gelandet: $tileName ($tileType)")
        activity.showResponse("$playerId → $tileName ($tileType), gewürfelt: $dice")

        GameStateClient.updatePosition(playerId, pos)
    }

    private fun handleCanBuyProperty(payload: String) {
        val json = JSONObject(payload)
        val tileName = json.getString("tileName")
        val tilePos = json.getInt("tilePos")
        val playerId = json.getString("playerId")

        Log.i(TAG, "$playerId darf $tileName kaufen!")
        activity.showBuyButton(tileName, tilePos, playerId)
        activity.showResponse("$playerId darf $tileName kaufen")
    }

    private fun handlePropertyBought(payload: String) {
        val json = JSONObject(payload)
        val playerId = json.getString("playerId")
        val tileName = json.getString("tileName")

        Log.i(TAG, "Feld erfolgreich gekauft: $tileName von $playerId")
        OwnershipClient.addProperty(playerId, tileName)
        activity.showOwnership()
        activity.showResponse("Kauf abgeschlossen: $tileName für $playerId")
    }

    private fun handleDrawEventRisikoCard(payload: String) {
        try {
            val json = JSONObject(payload)
            val title = json.optString("eventTitle", "Unbekannter Titel")
            val description = json.optString("eventDescription", "Keine Beschreibung")
            val amount = json.optInt("eventAmount", 0)

            Log.i(TAG, "Risikokarte: $title – $description (${amount}€)")
            activity.showEventCard(title, "$description\nBetrag: $amount€")

        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Parsen der Risikokarte: ${e.message}")
            activity.showResponse("⚠️ Fehler beim Anzeigen der Risikokarte")
        }
    }

    private fun handleDrawEventBankCard(payload: String) {
        try {
            val json = JSONObject(payload)
            val title = json.optString("eventTitle", "Unbekannter Titel")
            val description = json.optString("eventDescription", "Keine Beschreibung")
            val amount = json.optInt("eventAmount", 0)

            Log.i(TAG, "Bankkarte: $title – $description (${amount}€)")
            activity.showEventCard(title, "$description\nBetrag: $amount€")

        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Parsen der Bankkarte: ${e.message}")
            activity.showResponse("⚠️ Fehler beim Anzeigen der Bankkarte")
        }
    }

    private fun handleMustPayRent(payload: String) {
        val json = JSONObject(payload)
        val playerId = json.getString("playerId")
        val ownerId = json.getString("ownerId")
        val tileName = json.getString("tileName")

        Log.i(TAG, "$playerId muss Miete an $ownerId zahlen für $tileName")
        activity.showResponse("$playerId muss Miete an $ownerId zahlen für $tileName")
    }

    private fun logAndShow(title: String, payload: String) {
        Log.i(TAG, "$title: $payload")
        activity.showResponse("$title: $payload")
    }

    companion object {
        private const val TAG = "DktClientHandler"
    }
}
