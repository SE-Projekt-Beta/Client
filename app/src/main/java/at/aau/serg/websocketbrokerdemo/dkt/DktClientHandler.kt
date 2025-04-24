package at.aau.serg.websocketbrokerdemo.dkt

import android.util.Log
import at.aau.serg.websocketbrokerdemo.MainActivity
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import com.google.gson.JsonElement
import com.google.gson.JsonObject

class DktClientHandler(
    private val activity: MainActivity
) {

    fun handle(message: GameMessage) {
        when (message.type) {
            GameMessageType.CURRENT_PLAYER -> handleCurrentPlayer(message.payload.asJsonObject)
            GameMessageType.PLAYER_MOVED -> handlePlayerMoved(message.payload.asJsonObject)
            GameMessageType.CAN_BUY_PROPERTY -> handleCanBuyProperty(message.payload.asJsonObject)
            GameMessageType.PROPERTY_BOUGHT -> handlePropertyBought(message.payload.asJsonObject)
            GameMessageType.MUST_PAY_RENT -> handleMustPayRent(message.payload.asJsonObject)
            GameMessageType.DRAW_EVENT_BANK_CARD, GameMessageType.DRAW_EVENT_RISIKO_CARD -> handleEventCard(message.payload)
            GameMessageType.ERROR -> handleError(message.payload.asString)
            else -> Log.w(TAG, "Unbekannter Typ: ${message.type}")
        }
    }

    private fun handlePlayerMoved(payload: JsonObject) {
        val playerId = payload.get("playerId").asString
        val pos = payload.get("pos").asInt
        val dice = payload.get("dice").asInt
        val tileName = payload.get("tileName").asString
        val tileType = payload.get("tileType").asString

        Log.i(TAG, "$playerId hat $dice gewürfelt auf $tileName ($tileType)")
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

        Log.i(TAG, "Kauf abgeschlossen: $tileName von $playerId")
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

    private fun handleError(errorMessage: String) {
        Log.e(TAG, "Fehler vom Server: $errorMessage")
    }

    companion object {
        private const val TAG = "DktClientHandler"
    }

    private fun handleCurrentPlayer(payload: JsonObject) {
        val currentPlayerId = payload.get("playerId").asString
        val myId = activity.getMyPlayerName()

        if (currentPlayerId == myId) {
            activity.enableDiceButton()
        } else {
            activity.disableDiceButton()
        }
    }

}