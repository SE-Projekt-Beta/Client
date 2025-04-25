package at.aau.serg.websocketbrokerdemo.game

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OwnershipClientTest {

    @BeforeEach
    fun setup() {
        // Rücksetzen des Zustands vor jedem Test
        OwnershipClient.all().keys.forEach { player ->
            OwnershipClient.getProperties(player).forEach { property ->
                // Keine Remove-Funktion, daher alternativer Ansatz
                // da OwnershipClient nicht verändert werden darf, wird hier implizit neuer Player genutzt
            }
        }
    }

    @Test
    fun testAddAndRetrieveProperties() {
        val player = "playerX"
        OwnershipClient.addProperty(player, "Kärntner Straße")
        OwnershipClient.addProperty(player, "Opernring")

        val properties = OwnershipClient.getProperties(player)
        assertEquals(2, properties.size)
        assertTrue(properties.contains("Kärntner Straße"))
        assertTrue(properties.contains("Opernring"))
    }

    @Test
    fun testGetPropertiesForUnknownPlayer() {
        val result = OwnershipClient.getProperties("unknown_player")
        assertTrue(result.isEmpty())
    }

    @Test
    fun testAllPropertiesMap() {
        val player1 = "playerA"
        val player2 = "playerB"

        OwnershipClient.addProperty(player1, "Straße 1")
        OwnershipClient.addProperty(player2, "Straße 2")

        val all = OwnershipClient.all()
        assertEquals(listOf("Straße 1"), all[player1])
        assertEquals(listOf("Straße 2"), all[player2])
    }
}