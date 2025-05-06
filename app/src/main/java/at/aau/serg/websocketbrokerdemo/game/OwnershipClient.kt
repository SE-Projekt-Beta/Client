package at.aau.serg.websocketbrokerdemo.game

object OwnershipClient {
    private val ownership = mutableMapOf<String, MutableList<String>?>() // playerId -> List of tileNames

    fun clear() = ownership.clear()

    fun addProperty(playerId: String, tileName: String) {
        val list = ownership.getOrPut(playerId) { mutableListOf() }
        if (!list?.contains(tileName)!!) {
            list.add(tileName)
        }
    }

    fun setProperties(playerId: String, properties: List<String>?) {
        ownership[playerId] = properties?.toMutableList()
    }

    fun getProperties(playerId: String): List<String> {
        return ownership[playerId] ?: emptyList()
    }

    fun all(): Map<String, List<String>?> {
        return ownership.toMap()
    }

}
