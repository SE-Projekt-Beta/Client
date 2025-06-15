package at.aau.serg.websocketbrokerdemo.game


import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import android.graphics.Color
import android.view.View
import at.aau.serg.websocketbrokerdemo.GameBoardActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull


class OwnershipOverlayManagerTest {
    private lateinit var gameBoardActivity: GameBoardActivity
    private lateinit var manager: OwnershipOverlayManager

    @BeforeEach
    fun setUp() {
        gameBoardActivity = mockk(relaxed = true)
        manager = OwnershipOverlayManager(gameBoardActivity)
    }

    @Test
    fun `updateOwnershipOverlays sets correct color for owned tile`() {
        val overlayView = mockk<View>(relaxed = true)
        val tileIndex = 1
        val ownerId = 2
        ColorManager.playerColors[ownerId] = Color.RED
        mockkObject(OwnershipClient)
        every { OwnershipClient.getOwnerId(tileIndex) } returns ownerId
        val overlays = mapOf(tileIndex to overlayView)
        manager.updateOwnershipOverlays(overlays)
        verify { gameBoardActivity.runOnUiThread(any()) }
        unmockkObject(OwnershipClient)
    }

    @Test
    fun `updateOwnershipOverlays sets transparent color for unowned tile`() {
        val overlayView = mockk<View>(relaxed = true)
        val tileIndex = 1
        mockkObject(OwnershipClient)
        every { OwnershipClient.getOwnerId(tileIndex) } returns null
        val overlays = mapOf(tileIndex to overlayView)
        manager.updateOwnershipOverlays(overlays)
        verify { gameBoardActivity.runOnUiThread(any()) }
        unmockkObject(OwnershipClient)
    }
}
