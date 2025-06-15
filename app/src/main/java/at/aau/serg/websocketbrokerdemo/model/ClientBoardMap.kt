package at.aau.serg.websocketbrokerdemo.model

import android.graphics.PointF

object ClientBoardMap {

    private const val RISIKO = "Risiko"
    private const val BANK = "Bank"

    val tiles: List<ClientTile> = listOf(
        ClientTile(1, "Start", TileType.START, position = PointF(1785f, 1790f)), // pAST
        ClientTile(2, "Amtsplatz", TileType.STREET, 220, 80, 160, 320, position = PointF(1650f, 1780f)),
        ClientTile(3, "Risiko", TileType.RISK, position = PointF(1500f, 1780f)), // PASST
        ClientTile(4, "Kraft- Zentrale", TileType.STREET, 400, 100, 300, 500, position = PointF(1300f, 1780f)), // PASST
        ClientTile(5, "Murplatz", TileType.STREET, 300, 120, 200, 400, position = PointF(1150f, 1780f)),
        ClientTile(6, "Annenstraße", TileType.STREET, 250, 96, 150, 300, position = PointF(1000f, 1780f)),
        ClientTile(7, "Joanneumring", TileType.STREET, 220, 80, 130, 260, position = PointF(800f, 1780f)), // PASST
        ClientTile(8, "Eisenbahn Wien- Graz", TileType.STREET, 180, 56, 100, 200, position = PointF(650f, 1780f)), //PASST
        ClientTile(9, BANK, TileType.BANK, position = PointF(480f, 1780f)), // sollte passen
        ClientTile(10, "Joseph-Haydn-Gasse", TileType.STREET, 100, 24, 50, 100, position = PointF(300f, 1780f)), // PASST
        ClientTile(11, "Polizeikontrolle", TileType.GOTO_JAIL, position = PointF(50f, 1780f)),

        ClientTile(12, "Schlossgrund", TileType.STREET, 220, 80, 160, 320, position = PointF(170f, 1650f)), // passt
        ClientTile(13, "Dampf-Schifffahrt", TileType.STREET, 300, 70, 400, 500, position = PointF(170f, 1500f)),
        ClientTile(14, "Seilbahn", TileType.STREET, 250, 96, 150, 300, position = PointF(170f, 1300f)), // pASt
        ClientTile(15, "Kärntner Straße", TileType.STREET, 380, 200, 220, 440, position = PointF(170f, 1150f)), // PASST
        ClientTile(16, "Mariahilfer Straße", TileType.STREET, 350, 160, 220, 440, position = PointF(170f, 975f)), // sollte passen
        ClientTile(17, "Kobenzlstraße", TileType.STREET, 250, 96, 150, 300, position = PointF(170f, 800f)), // PASST
        ClientTile(18, "Eisenbahn", TileType.STREET, 220, 80, 160, 320, position = PointF(170f, 600f)), // passt
        ClientTile(19, "Landstraße", TileType.STREET, 300, 120, 200, 400, position = PointF(170f, 485f)), // Sollte passen
        ClientTile(20, "Stifterstraße", TileType.STREET, 180, 56, 100, 200, position = PointF(170f, 300f)),
        ClientTile(21, "Sondersteuer", TileType.TAX, position = PointF(50f, 70f)),

        ClientTile(22, "Museumstraße", TileType.STREET, 220, 80, 160, 320, position = PointF(300f, 165f)), // PASST
        ClientTile(23, RISIKO, TileType.RISK, position = PointF(430f, 165f)),
        ClientTile(24, "Autobuslinie", TileType.STREET, 210, 72, 120, 240, position = PointF(650f, 165f)),
        ClientTile(25, "Mirabellplatz", TileType.STREET, 250, 96, 150, 300, position = PointF(800f, 165f)), // PASST
        ClientTile(26, "Westbahnstraße", TileType.STREET, 240, 88, 140, 280, position = PointF(1000f, 165f)),
        ClientTile(27, "Universitätsplatz", TileType.STREET, 250, 96, 150, 300, position = PointF(1150f, 165f)),
        ClientTile(28, BANK, TileType.BANK, position = PointF(1300f, 165f)), //PASST
        ClientTile(29, "Burggasse", TileType.STREET, 140, 40, 100, 200, position = PointF(1495f, 165f)),
        ClientTile(30, "Villacherstraße", TileType.STREET, 200, 64, 110, 220, position = PointF(1650f, 165f)), // passt
        ClientTile(31, "Gefängnis", TileType.PRISON, position = PointF(1844f, 70f)),

        ClientTile(32, "Alter Platz", TileType.STREET, 210, 72, 120, 240, position = PointF(1785f, 300f)),
        ClientTile(33, "Vermögensabgabe", TileType.TAX, position = PointF(1785f, 450f)), //PASST
        ClientTile(34, "Flughafen Wien- Venedig", TileType.STREET, 300, 120, 200, 400, position = PointF(1785f, 620f)),
        ClientTile(35, "Maria-Theresien-Straße", TileType.STREET, 300, 120, 200, 400, position = PointF(1785f, 800f)),
        ClientTile(36, "Andreas-Hofer-Straße", TileType.STREET, 250, 96, 150, 300, position = PointF(1785f, 1000f)),
        ClientTile(37, "Boznerplatz", TileType.STREET, 300, 120, 200, 400, position = PointF(1785f, 1150f)),
        ClientTile(38, RISIKO, TileType.RISK, position = PointF(1785f, 1300f)),
        ClientTile(39, "Arlbergstraße", TileType.STREET, 120, 32, 50, 100, position = PointF(1785f, 1500f)),
        ClientTile(40, "Rathausstraße", TileType.STREET, 180, 56, 100, 200, position = PointF(1785f, 1650f))
    )

    fun getTile(pos: Int): ClientTile? {
        return tiles.firstOrNull { it.index == pos }
    }
}
