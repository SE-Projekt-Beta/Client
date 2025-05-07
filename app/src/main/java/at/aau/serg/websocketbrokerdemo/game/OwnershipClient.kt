package at.aau.serg.websocketbrokerdemo.game

object OwnershipClient {
    private val ownership = mutableMapOf<String, MutableList<String>>()

    fun addProperty(playerId: String, tileName: String) {
        val list = ownership.getOrPut(playerId) { mutableListOf() }
        list.add(tileName)
    }

    fun getProperties(playerId: String): List<String> = ownership[playerId] ?: emptyList()
    fun all(): Map<String, List<String>> = ownership.toMap()
}