package at.aau.serg.websocketbrokerdemo.game

import org.json.JSONArray

object OwnershipClient {
    val streetOwners: MutableMap<Int, Int?> = mutableMapOf()
    val houseCounts: MutableMap<Int, Int> = mutableMapOf()
    val hotelBuilt: MutableSet<Int> = mutableSetOf()

    fun updateFromBoard(boardArray: JSONArray) {
        streetOwners.clear()
        houseCounts.clear()
        hotelBuilt.clear()

        for (i in 0 until boardArray.length()) {
            val tile = boardArray.getJSONObject(i)
            val type = tile.getString("type")
            if (type == "StreetTile") {
                val index = tile.getInt("index")
                val ownerId = tile.optInt("ownerId", -1)
                val houses = tile.optInt("houseCount", 0)
                val hasHotel = tile.optBoolean("hotel", false)

                streetOwners[index] = if (ownerId >= 0) ownerId else null
                houseCounts[index] = houses
                if (hasHotel) {
                    hotelBuilt.add(index)
                }
            }
        }
    }

    fun getOwnerId(tileIndex: Int): Int? = streetOwners[tileIndex]

    fun getHouseCount(tileIndex: Int): Int = houseCounts[tileIndex] ?: 0

    fun hasHotel(tileIndex: Int): Boolean = hotelBuilt.contains(tileIndex)
}
