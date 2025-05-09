package at.aau.serg.websocketbrokerdemo.game

import org.json.JSONArray

object OwnershipClient {
    val streetOwners: MutableMap<Int, Int?> = mutableMapOf()

    fun updateFromBoard(boardArray: JSONArray) {
        streetOwners.clear()
        for (i in 0 until boardArray.length()) {
            val tile = boardArray.getJSONObject(i)
            val type = tile.getString("type")
            if (type == "StreetTile" && tile.has("ownerId")) {
                val index = tile.getInt("index")
                val ownerId = tile.optInt("ownerId", -1)
                streetOwners[index] = if (ownerId >= 0) ownerId else null
            }
        }
    }

    fun getOwnerId(tileIndex: Int): Int? {
        return streetOwners[tileIndex]
    }
}
