package at.aau.serg.websocketbrokerdemo.game

object GameStateClient {
    private val players: MutableMap<String, Player> = mutableMapOf()
    var currentDiceRoll: Int = 0
    var currentPlayerId: Int? = null

    // auf Player zugreifen
    fun updatePlayer(player: Player) {
        players[player.id.toString()] = player
    }

    fun getPlayer(playerId: String): Player? {
        return players[playerId]
    }

    fun getAllPlayers(): Collection<Player> = players.values

    // Position ändern
    fun updatePosition(playerId: String, newPos: Int) {
        players[playerId]?.position = newPos
    }

    fun getPosition(playerId: String): Int {
        return players[playerId]?.position ?: 0
    }

    /*fun getAllPositions(): Map<String, Int> {
        return players.mapValues { it.value.position }
    }

     */

    // Geld
    fun updateMoney(playerId: String, newAmount: Int) {
        players[playerId]?.cash = newAmount
    }

    fun deductMoney(playerId: String, amount: Int) {
        val player = players[playerId]
        if (player != null) {
            player.cash -= amount
            // ACHTUNG: wenn player bankrott wird, anzeigen in Ui?
        }
    }

    fun addMoney(playerId: String, amount: Int) {
        val player = players[playerId]
        if (player != null) {
            player.cash += amount
        }
    }

    // Würfel
    fun setDiceRoll(value: Int) {
        currentDiceRoll = value
    }

    // Gefägnis
    fun setInJail(playerId: String) {
        players[playerId]?.inJail = true
    }

    fun setSuspensionRounds(playerId: String, rounds: Int) {
        players[playerId]?.suspensionRounds = rounds
    }

    fun useEscapeCard(playerId: String) {
        players[playerId]?.hasEscapedCard = false
        setSuspensionRounds(playerId, 0)
    }

    fun leaveJail(playerId: String) {
        if (players[playerId]?.suspensionRounds == 0 || players[playerId]?.hasEscapedCard == true){
            players[playerId]?.inJail = false
        } else {
            // Kann/darf Gefägnis nicht verlassen
        }
    }

    // Cheat Flag
    fun setCheatFlag(playerId: String, value: Boolean) {
        players[playerId]?.cheatFlag = value
    }

    // Properties
    fun getProperties(playerId: String): List<Int> {
        return players[playerId]?.properties ?: emptyList()
    }

    fun addProperty(playerId: String, tileId: Int) {
        val props = players[playerId]?.properties?.toMutableList() ?: mutableListOf()
        if (!props.contains(tileId)) {
            props.add(tileId)
            players[playerId]?.properties = props
        }
    }

    fun removeProperty(playerId: String, tileId: Int) {
        val props = players[playerId]?.properties?.toMutableList() ?: mutableListOf()
        props.remove(tileId)
        players[playerId]?.properties = props
    }

    fun clearProperties(playerId: String) {
        players[playerId]?.properties = emptyList()
    }

    fun updateProperties(playerId: String, newProps: List<Int>) {
        players[playerId]?.properties = newProps
    }

    fun clear() {
        players.clear()
        currentDiceRoll = 0
        currentPlayerId = null
    }

}