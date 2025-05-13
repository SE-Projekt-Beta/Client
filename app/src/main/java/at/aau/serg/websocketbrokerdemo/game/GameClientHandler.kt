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
            GameMessageType.DRAW_RISK_CARD,
            GameMessageType.DRAW_BANK_CARD -> handleEventCard(message.payload.asJsonObject)
            GameMessageType.PASS_START -> handlePassStart()
            GameMessageType.PAY_TAX -> handleTax()
            GameMessageType.GO_TO_JAIL -> handleGoToJail()
            GameMessageType.DICE_ROLLED -> handleDiceRolled(message.payload.asJsonObject)
            GameMessageType.CASH_TASK -> handleCashTask(message.payload.asJsonObject)
            GameMessageType.PLAYER_LOST -> handlePlayerLost(message.payload.asJsonObject)
            GameMessageType.GAME_OVER -> handleGameOver(message.payload.asJsonObject)
            GameMessageType.ERROR -> Log.e(TAG, "Fehler: ${message.payload}")
            else -> Log.w(TAG, "Unbekannter Nachrichtentyp: ${message.type}")
        }
    }

    private fun handleGameState(payload: JsonObject) {
        GameController.updateFromGameState(payload)

        val myId = LobbyClient.playerId
        val currentPlayerId = GameController.getCurrentPlayerId()
        val currentPlayerName = GameController.getCurrentPlayerName()
        val diceValue = payload["dice"]?.asInt ?: -1
        val fieldIndex = GameController.getCurrentFieldIndex(currentPlayerId)
        val tileName = GameController.getTileName(fieldIndex)
        val cash = GameController.getCash(currentPlayerId)

        activity.updateTurnView(currentPlayerId, currentPlayerName)
        activity.updateDice(diceValue)
        activity.updateTile(tileName)
        activity.updateCashDisplay(cash)
        activity.updateTokenPosition(diceValue)

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

    private fun handlePassStart() {
        activity.runOnUiThread {
            StartBonusDialog(activity, "Du bekommst dein Startgeld!").show()
        }
    }

    private fun handleTax() {
        activity.runOnUiThread {
            TaxDialog(activity, "Du musst Steuern zahlen!").show()
        }
    }

    private fun handleGoToJail() {
        activity.runOnUiThread {
            RiskCardDialog(activity,
                "Gefängnis",
                "Du wirst ins Gefängnis geschickt. 3 Runden Pause!"
            ).show()
        }
    }

    private fun handleDiceRolled(payload: JsonObject) {
        val steps = payload["steps"]?.asInt ?: return
        activity.updateDice(steps)
        activity.updateTokenPosition(steps)
    }

    private fun handleCashTask(payload: JsonObject) {
        val playerId = payload["playerId"]?.asInt ?: return
        val amount = payload["amount"]?.asInt ?: 0
        val newCash = payload["newCash"]?.asInt ?: return

        if (playerId == LobbyClient.playerId) {
            activity.updateCashDisplay(newCash)
        }

        Log.i(TAG, "CashTask: Spieler $playerId: Änderung $amount €, neuer Kontostand: $newCash €")
    }

    private fun handlePlayerLost(payload: JsonObject) {
        val playerId = payload["playerId"]?.asInt ?: return
        Log.i(TAG, "Spieler $playerId ist bankrott.")
        activity.showPlayerLost(playerId)
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
