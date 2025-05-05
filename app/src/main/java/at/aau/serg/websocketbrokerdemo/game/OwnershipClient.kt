package at.aau.serg.websocketbrokerdemo.game

object OwnershipClient {
    private val ownership = mutableMapOf<String, List<Int>>()

    fun setProperties(playerId: String, properties: List<Int>) {
        ownership[playerId] = properties
    }

    fun getProperties(playerId: String): List<Int> {
        return ownership[playerId] ?: emptyList()
    }

    fun clear() {
        ownership.clear()
    }
}
