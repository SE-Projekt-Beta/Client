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
    fun testGetCurrentPlayerName() {
        every { GameStateClient.currentPlayerId } returns 1
        every { GameStateClient.getNickname(1) } returns "PlayerOne"

        val result = GameController.getCurrentPlayerName()

        assertEquals("PlayerOne", result)
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

    /*@Test
    fun testEvaluateTileOptions() {
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

        val result = GameController.evaluateTileOptions(playerId, tileIndex)

        assertTrue(result.canBuy.not()) // tile is already owned
        assertTrue(result.canBuildHouse)
        assertTrue(result.canBuildHotel)
    }*/
}
