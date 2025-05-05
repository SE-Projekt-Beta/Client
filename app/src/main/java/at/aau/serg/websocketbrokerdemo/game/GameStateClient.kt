package at.aau.serg.websocketbrokerdemo.game

data class PlayerState(
    val id: String,
    var name: String,
    var position: Int,
    var money: Int,
    var properties: List<Int>,  // IDs der gekauften Tiles
    var inJail: Boolean
)

object GameStateClient {
    private val playerPositions = mutableMapOf<String, Int>()
    private val players: MutableMap<String, PlayerState> = mutableMapOf()
    var currentDiceRoll: Int = 0
    var currentPlayerId: String? = null

    fun updatePlayer(player: PlayerState) {
        players[player.id] = player
    }

    fun getPlayerState(playerId: String): PlayerState? {
        return players[playerId]
    }

    fun updatePosition(playerId: String, newPos: Int) {
        players[playerId]?.position = newPos
    }

    fun getPosition(playerId: String): Int {
        return players[playerId]?.position ?: 0
    }

    fun getAllPositions(): Map<String, Int> {
        return players.mapValues { it.value.position }
    }

    fun updateMoney(playerId: String, amount: Int) {
        players[playerId]?.money = amount
    }

    fun setDiceRoll(value: Int) {
        currentDiceRoll = value
    }

    fun setInJail(playerId: String, inJail: Boolean) {
        players[playerId]?.inJail = inJail
    }

    fun updateProperties(playerId: String, newProps: List<Int>) {
        players[playerId]?.properties = newProps
    }
}