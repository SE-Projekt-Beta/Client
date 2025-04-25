package at.aau.serg.websocketbrokerdemo.game

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.Test

class OwnershipClientTest {

    @Test
    fun testAddAndRetrieveProperties() {
        OwnershipClient.addProperty("player1", "Kärntner Straße")
        OwnershipClient.addProperty("player1", "Opernring")

        val properties = OwnershipClient.getProperties("player1")
        assertEquals(2, properties.size)
        assertTrue(properties.contains("Kärntner Straße"))
        assertTrue(properties.contains("Opernring"))
    }

    @Test
    fun testGetPropertiesForUnknownPlayer() {
        val result = OwnershipClient.getProperties("unbekannt")
        assertTrue(result.isEmpty())
    }

    @Test
    fun testAllPropertiesMap() {
        OwnershipClient.addProperty("A", "Straße 1")
        OwnershipClient.addProperty("B", "Straße 2")

        val all = OwnershipClient.all()
        assertEquals(1, all["A"]?.size)
        assertEquals(1, all["B"]?.size)
    }
}
