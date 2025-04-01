package at.aau.serg.websocketbrokerdemo.model

data class Tile (
    val position: Int,        // Position am Spielfeld (0–39)
    val name: String,         // Name des Feldes
    val type: TileType,       // Typ des Feldes (aus TileType)
    val price: Int? = null,   // Kaufpreis, falls kaufbar
    val rent: Int? = null,    // Miete, falls kaufbar
    var owner: String? = null // Wer das Feld besitzt (Player-ID)
)