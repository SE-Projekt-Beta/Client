package at.aau.serg.websocketbrokerdemo.model

object ClientBoardMap {

    val tiles: List<ClientTile> = listOf(
        ClientTile(1, "Start", TileType.START),
        ClientTile(2, "Amtsplatz", TileType.STREET, 220, 80, 160, 320),
        ClientTile(3, "Risiko", TileType.RISK),
        ClientTile(4, "Kraft- Zentrale", TileType.STREET, 400, 100, 300, 500),
        ClientTile(5, "Murplatz", TileType.STREET, 300, 120, 200, 400),
        ClientTile(6, "Annenstraße", TileType.STREET, 250, 96, 150, 300),
        ClientTile(7, "Joanneumring", TileType.STREET, 220, 80, 130, 260),
        ClientTile(8, "Eisenbahn Wien- Graz", TileType.STREET, 180, 56, 100, 200),
        ClientTile(9, "Bank", TileType.BANK),
        ClientTile(10, "Joseph-Haydn-Gasse", TileType.STREET, 100, 24, 50, 100),
        ClientTile(11, "Polizeikontrolle", TileType.GOTO_JAIL),
        ClientTile(12, "Schlossgrund", TileType.STREET, 220, 80, 160, 320),
        ClientTile(13, "Dampf-Schifffahrt", TileType.STREET, 300, 70, 400, 500),
        ClientTile(14, "Seilbahn", TileType.STREET,250, 96,  150, 300),
        ClientTile(15, "Kärntner Straße", TileType.STREET, 380, 200, 220, 440),
        ClientTile(16, "Mariahilfer Straße", TileType.STREET, 350, 160, 220, 440),
        ClientTile(17, "Kobenzlstraße", TileType.STREET, 250, 96, 150, 300),
        ClientTile(18, "Eisenbahn", TileType.STREET, 220, 80, 160, 320),
        ClientTile(19, "Landstraße", TileType.STREET, 300, 120, 200, 400),
        ClientTile(20, "Stifterstraße", TileType.STREET, 180, 56, 100, 200),
        ClientTile(21, "Sondersteuer", TileType.TAX),
        ClientTile(22, "Museumstraße", TileType.STREET, 220, 80, 160, 320),
        ClientTile(23, "Risiko", TileType.RISK),
        ClientTile(24, "Autobuslinie", TileType.STREET, 210, 72, 120, 240),
        ClientTile(25, "Mirabellplatz", TileType.STREET, 250, 96, 150, 300),
        ClientTile(26, "Westbahnstraße", TileType.STREET, 240, 88, 140, 280),
        ClientTile(27, "Universitätsplatz", TileType.STREET, 250, 96, 150, 300),
        ClientTile(28, "Bank", TileType.BANK),
        ClientTile(29, "Burggasse", TileType.STREET, 140, 40, 100, 200),
        ClientTile(30, "Villacherstraße", TileType.STREET, 200, 64, 110, 220),
        ClientTile(31, "Gefängnis", TileType.PRISON),
        ClientTile(32, "Alter Platz", TileType.STREET, 210, 72, 120, 240),
        ClientTile(33, "Vermögensabgabe", TileType.TAX),
        ClientTile(34, "Flughafen Wien- Venedig", TileType.STREET, 300, 120, 200, 400),
        ClientTile(35, "Maria-Theresien-Straße", TileType.STREET, 300, 120, 200, 400),
        ClientTile(36, "Andreas-Hofer-Straße", TileType.STREET, 250, 96, 150, 300),
        ClientTile(37, "Boznerplatz", TileType.STREET, 300, 120, 200, 400),
        ClientTile(38, "Risiko", TileType.RISK),
        ClientTile(39, "Arlbergstraße", TileType.STREET, 120, 32, 50, 100),
        ClientTile(40, "Rathausstraße", TileType.STREET, 180, 56, 100, 200)
    )

    fun getTile(pos: Int): ClientTile? {
        return tiles.firstOrNull { it.index == pos }
    }
}
