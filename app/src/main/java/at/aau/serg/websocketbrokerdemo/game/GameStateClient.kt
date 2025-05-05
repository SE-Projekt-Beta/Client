package at.aau.serg.websocketbrokerdemo.game

import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO

object GameStateClient {
    private val playerPositions = mutableMapOf<String, Int>()
    private val players: MutableMap<String, PlayerDTO> = mutableMapOf()
    var currentDiceRoll: Int = 0
    var currentPlayerId: String? = null

    fun updatePlayer(player: PlayerDTO) {
        players[player.id] = player
    }

    fun getPlayerState(playerId: String): PlayerDTO? {
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