package at.aau.serg.websocketbrokerdemo.game

object GameStateClient {
    private val playerPositions = mutableMapOf<String, Int>()
    private val players: MutableMap<String, PlayerState> = mutableMapOf()
    var currentDiceRoll: Int = 0
    var currentPlayerId: String? = null

    fun updatePosition(playerId: String, newPos: Int) {
        playerPositions[playerId] = newPos
    }

    fun getPosition(playerId: String): Int {
        return playerPositions[playerId] ?: 0
    }

    fun getAllPositions(): Map<String, Int> = playerPositions.toMap()

    fun updateMoney(playerId: String, amount: Int) {

    }

    fun setDiceRoll(value: Int) {

    }
}