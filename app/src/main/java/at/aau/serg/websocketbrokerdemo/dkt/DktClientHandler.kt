package at.aau.serg.websocketbrokerdemo.dkt

import android.util.Log
import at.aau.serg.websocketbrokerdemo.MainActivity
import at.aau.serg.websocketbrokerdemo.network.GameMessage
import at.aau.serg.websocketbrokerdemo.network.MessageType
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONObject

class DktClientHandler(
    private val activity: MainActivity
) {

    fun handle(message: GameMessage) {
        when (message.type) {
            MessageType.PLAYER_MOVED -> handlePlayerMoved(message.payload.asJsonObject)
            MessageType.CAN_BUY_PROPERTY -> handleCanBuyProperty(message.payload.asJsonObject)
            MessageType.PROPERTY_BOUGHT -> handlePropertyBought(message.payload.asJsonObject)
            MessageType.MUST_PAY_RENT -> handleMustPayRent(message.payload.asJsonObject)
            MessageType.DRAW_EVENT_RISIKO_CARD, MessageType.DRAW_EVENT_BANK_CARD -> handleEventCard(message.payload)
            else -> Log.w(TAG, "Unbekannter Typ: ${message.type}")
        }
    }

    private fun handlePlayerMoved(payload: JsonObject) {
        val playerId = payload.get("playerId").asString
        val pos = payload.get("pos").asInt
        val dice = payload.get("dice").asInt
        val tileName = payload.get("tileName").asString
        val tileType = payload.get("tileType").asString

        Log.i(TAG, "$playerId hat $dice gewürfelt und ist auf Feld $pos gelandet: $tileName ($tileType)")
        activity.showResponse("$playerId → $tileName ($tileType), gewürfelt: $dice")

        GameStateClient.updatePosition(playerId, pos)
    }

    private fun handleCanBuyProperty(payload: JsonObject) {
        val tileName = payload.get("tileName").asString
        val tilePos = payload.get("tilePos").asInt
        val playerId = payload.get("playerId").asString

        Log.i(TAG, "$playerId darf $tileName kaufen!")
        activity.showBuyButton(tileName, tilePos, playerId)
        activity.showResponse("$playerId darf $tileName kaufen")
    }

    private fun handlePropertyBought(payload: JsonObject) {
        val playerId = payload.get("playerId").asString
        val tileName = payload.get("tileName").asString

        Log.i(TAG, "Feld erfolgreich gekauft: $tileName von $playerId")
        OwnershipClient.addProperty(playerId, tileName)
        activity.showOwnership()
        activity.showResponse("Kauf abgeschlossen: $tileName für $playerId")
    }

    private fun handleMustPayRent(payload: JsonObject) {
        val playerId = payload.get("playerId").asString
        val ownerId = payload.get("ownerId").asString
        val tileName = payload.get("tileName").asString

        Log.i(TAG, "$playerId muss Miete an $ownerId zahlen für $tileName")
        activity.showResponse("$playerId muss Miete an $ownerId zahlen für $tileName")
    }

    private fun handleEventCard(payload: JsonElement) {
        Log.i(TAG, "Ereigniskarte gezogen: $payload")
        activity.showEventCard(payload.toString())
    }

    companion object {
        private const val TAG = "DktClientHandler"
    }
}
