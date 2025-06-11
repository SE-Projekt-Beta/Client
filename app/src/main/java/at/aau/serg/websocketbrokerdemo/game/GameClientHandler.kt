package at.aau.serg.websocketbrokerdemo.game

import android.util.Log
import at.aau.serg.websocketbrokerdemo.GameBoardActivity
import at.aau.serg.websocketbrokerdemo.game.dialog.RiskCardDialog
import at.aau.serg.websocketbrokerdemo.game.dialog.StartBonusDialog
import at.aau.serg.websocketbrokerdemo.game.dialog.TaxDialog
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import com.google.gson.JsonObject
import at.aau.serg.websocketbrokerdemo.game.dialog.BankCardDialog
import kotlin.text.get


class GameClientHandler(
    private val activity: GameBoardActivity
) {
    fun handle(message: GameMessage) {
        Log.i(TAG, "Message type: ${message.type}")
        when (message.type) {
            GameMessageType.GAME_STATE -> handleGameState(message.payload.asJsonObject)
            GameMessageType.ASK_BUY_PROPERTY -> handleAskBuyProperty(message.payload.asJsonObject)
            GameMessageType.ASK_PAY_PRISON -> handleAskPayPrison(message.payload.asJsonObject)
            GameMessageType.ROLLED_PRISON -> handleRolledPrison(message.payload.asJsonObject)
            GameMessageType.DRAW_RISK_CARD -> handleRiskCardDrawn(message.payload.asJsonObject)
            GameMessageType.DRAW_BANK_CARD -> handleBankCardDrawn(message.payload.asJsonObject)
            GameMessageType.PASS_START -> handlePassStart(message)
            GameMessageType.PAY_TAX -> handleTax(message.payload.asJsonObject)
            GameMessageType.GO_TO_JAIL -> handleGoToJail(message.payload.asJsonObject)
            GameMessageType.DICE_ROLLED -> handleDiceRolled(message.payload.asJsonObject)
            GameMessageType.CASH_TASK -> handleCashTask(message.payload.asJsonObject)
            GameMessageType.PLAYER_LOST -> handlePlayerLost(message.payload.asJsonObject)
            GameMessageType.GAME_OVER -> handleGameOver(message.payload.asJsonObject)
            GameMessageType.EXTRA_MESSAGE -> handleExtraMessage(message.payload.asJsonObject)
            GameMessageType.ERROR -> Log.e(TAG, "Fehler: ${message.payload}")
            else -> Log.w(TAG, "Unbekannter Nachrichtentyp: ${message.type}")
        }
    }

    private fun handleAskBuyProperty(payload: JsonObject) {

        // check if the playerid is this player
        val playerId = payload["playerId"]?.asInt ?: return

        if (playerId != LobbyClient.playerId) {
            Log.i(TAG, "Spieler $playerId kann eine Immobilie kaufen.")
            return
        }

        val fieldIndex = payload["fieldIndex"]?.asInt ?: return
        val tileName = GameController.getTileName(fieldIndex)
        val price = GameController.getTilePrice(fieldIndex)

        if (playerId == LobbyClient.playerId) {
            val options = GameController.evaluateTileOptions(playerId, fieldIndex)
            activity.showBuyOptions(fieldIndex, tileName, options.canBuy, options.canBuildHouse, options.canBuildHotel)
            activity.showBuyDialog(fieldIndex, tileName)
            activity.disableDiceButton()
        } else {
            Log.i(TAG, "Spieler $playerId möchte $tileName kaufen.")
        }
    }

    private fun handleRolledPrison(payload: JsonObject) {
        val playerId = payload["playerId"]?.asInt ?: return
        val dice1 = payload["roll1"]?.asInt ?: return
        val dice2 = payload["roll2"]?.asInt ?: return
        if (playerId == LobbyClient.playerId) {
            Log.i(TAG, "Spieler $playerId hat die Würfel $dice1 und $dice2 geworfen.")
            activity.showRollPrisonDialog(dice1, dice2)
        } else {
            Log.i(TAG, "Spieler $playerId hat die Würfel $dice1 und $dice2 geworfen.")
        }
    }

    private fun handleAskPayPrison(payload: JsonObject) {
        val playerId = payload["playerId"]?.asInt ?: return
        if (playerId == LobbyClient.playerId) {
            activity.showPayPrisonDialog()
        } else {
            Log.i(TAG, "Spieler $playerId muss Gefängnisgeld zahlen.")
        }
    }

    private fun handleGameState(payload: JsonObject) {
        Log.i(TAG, "Received GAME_STATE payload: $payload")
        GameController.updateFromGameState(payload)

        val myId = LobbyClient.playerId
        val currentPlayerId = GameController.getCurrentPlayerId()
        val currentPlayerName = GameController.getCurrentPlayerName()
        val fieldIndex = GameController.getCurrentFieldIndex(myId)
        val tileName = GameController.getTileName(fieldIndex)
        val cash = GameController.getCash(myId)

        val playersJson = payload["players"]?.toString() ?: "No players"
        Log.i(TAG, "Players JSON: $playersJson")
        activity.updateTestView(playersJson)

        // Update all player tokens based on their current position in the game state
        try {
            val gson = com.google.gson.Gson()
            val type = object : com.google.gson.reflect.TypeToken<List<at.aau.serg.websocketbrokerdemo.game.PlayerClient>>() {}.type
            val players: List<at.aau.serg.websocketbrokerdemo.game.PlayerClient> = gson.fromJson(playersJson, type)
            players.forEach { player ->
                activity.setPlayerTokenPosition(player.id, player.position)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update player tokens from playersJson", e)
        }

        activity.updateTurnView(currentPlayerId, currentPlayerName)
        activity.updateTile(tileName, fieldIndex)
        activity.updateCashDisplay(cash)

        if (myId == currentPlayerId) {
            activity.disableDiceButton()    // Würfel ausblenden
            activity.enableDiceButton()
            val options = GameController.evaluateTileOptions(currentPlayerId, fieldIndex)
            activity.showBuyOptions(fieldIndex, tileName, options.canBuy, options.canBuildHouse, options.canBuildHotel)
        } else {
            activity.disableDiceView()
            activity.disableDiceButton()
            activity.hideActionButtons()
        }
    }

    private fun handleBankCardDrawn(payload: JsonObject) {
        val playerId = payload["playerId"]?.asInt ?: return
        val amount = payload["amount"]?.asInt ?: return
        val newCash = payload["newCash"]?.asInt ?: return
        val description = payload["description"]?.asString ?: return

        if (playerId == LobbyClient.playerId) {
            activity.updateCashDisplay(newCash)
        }

        val playerName = GameStateClient.getNickname(playerId) ?: "Ein Spieler"

        activity.runOnUiThread {
            BankCardDialog(activity, "Bankkarte", description, playerName).show()
        }
    }



    private fun handleRiskCardDrawn(payload: JsonObject) {
        val playerId = payload["playerId"]?.asInt ?: return
        val title = payload["title"]?.asString ?: "Risiko-Karte"
        val description = payload["description"]?.asString ?: return

        val playerName = GameStateClient.getNickname(playerId) ?: "Ein Spieler"

        activity.runOnUiThread {
            RiskCardDialog(activity, title, description, playerName).show()
        }
    }




    private fun handlePassStart(message: GameMessage) {
        val payload = message.payload.asJsonObject
        val playerId = payload["playerId"]?.asInt ?: return
        val amount = payload["amount"]?.asInt ?: return
        val landed = payload["landed"]?.asBoolean ?: false

        if (landed) {
            val nickname = GameStateClient.getNickname(playerId) ?: "Ein Spieler"
            activity.runOnUiThread {
                StartBonusDialog(activity, nickname, amount).show()
            }
        } else {
            if (playerId == LobbyClient.playerId) {
                val newCash = GameController.getCash(playerId)
                activity.updateCashDisplay(newCash)
            }
            Log.i(TAG, "Spieler $playerId hat Start überquert und $amount€ erhalten.")
        }
    }


    private fun handleTax(payload: JsonObject) {
        val playerId = payload["playerId"]?.asInt ?: return
        val tileName = payload["tileName"]?.asString ?: "Steuer"
        val amount = payload["amount"]?.asInt ?: return
        val newCash = payload["newCash"]?.asInt ?: return

        if (playerId == LobbyClient.playerId) {
            activity.updateCashDisplay(newCash)
        }

        val nickname = GameStateClient.getNickname(playerId) ?: "Ein Spieler"
        val message = "$nickname muss $amount€ zahlen."

        activity.runOnUiThread {
            TaxDialog(activity, tileName, message).show()
        }
    }


    private fun handleGoToJail(payload: JsonObject) {
        val playerId = payload["playerId"]?.asInt ?: return
        val playerName = GameStateClient.getNickname(playerId) ?: "Ein Spieler"

        if (playerId != LobbyClient.playerId) {
            Log.i(TAG, "Spieler $playerId wird ins Gefängnis geschickt.")
            return
        }

        activity.runOnUiThread {
            RiskCardDialog(
                activity,
                "Gefängnis",
                "wird ins Gefängnis geschickt. 3 Runden Pause!",
                playerName
            ).show()
        }
    }


    private fun handleDiceRolled(payload: JsonObject) {
        val steps1 = payload["roll1"]?.asInt ?: return
        val steps2 = payload["roll2"]?.asInt ?: return
        val rollingPlayerId = payload["playerId"]?.asInt ?: GameController.getCurrentPlayerId()
        val myId = LobbyClient.playerId

        if (rollingPlayerId == myId) {
            activity.updateDice(steps1, steps2)     // Einzelne Würfel setzen
            activity.disableDiceButton()        // Würfel-Button ausblenden
            activity.enableDiceView()       // Würfelbilder einbeldnen
        } else {
            // Andere sehen keine Würfel
            activity.disableDiceView()
            activity.disableDiceButton()
        }

        // Bewegung der Spielfigur
        activity.updateTokenPosition(steps1 + steps2)
        Log.d("TokenDebug", "Calling updateTokenPosition after rolling dice")

        // Wurf ist vollständig angekommen, wir können schütteln wieder erlauben
        activity.onRollFinished()
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
        val winnerId = payload["winnerId"]?.asInt ?: return
        val winnerName = payload["winnerName"]?.asString ?: return
        activity.showGameOverDialog(winnerName)
    }

    private fun handleExtraMessage(payload: JsonObject) {
        val playerId = payload["playerId"]?.asInt ?: return
        val title = payload["title"]?.asString ?: "Extra Message"
        val message = payload["message"]?.asString ?: return
        Log.i(TAG, "Extra message: $message for player $playerId")
        if (playerId != LobbyClient.playerId) {
            Log.i(TAG, "Extra message for player $playerId, not showing to current player.")
            return
        }
        activity.showDialog(title, message)
    }

    companion object {
        private const val TAG = "GameClientHandler"
    }
}
