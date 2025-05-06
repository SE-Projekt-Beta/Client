object GameStateClient {
    private val playerPositions = mutableMapOf<String, Int>()

    fun clear() = playerPositions.clear()

    fun updatePosition(playerId: String, newPos: Int) {
        playerPositions[playerId] = newPos
    }

    fun getPosition(playerId: String): Int {
        return playerPositions[playerId] ?: 0
    }

    fun getAllPositions(): Map<String, Int> = playerPositions.toMap()
}
