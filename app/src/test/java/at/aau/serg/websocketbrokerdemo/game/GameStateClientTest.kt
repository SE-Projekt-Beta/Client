package at.aau.serg.websocketbrokerdemo.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue


class GameStateClientTest {

    @BeforeEach
    fun setup() {
        // Rücksetzen des Zustands vor jedem Test
        GameStateClient.getAllPositions().keys.forEach { player ->
            GameStateClient.updatePosition(player, 0)
        }
    }

    private fun createPlayer(
        id: String,
        nickname: String,
        position: Int = 0,
        cash: Int = 1000,
        properties: List<Int> = emptyList(),
        inJail: Boolean = false,
        suspensionRounds: Int = 0,
        hasEscapeCard: Boolean = false,
        cheatFlag: Boolean = false
    ) = Player(
        id = id,
        nickname = nickname,
        position = position,
        cash = cash,
        properties = properties.toMutableList(),
        inJail = inJail,
        suspensionRounds = suspensionRounds,
        hasEscapedCard = hasEscapeCard,
        cheatFlag = cheatFlag
    )

    @Test
    fun testUpdateAndGetPosition() {
        val player = createPlayer("1", "player1")
        GameStateClient.updatePlayer(player)

        GameStateClient.updatePosition("1", 7)
        assertEquals(7, GameStateClient.getPosition("1"))
    }

    @Test
    fun testGetAllPositions() {
        GameStateClient.updatePlayer(createPlayer("1", "player1"))
        GameStateClient.updatePlayer(createPlayer("1", "player2"))

        GameStateClient.updatePosition("1", 3)
        GameStateClient.updatePosition("2", 5)


        val all = GameStateClient.getAllPositions()
        assertEquals(3, all["1"])
        assertEquals(5, all["2"])
    }

    @Test
    fun testUpdateMoney() {
        val player = createPlayer("1", "player1")
        GameStateClient.updatePlayer(player)

        GameStateClient.updateMoney("1", 1500)
        assertEquals(1500, GameStateClient.getPlayer("1")?.cash)
    }

    @Test
    fun testAddAndDeductMoney() {
        GameStateClient.updatePlayer(createPlayer("1","player1", cash = 1000))

        GameStateClient.addMoney("1", 500)
        assertEquals(1500, GameStateClient.getPlayer("1")?.cash)

        GameStateClient.deductMoney("1", 300)
        assertEquals(1200, GameStateClient.getPlayer("1")?.cash)
    }

    @Test
    fun testAddAndRemoveProperty() {
        GameStateClient.updatePlayer(createPlayer("1", "player1"))

        GameStateClient.addProperty("1", 8)
        assertTrue(GameStateClient.getProperties("1").contains(8))

        GameStateClient.removeProperty("1", 8)
        assertFalse(GameStateClient.getProperties("1").contains(8))
    }

    @Test
    fun testClearAndUpdateProperties() {
        GameStateClient.updatePlayer(createPlayer("1", "player1", properties = listOf(1, 2)))

        GameStateClient.clearProperties("1")
        assertTrue(GameStateClient.getProperties("1").isEmpty())

        GameStateClient.updateProperties("1", listOf(3, 4))
        assertEquals(listOf(3, 4), GameStateClient.getProperties("1"))
    }

    @Test
    fun testJailStatus() {
        GameStateClient.updatePlayer(createPlayer("1", "player1"))

        GameStateClient.setInJail("1")
        assertTrue(GameStateClient.getPlayer("1")?.inJail == true)

        GameStateClient.setSuspensionRounds("1", 2)
        assertEquals(2, GameStateClient.getPlayer("1")?.suspensionRounds)

        GameStateClient.useEscapeCard("1")
        assertFalse(GameStateClient.getPlayer("1")?.hasEscapedCard == true)
        assertEquals(0, GameStateClient.getPlayer("1")?.suspensionRounds)
    }

    @Test
    fun testLeaveJail() {
        GameStateClient.updatePlayer(createPlayer("1","player1",  inJail = true, hasEscapeCard = true))
        GameStateClient.leaveJail("1")
        assertFalse(GameStateClient.getPlayer("1")?.inJail == true)

        GameStateClient.updatePlayer(createPlayer("2","player2", inJail = true, suspensionRounds = 0))
        GameStateClient.leaveJail("2")
        assertFalse(GameStateClient.getPlayer("2")?.inJail == true)

        GameStateClient.updatePlayer(createPlayer("3","player3", inJail = true, suspensionRounds = 2))
        GameStateClient.leaveJail("3")
        assertTrue(GameStateClient.getPlayer("3")?.inJail == true)
    }

    @Test
    fun testCheatFlag() {
        GameStateClient.updatePlayer(createPlayer("1", "player1"))
        GameStateClient.setCheatFlag("1", true)
        assertTrue(GameStateClient.getPlayer("1")?.cheatFlag == true)
    }

    @Test
    fun testClearState() {
        GameStateClient.updatePlayer(createPlayer("1","player1" , position = 5))
        GameStateClient.setDiceRoll(6)
        GameStateClient.clear()

        assertEquals(0, GameStateClient.getPosition("1"))
        assertEquals(0, GameStateClient.currentDiceRoll)
        assertEquals(null, GameStateClient.currentPlayerId)
    }
}