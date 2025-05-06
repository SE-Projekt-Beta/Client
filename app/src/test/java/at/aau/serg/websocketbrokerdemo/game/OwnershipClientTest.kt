package at.aau.serg.websocketbrokerdemo.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class OwnershipClientTest {

    @BeforeEach
    fun setup() {
        // Clear state before each test
        OwnershipClient.clear()
    }

    @Test
    fun testDefaultPropertiesForUnknown() {
        // Unknown players should have no properties
        assertTrue(OwnershipClient.getProperties("unknownPlayer").isEmpty())
    }

    @Test
    fun testAddAndGetProperties() {
        // After adding a property, getProperties should return it
        OwnershipClient.addProperty("playerX", "Tile1")
        assertEquals(listOf("Tile1"), OwnershipClient.getProperties("playerX"))
    }

    @Test
    fun testAllPropertiesMap() {
        // Adding properties for multiple players and retrieving the full map
        OwnershipClient.addProperty("playerA", "Straße 1")
        OwnershipClient.addProperty("playerB", "Straße 2")

        val all = OwnershipClient.all()
        assertEquals(listOf("Straße 1"), all["playerA"])
        assertEquals(listOf("Straße 2"), all["playerB"])
    }
}
