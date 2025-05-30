package at.aau.serg.websocketbrokerdemo.game

import org.json.JSONObject

object GameStateClient {
    var currentPlayerId: Int = -1
    var currentRound: Int = 1
    val players: MutableMap<Int, PlayerClient> = mutableMapOf()

    fun updateFromServer(data: JSONObject) {
        currentPlayerId = data.getInt("currentPlayerId")
        currentRound = data.getInt("currentRound")
        players.clear()

        val playersArray = data.getJSONArray("players")
        for (i in 0 until playersArray.length()) {
            val p = playersArray.getJSONObject(i)
            val houseCounts = mutableMapOf<Int, Int>()
            if (p.has("houseCounts")) {
                val hc = p.getJSONObject("houseCounts")
                for (key in hc.keys()) {
                    houseCounts[key.toInt()] = hc.getInt(key)
                }
            }
            val properties = mutableListOf<Int>()
            if (p.has("properties")) {
                val props = p.getJSONArray("properties")
                for (j in 0 until props.length()) {
                    properties.add(props.getInt(j))
                }
            }
            val player = PlayerClient(
                id = p.getInt("id"),
                nickname = p.getString("nickname"),
                cash = p.getInt("cash"),
                position = p.getInt("position"),
                alive = p.getBoolean("alive"),
                suspended = p.getBoolean("suspended"),
                hasEscapeCard = p.getBoolean("escapeCard"),
                houseCounts = houseCounts,
                properties = properties
            )
            players[player.id] = player
        }
    }

    fun getPlayerPosition(playerId: Int): Int = players[playerId]?.position ?: -1

    fun getCash(playerId: Int): Int = players[playerId]?.cash ?: 0

    fun getNickname(playerId: Int): String? = players[playerId]?.nickname

    fun getPlayer(playerId: Int): PlayerClient? = players[playerId]

    fun getAllNicknames(): List<String> = players.values.map { it.nickname }

    val remainingPlayers: Int
        get() = players.values.count { it.alive }

}
