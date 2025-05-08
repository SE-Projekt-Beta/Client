package at.aau.serg.websocketbrokerdemo.game

object GameStateClient {
    private val playerPositions = mutableMapOf<String, Int>()
    private var currentPlayerId: Int = -1

    fun updatePosition(playerId: String, newPos: Int) {
        playerPositions[playerId] = newPos
    }

    fun getPosition(playerId: String): Int {
        return playerPositions[playerId] ?: 0
    }

    fun getAllPositions(): Map<String, Int> = playerPositions.toMap()

    // 🔁 Für Rundensystem
    fun setCurrentPlayerId(id: Int) {
        currentPlayerId = id
    }

    fun getCurrentPlayerId(): Int = currentPlayerId
}
