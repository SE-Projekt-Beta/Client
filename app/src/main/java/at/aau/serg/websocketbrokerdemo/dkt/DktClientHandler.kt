package at.aau.serg.websocketbrokerdemo.dkt

import org.json.JSONObject
import android.util.Log
import at.aau.serg.websocketbrokerdemo.MainActivity


class DktClientHandler(private val activity: MainActivity) {

    fun handle(message: GameMessage) {
        when (message.type) {
            "dice_result" -> handleDiceResult(message.payload)
            "buy_property" -> handleBuyProperty(message.payload)
            "player_moved" -> handlePlayerMoved(message.payload)
            "can_buy_property" -> handleCanBuyProperty(message.payload)
            "property_bought" -> handlePropertyBought(message.payload)
            "draw_event_card" -> handleDrawEventCard(message.payload)
            "must_pay_rent" -> handleMustPayRent(message.payload)
            "event_card" -> handleEventCard(message.payload)
            else -> Log.w("DktClientHandler", "Unbekannter Nachrichtentyp: ${message.type}")
        }
    }

    private fun handleDiceResult(payload: String) {
        Log.i("DktClientHandler", "Würfelergebnis empfangen: $payload")
        activity.showResponse("Gewürfelt: $payload")
    }

    private fun handleBuyProperty(payload: String) {
        Log.i("DktClientHandler", "Kaufversuch: $payload")
        activity.showResponse("Kaufe: $payload")
    }

    private fun handlePlayerMoved(payload: String) {
        val json = JSONObject(payload)
        val playerId = json.getString("playerId")
        val pos = json.getInt("pos")
        val dice = json.getInt("dice")
        val tileName = json.getString("tileName")
        val tileType = json.getString("tileType")

        Log.i("DktClientHandler", "$playerId hat $dice gewürfelt und ist auf Feld $pos gelandet: $tileName ($tileType)")
        activity.showResponse("$playerId → $tileName ($tileType), gewürfelt: $dice")
        GameStateClient.updatePosition(playerId, pos)
    }

    private fun handleCanBuyProperty(payload: String) {
        val json = JSONObject(payload)
        val tileName = json.getString("tileName")
        val tilePos = json.getInt("tilePos")
        val playerId = json.getString("playerId")

        Log.i("DktClientHandler", "$playerId darf $tileName kaufen!")
        activity.showBuyButton(tileName, tilePos, playerId)
        activity.showResponse("$playerId darf $tileName kaufen")
    }

    private fun handlePropertyBought(payload: String) {
        val json = JSONObject(payload)
        val playerId = json.getString("playerId")
        val tileName = json.getString("tileName")

        Log.i("DktClientHandler", "Feld erfolgreich gekauft: $tileName von $playerId")
        OwnershipClient.addProperty(playerId, tileName)
        activity.showOwnership()
        activity.showResponse("Kauf abgeschlossen: $tileName für $playerId")
    }


    private fun handleDrawEventCard(payload: String) {
        Log.i("DktClientHandler", "Ereigniskarte ziehen: $payload")
        activity.showResponse("Ereigniskarte: $payload")
    }

    private fun handleMustPayRent(payload: String) {
        val json = JSONObject(payload)
        val playerId = json.getString("playerId")
        val ownerId = json.getString("ownerId")
        val tileName = json.getString("tileName")

        Log.i("DktClientHandler", "$playerId muss Miete an $ownerId zahlen für $tileName")
        activity.showResponse("$playerId muss Miete an $ownerId zahlen für $tileName")
    }
    private fun handleEventCard(payload: String) {
        Log.i("DktClientHandler", "Ereigniskarte gezogen: $payload")
        activity.showEventCard(payload)
    }
}

