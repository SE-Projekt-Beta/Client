package at.aau.serg.websocketbrokerdemo.model

import android.graphics.PointF

object ClientBoardMap {

    private const val RISIKO = "Risiko"
    private const val BANK = "Bank"

    val tiles: List<ClientTile> = listOf(
        ClientTile(1, "Start", TileType.START, position = PointF(1844f, 1844f)), //PASST
        ClientTile(2, "Amtsplatz", TileType.STREET, 220, 80, 160, 320, position = PointF(1650f, 1844f)), //PASST JETZT - ganz bissl rechts geschoben
        ClientTile(3, "Risiko", TileType.RISK, position = PointF(1500f, 1844f)), //von Arlbergstraße ausgerechnet
        ClientTile(4, "Kraft- Zentrale", TileType.STREET, 400, 100, 300, 500, position = PointF(1300f, 1844f)), //PASST JETZT - muss weiter rechts
        ClientTile(5, "Murplatz", TileType.STREET, 300, 120, 200, 400, position = PointF(1150f, 1844f)), //wie Uni-Platz gemacht, nach rechts
        ClientTile(6, "Annenstraße", TileType.STREET, 250, 96, 150, 300, position = PointF(1000f, 1844f)), //PASST JETZT - wie Westbahnstraße nach rechts
        ClientTile(7, "Joanneumring", TileType.STREET, 220, 80, 130, 260, position = PointF(800f, 1844f)), //PASST JETZT - muss nach rechts
        ClientTile(8, "Eisenbahn Wien- Graz", TileType.STREET, 180, 56, 100, 200, position = PointF(650f, 1844f)), //PASST JETZT - Eisenbahn Wien Graz übernommen;
        ClientTile(9, BANK, TileType.BANK, position = PointF(500f, 1844f)),
        ClientTile(10, "Joseph-Haydn-Gasse", TileType.STREET, 100, 24, 50, 100, position = PointF(300f, 1844f)), //PASST JETZT - nach rechts geschoben
        ClientTile(11, "Polizeikontrolle", TileType.GOTO_JAIL, position = PointF(50f, 1844f)), //Koordinaten von Joseph-Hayden-Gasse übernommen

        ClientTile(12, "Schlossgrund", TileType.STREET, 220, 80, 160, 320, position = PointF(8f, 1650f)), //PASST JETZT
        ClientTile(13, "Dampf-Schifffahrt", TileType.STREET, 300, 70, 400, 500, position = PointF(8f, 1500f)), //PASST JETZT - muss bissl nach unten
        ClientTile(14, "Seilbahn", TileType.STREET, 250, 96, 150, 300, position = PointF(8f, 1300f)), //PASST JETZT - muss nach unten
        ClientTile(15, "Kärntner Straße", TileType.STREET, 380, 200, 220, 440, position = PointF(8f, 1150f)), //PASST JETZT - wie Bozner Platz
        ClientTile(16, "Mariahilfer Straße", TileType.STREET, 350, 160, 220, 440, position = PointF(8f, 1000f)), //PASST JETZT - Koordinaten von Kärntner Straße braucht es hier
        ClientTile(17, "Kobenzlstraße", TileType.STREET, 250, 96, 150, 300, position = PointF(8f, 800f)), //PASST JETZT
        ClientTile(18, "Eisenbahn", TileType.STREET, 220, 80, 160, 320, position = PointF(8f, 600f)), //PASST JETZT
        ClientTile(19, "Landstraße", TileType.STREET, 300, 120, 200, 400, position = PointF(8f, 500f)), //PASST JETZT - muss nach unten
        ClientTile(20, "Stifterstraße", TileType.STREET, 180, 56, 100, 200, position = PointF(8f, 300f)), //PASST JETZT - wie Alter Platz, muss nach unten
        ClientTile(21, "Sondersteuer", TileType.TAX, position = PointF(8f, 8f)),

        ClientTile(22, "Museumstraße", TileType.STREET, 220, 80, 160, 320, position = PointF(300f, 8f)), //PASST JETZT - gegenüber von Joseph-Hayden-Gasse
        ClientTile(23, RISIKO, TileType.RISK, position = PointF(500f, 8f)),
        ClientTile(24, "Autobuslinie", TileType.STREET, 210, 72, 120, 240, position = PointF(650f, 8f)), //PASST JETZT - bissl weiter nach unten und rechts geschoben
        ClientTile(25, "Mirabellplatz", TileType.STREET, 250, 96, 150, 300, position = PointF(800f, 8f)), //PASST JETZT - weiter rechts rutschen
        ClientTile(26, "Westbahnstraße", TileType.STREET, 240, 88, 140, 280, position = PointF(1000f, 8f)), //PASST JETZT - muss nach rechts
        ClientTile(27, "Universitätsplatz", TileType.STREET, 250, 96, 150, 300, position = PointF(1150f, 8f)), //von MT-Straße ausgerechnet - etwas nach links
        ClientTile(28, BANK, TileType.BANK, position = PointF(1300f, 8f)), //wie Kraftzentrale gegenüber gemacht
        ClientTile(29, "Burggasse", TileType.STREET, 140, 40, 100, 200, position = PointF(1500f, 8f)), //PASST JETZT - bissl weiter rechts geschoben
        ClientTile(30, "Villacherstraße", TileType.STREET, 200, 64, 110, 220, position = PointF(1650f, 8f)), //PASST JETZT - bissl weiter rechts geschoben
        ClientTile(31, "Gefängnis", TileType.PRISON, position = PointF(1844f, 8f)), //PASST JETZT

        ClientTile(32, "Alter Platz", TileType.STREET, 210, 72, 120, 240, position = PointF(1844f, 300f)), //PASST JETZT - weiter nach unten geschoben
        ClientTile(33, "Vermögensabgabe", TileType.TAX, position = PointF(1844f, 450f)), //PASST JETZT
        ClientTile(34, "Flughafen Wien- Venedig", TileType.STREET, 300, 120, 200, 400, position = PointF(1844f, 600f)), //wie Eisenbahn 18, nach unten
        ClientTile(35, "Maria-Theresien-Straße", TileType.STREET, 300, 120, 200, 400, position = PointF(1844f, 800f)), //PASST JETZT
        ClientTile(36, "Andreas-Hofer-Straße", TileType.STREET, 250, 96, 150, 300, position = PointF(1844f, 1000f)), //PASST JETZT - nach unten - wie Mariahilfer-Straße gegenüber
        ClientTile(37, "Boznerplatz", TileType.STREET, 300, 120, 200, 400, position = PointF(1844f, 1150f)), //PASST JETZT - nach unten
        ClientTile(38, RISIKO, TileType.RISK, position = PointF(1844f, 1300f)), //muss nach unten -> wie Seilbahn gegenüber
        ClientTile(39, "Arlbergstraße", TileType.STREET, 120, 32, 50, 100, position = PointF(1844f, 1500f)), //PASST JETZT - bissl nach unten geschoben
        ClientTile(40, "Rathausstraße", TileType.STREET, 180, 56, 100, 200, position = PointF(1844f, 1650f)) //PASST JETZT - Koordinaten von Villacher Straße usw.
    )

    fun getTile(pos: Int): ClientTile? {
        return tiles.firstOrNull { it.index == pos }
    }
}
