package at.aau.serg.websocketbrokerdemo.model

import android.graphics.PointF

object ClientBoardMap {

    private const val RISIKO = "Risiko"
    private const val BANK = "Bank"

    private val tileSize = 204f
    private val step = (2048 - tileSize)/9f

    val tiles: List<ClientTile> = listOf(
        ClientTile(1, "Start", TileType.START, position = PointF(1844f, 1844f)),
        ClientTile(2, "Amtsplatz", TileType.STREET, 220, 80, 160, 320, position = PointF(1640f, 1844f)),
        ClientTile(3, "Risiko", TileType.RISK, position = PointF(1436f, 1844f)),
        ClientTile(4, "Kraft- Zentrale", TileType.STREET, 400, 100, 300, 500, position = PointF(1232f, 1844f)),
        ClientTile(5, "Murplatz", TileType.STREET, 300, 120, 200, 400, position = PointF(1028f, 1844f)),
        ClientTile(6, "Annenstraße", TileType.STREET, 250, 96, 150, 300, position = PointF(824f, 1844f)),
        ClientTile(7, "Joanneumring", TileType.STREET, 220, 80, 130, 260, position = PointF(620f, 1844f)),
        ClientTile(8, "Eisenbahn Wien- Graz", TileType.STREET, 180, 56, 100, 200, position = PointF(416f, 1844f)),
        ClientTile(9, BANK, TileType.BANK, position = PointF(212f, 1844f)),
        ClientTile(10, "Joseph-Haydn-Gasse", TileType.STREET, 100, 24, 50, 100, position = PointF(8f, 1844f)),

        ClientTile(11, "Polizeikontrolle", TileType.GOTO_JAIL, position = PointF(8f, 1640f)),
        ClientTile(12, "Schlossgrund", TileType.STREET, 220, 80, 160, 320, position = PointF(8f, 1436f)),
        ClientTile(13, "Dampf-Schifffahrt", TileType.STREET, 300, 70, 400, 500, position = PointF(8f, 1232f)),
        ClientTile(14, "Seilbahn", TileType.STREET, 250, 96, 150, 300, position = PointF(8f, 1028f)),
        ClientTile(15, "Kärntner Straße", TileType.STREET, 380, 200, 220, 440, position = PointF(8f, 824f)),
        ClientTile(16, "Mariahilfer Straße", TileType.STREET, 350, 160, 220, 440, position = PointF(8f, 620f)),
        ClientTile(17, "Kobenzlstraße", TileType.STREET, 250, 96, 150, 300, position = PointF(8f, 416f)),
        ClientTile(18, "Eisenbahn", TileType.STREET, 220, 80, 160, 320, position = PointF(8f, 212f)),
        ClientTile(19, "Landstraße", TileType.STREET, 300, 120, 200, 400, position = PointF(8f, 8f)),
        ClientTile(20, "Stifterstraße", TileType.STREET, 180, 56, 100, 200, position = PointF(212f, 8f)),

        ClientTile(21, "Sondersteuer", TileType.TAX, position = PointF(416f, 8f)),
        ClientTile(22, "Museumstraße", TileType.STREET, 220, 80, 160, 320, position = PointF(620f, 8f)),
        ClientTile(23, RISIKO, TileType.RISK, position = PointF(824f, 8f)),
        ClientTile(24, "Autobuslinie", TileType.STREET, 210, 72, 120, 240, position = PointF(1028f, 8f)),
        ClientTile(25, "Mirabellplatz", TileType.STREET, 250, 96, 150, 300, position = PointF(1232f, 8f)),
        ClientTile(26, "Westbahnstraße", TileType.STREET, 240, 88, 140, 280, position = PointF(1436f, 8f)),
        ClientTile(27, "Universitätsplatz", TileType.STREET, 250, 96, 150, 300, position = PointF(1640f, 8f)),
        ClientTile(28, BANK, TileType.BANK, position = PointF(1844f, 8f)),
        ClientTile(29, "Burggasse", TileType.STREET, 140, 40, 100, 200, position = PointF(1844f, 212f)),
        ClientTile(30, "Villacherstraße", TileType.STREET, 200, 64, 110, 220, position = PointF(1844f, 416f)),

        ClientTile(31, "Gefängnis", TileType.PRISON, position = PointF(1844f, 620f)),
        ClientTile(32, "Alter Platz", TileType.STREET, 210, 72, 120, 240, position = PointF(1844f, 824f)),
        ClientTile(33, "Vermögensabgabe", TileType.TAX, position = PointF(1844f, 1028f)),
        ClientTile(34, "Flughafen Wien- Venedig", TileType.STREET, 300, 120, 200, 400, position = PointF(1844f, 1232f)),
        ClientTile(35, "Maria-Theresien-Straße", TileType.STREET, 300, 120, 200, 400, position = PointF(1844f, 1436f)),
        ClientTile(36, "Andreas-Hofer-Straße", TileType.STREET, 250, 96, 150, 300, position = PointF(1844f, 1640f)),
        ClientTile(37, "Boznerplatz", TileType.STREET, 300, 120, 200, 400, position = PointF(1844f, 1844f - step)),
        ClientTile(38, RISIKO, TileType.RISK, position = PointF(1844f - step, 1844f)),
        ClientTile(39, "Arlbergstraße", TileType.STREET, 120, 32, 50, 100, position = PointF(1844f - 2*step, 1844f)),
        ClientTile(40, "Rathausstraße", TileType.STREET, 180, 56, 100, 200, position = PointF(1844f - 3*step, 1844f))
    )

    fun getTile(pos: Int): ClientTile? {
        return tiles.firstOrNull { it.index == pos }
    }
}
