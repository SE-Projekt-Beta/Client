package at.aau.serg.websocketbrokerdemo.model

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import at.aau.serg.websocketbrokerdemo.R

class TileInfoDialog(
    context: Context,
    private val tile: ClientTile
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_tile_info)

        findViewById<TextView>(R.id.tileNameTextView).text = tile.name
        findViewById<TextView>(R.id.tilePriceTextView).text = "Kaufpreis: ${tile.price} €"
        findViewById<TextView>(R.id.tileRentTextView).text = "Miete: ${tile.rent} €"
        findViewById<TextView>(R.id.tileHouseCostTextView).text = "Hauskosten: ${tile.houseCost} €"
        findViewById<TextView>(R.id.tileHotelCostTextView).text = "Hotelkosten: ${tile.hotelCost} €"

        findViewById<Button>(R.id.closeTileInfoButton).setOnClickListener {
            dismiss()
        }
    }
}
