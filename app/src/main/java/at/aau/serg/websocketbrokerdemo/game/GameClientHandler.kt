package at.aau.serg.websocketbrokerdemo.game

import android.util.Log
import at.aau.serg.websocketbrokerdemo.MainActivity
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject

class GameClientHandler(
    private val activity: MainActivity
) {

    fun handle(message: GameMessage) {
        when (message.type) {
            GameMessageType.PLAYER_UPDATE -> handlePlayerUpdate(message.payload.asJsonObject)
            GameMessageType.CURRENT_PLAYER -> handleCurrentPlayer(message.payload.asJsonObject)
            GameMessageType.PLAYER_MOVED -> handlePlayerMoved(message.payload.asJsonObject)
            GameMessageType.CAN_BUY_PROPERTY -> handleCanBuyProperty(message.payload.asJsonObject)
            GameMessageType.PROPERTY_BOUGHT -> handlePropertyBought(message.payload.asJsonObject)
            GameMessageType.MUST_PAY_RENT -> handleMustPayRent(message.payload.asJsonObject)
            GameMessageType.DRAW_EVENT_BANK_CARD -> handleDrawEventBankCard(message.payload.asString)
            GameMessageType.DRAW_EVENT_RISIKO_CARD -> handleDrawEventRisikoCard(message.payload.asString)
            GameMessageType.GO_TO_JAIL -> handleGoToJail(message.payload.asString)
            GameMessageType.ERROR -> handleError(message.payload.asString)
            else -> Log.w(TAG, "Unbekannter Typ: ${message.type}")
        }
    }

    private fun handlePlayerUpdate(payload: JsonObject) {
        val player = Gson().fromJson(payload.toString(), PlayerDTO::class.java)
        GameStateClient.updatePlayer(player)
        Log.i(TAG, "Spielerzustand aktualisiert: $player")
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

    private fun handlePlayerMoved(payload: JsonObject) {
        val playerId = payload.get("playerId").asString
        val pos = payload.get("pos").asInt
        val dice = payload.get("dice").asInt
        val tileName = payload.get("tileName").asString
        val tileType = payload.get("tileType").asString

        Log.i(TAG, "$playerId hat $dice gewürfelt auf $tileName ($tileType)")
        activity.showResponse("$playerId → $tileName ($tileType), gewürfelt: $dice")

        GameStateClient.updatePosition(playerId, pos)
        GameStateClient.setDiceRoll(dice)
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

    private fun handleGoToJail(payload: String) {
        val json = JSONObject(payload)
        val playerId = json.getString("playerId")
        val jailPos = 10 // Position des Gefängnisses

        GameStateClient.updatePosition(playerId, jailPos)

        val message = "$playerId wurde ins Gefängnis geschickt! 🚔"
        Log.i(TAG, message)
        activity.showResponse(message)
        activity.showJailDialog(playerId)
    }


    private fun handleError(errorMessage: String) {
        Log.e(TAG, "Fehler vom Server: $errorMessage")
    }

    companion object {
        private const val TAG = "DktClientHandler"
    }
}