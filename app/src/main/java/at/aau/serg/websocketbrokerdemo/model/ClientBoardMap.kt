package at.aau.serg.websocketbrokerdemo.model

object ClientBoardMap {

    val tiles: List<ClientTile> = listOf(
        ClientTile(0, "Start", TileType.START),
        ClientTile(1, "Amtsplatz", TileType.STREET, 220, 80, 160, 320),
        ClientTile(2, "Risiko", TileType.RISK),
        ClientTile(3, "Kraft- Zentrale", TileType.STREET, 400, 100, 300, 500),
        ClientTile(4, "Murplatz", TileType.STREET, 300, 120, 200, 400),
        ClientTile(5, "Annenstraße", TileType.STREET, 250, 96, 150, 300),
        ClientTile(6, "Joanneumring", TileType.STREET, 220, 80, 130, 260),
        ClientTile(7, "Eisenbahn Wien- Graz", TileType.STREET, 180, 56, 100, 200),
        ClientTile(8, "Bank", TileType.BANK),
        ClientTile(9, "Joseph-Haydn-Gasse", TileType.STREET, 100, 24, 50, 100),
        ClientTile(10, "Polizeikontrolle", TileType.GOTO_JAIL),
        ClientTile(11, "Schlossgrund", TileType.STREET, 220, 80, 160, 320),
        ClientTile(12, "Dampf-Schifffahrt", TileType.STREET, 300, 70, 400, 500),
        ClientTile(13, "Seilbahn", TileType.STREET, 250, 96, 150, 300),
        ClientTile(14, "Kärntner Straße", TileType.STREET, 380, 200, 220, 440),
        ClientTile(15, "Mariahilfer Straße", TileType.STREET, 350, 160, 220, 440),
        ClientTile(16, "Kobenzlstraße", TileType.STREET, 250, 96, 150, 300),
        ClientTile(17, "Eisenbahn", TileType.STREET, 220, 80, 160, 320),
        ClientTile(18, "Landstraße", TileType.STREET, 300, 120, 200, 400),
        ClientTile(19, "Stifterstraße", TileType.STREET, 180, 56, 100, 200),
        ClientTile(20, "Sondersteuer", TileType.TAX),
        ClientTile(21, "Museumstraße", TileType.STREET, 220, 80, 160, 320),
        ClientTile(22, "Risiko", TileType.RISK),
        ClientTile(23, "Autobuslinie", TileType.STREET, 210, 72, 120, 240),
        ClientTile(24, "Mirabellplatz", TileType.STREET, 250, 96, 150, 300),
        ClientTile(25, "Westbahnstraße", TileType.STREET, 240, 88, 140, 280),
        ClientTile(26, "Universitätsplatz", TileType.STREET, 250, 96, 150, 300),
        ClientTile(27, "Bank", TileType.BANK),
        ClientTile(28, "Burggasse", TileType.STREET, 140, 40, 100, 200),
        ClientTile(29, "Villacherstraße", TileType.STREET, 200, 64, 110, 220),
        ClientTile(30, "Gefängnis", TileType.PRISON),
        ClientTile(31, "Alter Platz", TileType.STREET, 210, 72, 120, 240),
        ClientTile(32, "Vermögensabgabe", TileType.TAX),
        ClientTile(33, "Flughafen Wien- Venedig", TileType.STREET, 300, 120, 200, 400),
        ClientTile(34, "Maria-Theresien-Straße", TileType.STREET, 300, 120, 200, 400),
        ClientTile(35, "Andreas-Hofer-Straße", TileType.STREET, 250, 96, 150, 300),
        ClientTile(36, "Boznerplatz", TileType.STREET, 300, 120, 200, 400),
        ClientTile(37, "Risiko", TileType.RISK),
        ClientTile(38, "Arlbergstraße", TileType.STREET, 120, 32, 50, 100),
        ClientTile(39, "Rathausstraße", TileType.STREET, 180, 56, 100, 200)
    )

    fun getTile(pos: Int): ClientTile? {
        return tiles.getOrNull(pos)
    }
}
