package at.aau.serg.websocketbrokerdemo.game

object OwnershipClient {
    private val ownership = mutableMapOf<String, MutableList<String>>()

    fun addProperty(playerId: String, tileName: String) {
        val properties = ownership.getOrPut(playerId) { mutableListOf() }
        if (!properties.contains(tileName)) {
            properties.add(tileName)
        }
    }

    fun getProperties(playerId: String): List<String> {
        return ownership[playerId] ?: emptyList()
    }

    fun all(): Map<String, List<String>> {
        return ownership.toMap()
    }

    fun reset() {
        ownership.clear()
    }
}
