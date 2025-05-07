package at.aau.serg.websocketbrokerdemo.game

import android.util.Log
import at.aau.serg.websocketbrokerdemo.lobby.LobbyClient
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessage
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import com.google.gson.JsonObject

class GameClientHandler(
    private val showResponse: (String) -> Unit,
    private val enableDiceButton: () -> Unit,
    private val disableDiceButton: () -> Unit,
    private val showBuyButton: (tileName: String, tilePos: Int, playerId: Int) -> Unit,
    private val showOwnership: () -> Unit
) {

    fun handle(message: GameMessage) {
        when (message.type) {
            GameMessageType.CURRENT_PLAYER -> handleCurrentPlayer(message.payload.asJsonObject)
            GameMessageType.PLAYER_MOVED -> handlePlayerMoved(message.payload.asJsonObject)
            GameMessageType.CAN_BUY_PROPERTY -> handleCanBuyProperty(message.payload.asJsonObject)
            GameMessageType.PROPERTY_BOUGHT -> handlePropertyBought(message.payload.asJsonObject)
            GameMessageType.MUST_PAY_RENT -> handleMustPayRent(message.payload.asJsonObject)
            GameMessageType.EVENT_CARD_DRAWN,
            GameMessageType.RISK_CARD_DRAWN -> handleCardDrawn(message.payload.asJsonObject)
            GameMessageType.SKIPPED -> handleSkipped(message.payload.asJsonObject)
            GameMessageType.ERROR -> handleError(message.payload.asString)
            else -> Log.w(TAG, "Unbekannter Typ: ${message.type}")
        }
    }

    private fun handleCurrentPlayer(payload: JsonObject) {
        val currentPlayerId = payload.get("playerId").asInt
        if (currentPlayerId == LobbyClient.playerId) {
            enableDiceButton()
        } else {
            disableDiceButton()
        }
    }

    private fun handlePlayerMoved(payload: JsonObject) {
        val playerId = payload.get("playerId").asInt
        val pos = payload.get("pos").asInt
        val dice = payload.get("dice").asInt
        val tileName = payload.get("tileName").asString
        val tileType = payload.get("tileType").asString

        Log.i(TAG, "$playerId hat $dice gewürfelt auf $tileName ($tileType)")
        showResponse("Spieler $playerId → $tileName ($tileType), Würfel: $dice")
        GameStateClient.updatePosition(playerId.toString(), pos)
    }

    private fun handleCanBuyProperty(payload: JsonObject) {
        val tileName = payload.get("tileName").asString
        val tilePos = payload.get("tilePos").asInt
        val playerId = payload.get("playerId").asInt

        showBuyButton(tileName, tilePos, playerId)
        showResponse("Spieler $playerId darf $tileName kaufen")
    }

    private fun handlePropertyBought(payload: JsonObject) {
        val playerId = payload.get("playerId").asInt
        val tileName = payload.get("tileName").asString

        OwnershipClient.addProperty(playerId.toString(), tileName)
        showOwnership()
        showResponse("Kauf abgeschlossen: $tileName für Spieler $playerId")
    }

    private fun handleMustPayRent(payload: JsonObject) {
        val playerId = payload.get("playerId").asInt
        val ownerId = payload.get("ownerId").asInt
        val tileName = payload.get("tileName").asString

        showResponse("Spieler $playerId zahlt Miete an Spieler $ownerId für $tileName")
    }

    private fun handleCardDrawn(payload: JsonObject) {
        val title = payload.get("title").asString
        val description = payload.get("description").asString
        showResponse("Karte gezogen: $title\n$description")
    }

    private fun handleSkipped(payload: JsonObject) {
        val playerId = payload.get("playerId").asInt
        val tileName = payload.get("tileName").asString
        showResponse("Spieler $playerId hat Feld übersprungen: $tileName")
    }

    private fun handleError(msg: String) {
        Log.e(TAG, "Fehler vom Server: $msg")
        showResponse("⚠️ Fehler: $msg")
    }

    companion object {
        private const val TAG = "DktClientHandler"
    }
}
