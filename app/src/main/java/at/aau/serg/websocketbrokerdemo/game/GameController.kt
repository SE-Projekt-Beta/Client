package at.aau.serg.websocketbrokerdemo.game

import at.aau.serg.websocketbrokerdemo.model.ClientBoardMap
import at.aau.serg.websocketbrokerdemo.model.TileType
import at.aau.serg.websocketbrokerdemo.network.dto.GameMessageType
import com.google.gson.JsonObject
import org.json.JSONObject

object GameController {

    fun updateFromGameState(payload: JsonObject) {
        // GameState aktualisieren
        val gameState = JSONObject(payload.toString())
        GameStateClient.updateFromServer(gameState)

        // Besitzinformationen aktualisieren
        val boardArray = gameState.getJSONArray("board")
        OwnershipClient.updateFromBoard(boardArray)
    }

    fun getCurrentPlayerId(): Int = GameStateClient.currentPlayerId

    fun getCurrentPlayerName(): String =
        GameStateClient.getNickname(GameStateClient.currentPlayerId) ?: "Unbekannt"

    fun getCurrentFieldIndex(playerId: Int): Int = GameStateClient.getPlayerPosition(playerId)

    fun getCash(playerId: Int): Int = GameStateClient.getCash(playerId)

    fun getTileName(tileIndex: Int): String =
        ClientBoardMap.getTile(tileIndex)?.name ?: "Unbekannt"

    fun getOwnedTileNames(playerId: Int): List<String> {
        return OwnershipClient.streetOwners
            .filterValues { it == playerId }
            .keys
            .mapNotNull { ClientBoardMap.getTile(it)?.name }
    }

    fun buildPayload(vararg keyValuePairs: Any): JsonObject {
        val payload = JsonObject()
        for (i in keyValuePairs.indices step 2) {
            val key = keyValuePairs[i] as String
            val value = keyValuePairs[i + 1]
            when (value) {
                is Int -> payload.addProperty(key, value)
                is String -> payload.addProperty(key, value)
                is Boolean -> payload.addProperty(key, value)
            }
        }
        return payload
    }

    fun canBuy(tileIndex: Int, playerId: Int): Boolean {
        val tile = ClientBoardMap.getTile(tileIndex) ?: return false
        val owner = OwnershipClient.getOwnerId(tileIndex)
        return tile.type == TileType.STREET && owner == null
    }

    fun canBuildHouse(tileIndex: Int, playerId: Int): Boolean {
        val tile = ClientBoardMap.getTile(tileIndex) ?: return false
        val owner = OwnershipClient.getOwnerId(tileIndex)
        return tile.type == TileType.STREET && owner == playerId && tile.houseCost != null
    }

    fun canBuildHotel(tileIndex: Int, playerId: Int): Boolean {
        val tile = ClientBoardMap.getTile(tileIndex) ?: return false
        val owner = OwnershipClient.getOwnerId(tileIndex)
        return tile.type == TileType.STREET && owner == playerId && tile.hotelCost != null
    }

    fun evaluateTileOptions(playerId: Int, tileIndex: Int): TileOptions {
        return TileOptions(
            canBuy = canBuy(tileIndex, playerId),
            canBuildHouse = canBuildHouse(tileIndex, playerId),
            canBuildHotel = canBuildHotel(tileIndex, playerId)
        )
    }

    data class TileOptions(val canBuy: Boolean, val canBuildHouse: Boolean, val canBuildHotel: Boolean)
}
