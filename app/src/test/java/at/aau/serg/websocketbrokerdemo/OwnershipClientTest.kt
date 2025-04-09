package at.aau.serg.websocketbrokerdemo

import at.aau.serg.websocketbrokerdemo.dkt.OwnershipClient
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.Test

class OwnershipClientTest {
    @Test
    fun testAddAndRetrieveProperties() {
        OwnershipClient.addProperty("player1", "Opernring")
        OwnershipClient.addProperty("player1", "Schönbrunner Straße")

        val properties = OwnershipClient.getProperties("player1")

        assertEquals(2, properties.size)
        assertTrue(properties.contains("Opernring"))
        assertTrue(properties.contains("Schönbrunner Straße"))
    }

    @Test
    fun testEmptyPropertiesForUnknownPlayer() {
        val properties = OwnershipClient.getProperties("unknown")
        assertTrue(properties.isEmpty())
    }
}