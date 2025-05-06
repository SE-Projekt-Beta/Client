package at.aau.serg.websocketbrokerdemo.game

import at.aau.serg.websocketbrokerdemo.network.dto.PlayerDTO

object GameStateClient {
    private val playerPositions = mutableMapOf<String, Int>()
    private val players: MutableMap<String, Player> = mutableMapOf()
    var currentDiceRoll: Int = 0
    var currentPlayerId: Int? = null
    var currentPhase: GamePhase = GamePhase.WAITING_FOR_TURN

    fun updatePlayer(player: Player) {
        players[player.id.toString()] = player
    }

    fun getPlayerState(playerId: String): Player? {
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
        players[playerId]?.cash = amount
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

    fun getAllPlayers(): List<Player> {
      return players.values.toList()
    }

    fun clear() {
        players.clear()
        currentDiceRoll = 0
        currentPlayerId = null
    }

    private fun initializePLayer(playerId: String, nickname: String): Player {
        return players.getOrPut(playerId) {
            Player(
                id = playerId.toInt(), // falls playerId ein Int als String ist
                nickname = nickname,
                position = 1,
                cash = 0,
                properties = null,
                suspensionRounds = 0,
                inJail = false,
                hasEscapedCard = false,
                cheatFlag = false
            )
        }
    }

}