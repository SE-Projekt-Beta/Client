package at.aau.serg.websocketbrokerdemo.game

import android.util.Log
import at.aau.serg.websocketbrokerdemo.GameBoardActivity
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import com.google.gson.JsonObject

class GameClientHandler(
    private val activity: GameBoardActivity
) {

    fun handle(message: GameMessage) {
        Log.i(TAG, "Nachricht empfangen: $message")
        when (message.type) {
            GameMessageType.GAME_STATE -> handleGameState(message.payload.asJsonObject)
            GameMessageType.CURRENT_PLAYER -> handleCurrentPlayer(message.payload.asJsonObject)
            GameMessageType.PLAYER_MOVED -> handlePlayerMoved(message.payload.asJsonObject)
            GameMessageType.CAN_BUY_PROPERTY -> handleCanBuyProperty(message.payload.asJsonObject)
            GameMessageType.PROPERTY_BOUGHT -> handlePropertyBought(message.payload.asJsonObject)
            GameMessageType.MUST_PAY_RENT -> handleMustPayRent(message.payload.asJsonObject)
            GameMessageType.DRAW_EVENT_BANK_CARD -> handleDrawEventBankCard(message.payload.asJsonObject)
            GameMessageType.DRAW_EVENT_RISIKO_CARD -> handleDrawEventRisikoCard(message.payload.asJsonObject)
            GameMessageType.GO_TO_JAIL -> handleGoToJail(message.payload.asJsonObject)
            GameMessageType.ERROR -> handleError(message.payload.asString)
            else -> Log.w(TAG, "Unbekannter Typ: ${message.type}")
        }
    }

    private fun handleGameState(payload: JsonObject) {
        val currentPlayerId = payload.get("currentPlayerId").asString
        val currentRound = payload.get("currentRound").asInt
        val players = payload.get("players").asJsonArray
        val board = payload.get("board").asJsonArray

        Log.i(TAG, "Aktueller Spieler: $currentPlayerId")
        activity.updateGameState(currentPlayerId, currentRound, players, board)
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
        val tilePos = payload.get("tilePos").asInt

        Log.i(TAG, "Kauf abgeschlossen: $tileName von $playerId")
        GameStateClient.addProperty(playerId, tilePos)
        activity.showCurrentPlayerOwnership()
        activity.showResponse("Kauf abgeschlossen: $tileName für $playerId")
    }

    private fun handleMustPayRent(payload: JsonObject) {
        val playerId = payload.get("playerId").asString
        val ownerId = payload.get("ownerId").asString
        val tileName = payload.get("tileName").asString
        val amount = payload.get("amount").asInt

        Log.i(TAG, "$playerId muss Miete an $ownerId zahlen für $tileName")
        activity.showResponse("$playerId muss Miete an $ownerId zahlen für $tileName")

        GameStateClient.deductMoney(playerId, amount)
        GameStateClient.addMoney(ownerId, amount)
    }

    private fun handleDrawEventRisikoCard(payload: JsonObject) {
        val playerId = payload.get("playerId").asString
        val title = payload.get("title").asString
        val description = payload.get("description").asString

        Log.i(TAG, "Risikokarte: $title – $description")
        activity.showEventCard(title, "$description")

        // Eventuell Geld hinzufügen oder abziehen
    }

    private fun handleDrawEventBankCard(payload: JsonObject) {
        val playerId = payload.get("playerId").asString
        val title = payload.get("title").asString
        val description = payload.get("description").asString

        Log.i(TAG, "Bankkarte: $title – $description")
        activity.showEventCard(title, "$description")

        // Eventuell Geld hinzufügen oder abziehen
    }

    private fun handleGoToJail(payload: JsonObject) {
        val playerId = payload.get("playerId").asString
        val jailPos = payload.get("tilePos").asInt

        GameStateClient.updatePosition(playerId, jailPos)

        val message = "$playerId wurde ins Gefängnis geschickt! 🚔"
        Log.i(TAG, message)
        activity.showResponse(message)
        // activity.showJailDialog(playerId)
    }


    private fun handleError(errorMessage: String) {
        Log.e(TAG, "Fehler vom Server: $errorMessage")
    }

    companion object {
        private const val TAG = "DktClientHandler"
    }
}