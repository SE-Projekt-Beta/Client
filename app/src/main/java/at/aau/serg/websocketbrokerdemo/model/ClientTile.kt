package at.aau.serg.websocketbrokerdemo.model

import android.graphics.PointF

data class ClientTile(
    val index: Int,
    val name: String,
    val type: TileType,
    val price: Int? = null,
    val rent: Int? = null,
    val houseCost: Int? = null,
    val hotelCost: Int? = null,
    val position: PointF?,
)
