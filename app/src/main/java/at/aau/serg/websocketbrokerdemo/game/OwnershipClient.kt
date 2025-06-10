package at.aau.serg.websocketbrokerdemo.game

import androidx.compose.runtime.mutableStateMapOf
import org.json.JSONArray

object OwnershipClient {
    val streetOwners = mutableStateMapOf<Int, Int?>()

    fun updateFromBoard(boardArray: JSONArray) {
        streetOwners.clear()

        // Reset alle Häuser
        GameStateClient.players.values.forEach { it.houseCounts.clear() }

        for (i in 0 until boardArray.length()) {
            val tile = boardArray.getJSONObject(i)
            val index = tile.getInt("index")

            if (tile.getString("type") == "StreetTile" && tile.has("ownerId")) {
                val ownerId = tile.getInt("ownerId")
                if (ownerId >= 0) {
                    streetOwners[index] = ownerId

                    val houseCount = tile.optInt("houseCount", 0)
                    val player = GameStateClient.getPlayer(ownerId)
                    if (houseCount > 0 && player != null) {
                        player.houseCounts[index] = houseCount
                    }
                }
            }
        }
    }


    fun getOwnerId(tileIndex: Int): Int? {
        return streetOwners[tileIndex]
    }
}
