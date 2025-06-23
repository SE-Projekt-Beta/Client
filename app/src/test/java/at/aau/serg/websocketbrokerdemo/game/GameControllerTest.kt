package at.aau.serg.websocketbrokerdemo.game

import android.graphics.PointF
import at.aau.serg.websocketbrokerdemo.model.ClientBoardMap
import at.aau.serg.websocketbrokerdemo.model.ClientTile
import at.aau.serg.websocketbrokerdemo.model.TileType
import com.google.gson.JsonObject
import io.mockk.*
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameControllerTest {

    @BeforeEach
    fun setUp() {
        mockkObject(GameStateClient)
        mockkObject(OwnershipClient)
        mockkObject(ClientBoardMap)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testUpdateFromGameState() {
        val jsonPayload = JsonObject().apply {
            addProperty("testKey", "testValue")
            add("board", com.google.gson.JsonArray())
        }

        val jsonObjectSlot = slot<JSONObject>()
        val jsonArraySlot = slot<JSONArray>()

        every { GameStateClient.updateFromServer(capture(jsonObjectSlot)) } just Runs
        every { OwnershipClient.updateFromBoard(capture(jsonArraySlot)) } just Runs

        GameController.updateFromGameState(jsonPayload)

        assertEquals("testValue", jsonObjectSlot.captured.getString("testKey"))
        assertTrue(jsonArraySlot.captured.length() == 0)
    }

    @Test
    fun testGetCurrentPlayerId() {
        every { GameStateClient.currentPlayerId } returns 42

        val result = GameController.getCurrentPlayerId()

        assertEquals(42, result)
    }

    @Test
    fun testGetCurrentPlayerNameWhenNull() {
        every { GameStateClient.getNickname(1) } returns null

        val result = GameController.getCurrentPlayerName()

        assertEquals("Unbekannt", result)
    }

    @Test
    fun testGetCurrentFieldIndex() {
        every { GameStateClient.getPlayerPosition(1) } returns 7

        val result = GameController.getCurrentFieldIndex(1)

        assertEquals(7, result)
    }

    @Test
    fun testGetCash() {
        every { GameStateClient.getCash(1) } returns 1500

        val result = GameController.getCash(1)

        assertEquals(1500, result)
    }

    @Test
    fun testGetTileName() {
        val dummyTile = ClientTile(
            index = 5,
            name = "Teststraße",
            price = 100,
            rent = 10,
            houseCost = 50,
            hotelCost = 200,
            type = TileType.STREET,
            position = PointF(0f, 0f)
        )

        every { ClientBoardMap.getTile(5) } returns dummyTile

        val result = GameController.getTileName(5)

        assertEquals("Teststraße", result)
    }

    @Test
    fun testGetTileNameIfNotFound() {
        every { ClientBoardMap.getTile(5) } returns null

        val result = GameController.getTileName(5)

        assertEquals("Unbekannt", result)
    }

    @Test
    fun testGetOwnedTileNames() {
        val playerId = 1
        val tileIndex = 5
        val dummyTile = ClientTile(
            index = tileIndex,
            name = "Teststraße",
            price = 100,
            rent = 10,
            houseCost = 50,
            hotelCost = 200,
            type = TileType.STREET,
            position = PointF(0f, 0f)
        )

        every { ClientBoardMap.getTile(tileIndex) } returns dummyTile
        every { OwnershipClient.streetOwners } returns mutableMapOf(tileIndex to playerId)

        val result = GameController.getOwnedTileNames(playerId)

        assertEquals(listOf("Teststraße"), result)
    }

    @Test
    fun testBuildPayload() {
        val result = GameController.buildPayload("key1", 123, "key2", "value2", "key3", true)

        assertEquals(123, result.get("key1").asInt)
        assertEquals("value2", result.get("key2").asString)
        assertTrue(result.get("key3").asBoolean)
    }

    @Test
    fun testCanBuyUnownedStreet() {
        val tileIndex = 3
        val playerId = 1
        val dummyTile = ClientTile(
            index = tileIndex,
            name = "Teststraße",
            price = 100,
            rent = 10,
            houseCost = 50,
            hotelCost = 200,
            type = TileType.STREET,
            position = PointF(0f, 0f)
        )

        every { ClientBoardMap.getTile(tileIndex) } returns dummyTile
        every { OwnershipClient.getOwnerId(tileIndex) } returns null

        val result = GameController.canBuy(tileIndex, playerId)

        assertTrue(result)
    }

    @Test
    fun testCanBuyOwnedTile() {
        val tileIndex = 3
        val playerId = 1
        val dummyTile = ClientTile(
            index = tileIndex,
            name = "Gefängnis",
            price = 200,
            rent = 25,
            houseCost = null,
            hotelCost = null,
            type = TileType.PRISON,
            position = PointF(0f, 0f)
        )

        every { ClientBoardMap.getTile(tileIndex) } returns dummyTile
        every { OwnershipClient.getOwnerId(tileIndex) } returns 2

        val result = GameController.canBuy(tileIndex, playerId)

        assertFalse(result)
    }

    @Test
    fun testCanBuildHouse() {
        val tileIndex = 4
        val playerId = 1
        val dummyTile = ClientTile(
            index = tileIndex,
            name = "Teststraße",
            price = 100,
            rent = 10,
            houseCost = 50,
            hotelCost = null,
            type = TileType.STREET,
            position = PointF(0f, 0f)
        )

        every { ClientBoardMap.getTile(tileIndex) } returns dummyTile
        every { OwnershipClient.getOwnerId(tileIndex) } returns playerId

        val result = GameController.canBuildHouse(tileIndex, playerId)

        assertTrue(result)
    }

    @Test
    fun testCanBuildHotel() {
        val tileIndex = 4
        val playerId = 1
        val dummyTile = ClientTile(
            index = tileIndex,
            name = "Teststraße",
            price = 100,
            rent = 10,
            houseCost = 50,
            hotelCost = 200,
            type = TileType.STREET,
            position = PointF(0f, 0f)
        )

        every { ClientBoardMap.getTile(tileIndex) } returns dummyTile
        every { OwnershipClient.getOwnerId(tileIndex) } returns playerId

        val result = GameController.canBuildHotel(tileIndex, playerId)

        assertTrue(result)
    }

    @Test
    fun testGetTilePrice_null() {
        every { ClientBoardMap.getTile(99) } returns null
        val result = GameController.getTilePrice(99)
        assertEquals(null, result)
    }


    @Test
    fun testGetTilePrice_valid() {
        val tile = ClientTile(99, "Straße", TileType.STREET, 10, null, null, 3, PointF(0f, 0f))
        every { ClientBoardMap.getTile(99) } returns tile
        val result = GameController.getTilePrice(99)
        assertEquals(10, result)
    }


    @Test
    fun testGetBuildableHouseTiles_noneBuildable() {
        every { OwnershipClient.streetOwners } returns mutableMapOf(1 to 2, 2 to 2)
        every { ClientBoardMap.getTile(any()) } returns ClientTile(0, "", TileType.STREET,0, 0, null, null, PointF(0f, 0f))
        every { OwnershipClient.getOwnerId(any()) } returns 2

        val result = GameController.getBuildableHouseTiles(1)

        assertTrue(result.isEmpty())
    }

    @Test
    fun testGetHouseOverview() {
        val player1 = PlayerClient(
            id = 1,
            nickname = "Tom",
            cash = 1000,
            position = 0,
            alive = true,
            suspended = false,
            hasEscapeCard = false,
            houseCounts = mutableMapOf(1 to 2)
        )

        val player2 = PlayerClient(
            id = 2,
            nickname = "Anna",
            cash = 1500,
            position = 0,
            alive = true,
            suspended = false,
            hasEscapeCard = false
        )

        every { GameStateClient.players } returns mapOf(
            1 to player1,
            2 to player2
        ) as MutableMap<Int, PlayerClient>

        every { ClientBoardMap.getTile(1) } returns ClientTile(
            index = 1,
            name = "Hauptstraße",
            price = 0,
            rent = null,
            houseCost = null,
            hotelCost = null,
            type = TileType.STREET,
            position = PointF(0f, 0f)
        )

        val overview = GameController.getHouseOverview()

        assertTrue(overview.contains("Tom (Cash: 1000)"))
        assertTrue(overview.contains("Hauptstraße: 2"))
        assertTrue(overview.contains("Anna (Cash: 1500)"))
        assertTrue(overview.contains("Keine Häuser"))
    }

    @Test
    fun testEvaluateTileOptions_CanBuyAndBuildHouseAndHotel() {
        val tileIndex = 10
        val playerId = 1

        val tile = ClientTile(
            index = tileIndex,
            name = "Teststraße",
            price = 200,
            rent = 20,
            houseCost = 50,
            hotelCost = 100,
            type = TileType.STREET,
            position = PointF(0f, 0f)
        )

        every { ClientBoardMap.getTile(tileIndex) } returns tile
        every { OwnershipClient.getOwnerId(tileIndex) } returns playerId

        val result = GameController.evaluateTileOptions(playerId, tileIndex)

        assertFalse(result.canBuy) // tile is already owned
        assertTrue(result.canBuildHouse)
        assertTrue(result.canBuildHotel)
    }

    @Test
    fun testEvaluateTileOptions_CanOnlyBuy() {
        val tileIndex = 11
        val playerId = 1

        val tile = ClientTile(
            index = tileIndex,
            name = "Kaufstraße",
            price = 300,
            rent = 30,
            houseCost = null,
            hotelCost = null,
            type = TileType.STREET,
            position = PointF(0f, 0f)
        )

        every { ClientBoardMap.getTile(tileIndex) } returns tile
        every { OwnershipClient.getOwnerId(tileIndex) } returns null

        val result = GameController.evaluateTileOptions(playerId, tileIndex)

        assertTrue(result.canBuy)
        assertFalse(result.canBuildHouse)
        assertFalse(result.canBuildHotel)
    }

    @Test
    fun testEvaluateTileOptions_InvalidTile() {
        val tileIndex = 12
        val playerId = 1

        every { ClientBoardMap.getTile(tileIndex) } returns null
        every { OwnershipClient.getOwnerId(tileIndex) } returns null

        val result = GameController.evaluateTileOptions(playerId, tileIndex)

        assertFalse(result.canBuy)
        assertFalse(result.canBuildHouse)
        assertFalse(result.canBuildHotel)
    }

    @Test
    fun testEvaluateTileOptions_NotStreetTile() {
        val tileIndex = 13
        val playerId = 1

        val tile = ClientTile(
            index = tileIndex,
            name = "Gefägnis",
            price = 100,
            rent = 20,
            houseCost = null,
            hotelCost = null,
            type = TileType.PRISON,
            position = PointF(0f, 0f)
        )

        every { ClientBoardMap.getTile(tileIndex) } returns tile
        every { OwnershipClient.getOwnerId(tileIndex) } returns null

        val result = GameController.evaluateTileOptions(playerId, tileIndex)

        assertFalse(result.canBuy)
        assertFalse(result.canBuildHouse)
        assertFalse(result.canBuildHotel)
    }



}
