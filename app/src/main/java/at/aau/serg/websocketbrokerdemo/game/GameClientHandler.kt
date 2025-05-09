package at.aau.serg.websocketbrokerdemo.game

import android.util.Log
import at.aau.serg.websocketbrokerdemo.GameBoardActivity
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import com.google.gson.JsonObject

class GameClientHandler(
    private val activity: GameBoardActivity
) {
    fun handle(message: GameMessage) {
        when (message.type) {
            GameMessageType.GAME_STATE -> handleGameState(message.payload.asJsonObject)
            GameMessageType.EVENT_CARD_DRAWN -> handleEventCard(message.payload.asJsonObject)
            GameMessageType.PLAYER_LOST -> handlePlayerLost(message.payload.asJsonObject)
            GameMessageType.GAME_OVER -> handleGameOver(message.payload.asJsonObject)
            GameMessageType.ERROR -> Log.e(TAG, "Fehler: ${message.payload}")
            else -> Log.w(TAG, "Unbekannter Nachrichtentyp: ${message.type}")
        }
    }

    private fun handleGameState(payload: JsonObject) {
        // Update Clients
        GameController.updateFromGameState(payload)

        val myId = LobbyClient.playerId
        val currentPlayerId = GameController.getCurrentPlayerId()
        val currentPlayerName = GameController.getCurrentPlayerName()
        val diceValue = payload["dice"]?.asInt ?: -1
        val fieldIndex = GameController.getCurrentFieldIndex(currentPlayerId)
        val tileName = GameController.getTileName(fieldIndex)
        val cash = GameController.getCash(currentPlayerId)

        // Spielfeld-UI aktualisieren
        activity.updateTurnView(currentPlayerId, currentPlayerName)
        activity.updateDice(diceValue)
        activity.updateTile(tileName)
        activity.updateCashDisplay(cash)

        // Buttons und Feldaktionen nur für aktuellen Spieler anzeigen
        if (myId == currentPlayerId) {
            activity.enableDiceButton()
            val options = GameController.evaluateTileOptions(currentPlayerId, fieldIndex)
            activity.showBuyOptions(fieldIndex, tileName, options.canBuy, options.canBuildHouse, options.canBuildHotel)
        } else {
            activity.disableDiceButton()
            activity.hideActionButtons()
        }
    }

    private fun handleEventCard(payload: JsonObject) {
        val title = payload["title"]?.asString ?: "Ereignis"
        val description = payload["description"]?.asString ?: ""
        activity.showEventCard(title, description)
    }

    private fun handlePlayerLost(payload: JsonObject) {
        val playerId = payload["playerId"]?.asInt ?: -1
        Log.i(TAG, "Spieler $playerId ist bankrott.")
        // Optional: Spieler als ausgeschieden markieren oder anzeigen
    }

    private fun handleGameOver(payload: JsonObject) {
        val ranking = payload["ranking"]?.asJsonArray
            ?.joinToString("\n") { it.asString }
            ?: "Unbekannt"
        activity.showGameOverDialog(ranking)
    }

    companion object {
        private const val TAG = "GameClientHandler"
    }
}
