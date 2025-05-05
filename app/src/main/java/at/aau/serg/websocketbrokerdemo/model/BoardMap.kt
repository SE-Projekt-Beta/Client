package at.aau.serg.websocketbrokerdemo.model

object BoardMap {
    val tiles = listOf(
        Tile(0, "Los", TileType.START),
        Tile(1, "Kärntner Straße", TileType.STREET),
        Tile(2, "Risiko Ereignis", TileType.EVENT_RISIKO),
        Tile(3, "Bahnhof West", TileType.STATION),
        Tile(4, "Einkommenssteuer", TileType.TAX),
        Tile(5, "Opernring", TileType.STREET),
        Tile(6, "Bank Ereignis", TileType.EVENT_BANK),
        Tile(7, "Bahnhof Süd", TileType.STATION),
        Tile(8, "Mariahilfer Straße", TileType.STREET),
        Tile(9, "Strafsteuer", TileType.TAX),
        Tile(10, "Gefängnis", TileType.JAIL),
        Tile(11, "Schönbrunner Straße", TileType.STREET),
        Tile(12, "Risiko Ereignis", TileType.EVENT_RISIKO),
        Tile(13, "Bahnhof Ost", TileType.STATION),
        Tile(14, "Technologieabgabe", TileType.TAX),
        Tile(15, "Landstraßer Hauptstraße", TileType.STREET),
        Tile(16, "Bank Ereignis", TileType.EVENT_BANK),
        Tile(17, "Bahnhof Nord", TileType.STATION),
        Tile(18, "Favoritenstraße", TileType.STREET),
        Tile(19, "Einkommenssteuer", TileType.TAX),
        Tile(20, "Frei Parken", TileType.FREE),
        Tile(21, "Praterstraße", TileType.STREET),
        Tile(22, "Risiko Ereignis", TileType.EVENT_RISIKO),
        Tile(23, "Gürtel", TileType.STREET),
        Tile(24, "Bahnhof Mitte", TileType.STATION),
        Tile(25, "Ringstraße", TileType.STREET),
        Tile(26, "Bank Ereignis", TileType.EVENT_BANK),
        Tile(27, "Donauuferstraße", TileType.STREET),
        Tile(28, "Luxussteuer", TileType.TAX),
        Tile(29, "Untere Donaustraße", TileType.STREET),
        Tile(30, "Gehe ins Gefängnis", TileType.GOTO_JAIL),
        Tile(31, "Lassallestraße", TileType.STREET),
        Tile(32, "Risiko Ereignis", TileType.EVENT_RISIKO),
        Tile(33, "Währinger Straße", TileType.STREET),
        Tile(34, "Bahnhof Flughafen", TileType.STATION),
        Tile(35, "Billrothstraße", TileType.STREET),
        Tile(36, "Bank Ereignis", TileType.EVENT_BANK),
        Tile(37, "Heiligenstädter Straße", TileType.STREET),
        Tile(38, "Strafsteuer", TileType.TAX),
        Tile(39, "Stephansplatz", TileType.STREET)
    )

    fun getTile(id: Int): Tile {
        return tiles[id]
    }
}