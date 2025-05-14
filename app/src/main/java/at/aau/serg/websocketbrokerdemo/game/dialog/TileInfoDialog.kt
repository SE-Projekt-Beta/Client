package at.aau.serg.websocketbrokerdemo.game.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import at.aau.serg.websocketbrokerdemo.R
import at.aau.serg.websocketbrokerdemo.model.ClientTile

class TileInfoDialog(
    context: Context,
    private val tile: ClientTile
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_tile_info)

        findViewById<TextView>(R.id.tileNameTextView).text = tile.name
        findViewById<TextView>(R.id.tilePriceTextView).text = context.getString(R.string.tile_price, tile.price)
        findViewById<TextView>(R.id.tileRentTextView).text = context.getString(R.string.tile_rent, tile.rent)
        findViewById<TextView>(R.id.tileHouseCostTextView).text = context.getString(R.string.tile_house_cost, tile.houseCost)
        findViewById<TextView>(R.id.tileHotelCostTextView).text = context.getString(R.string.tile_hotel_cost, tile.hotelCost)

        findViewById<Button>(R.id.closeTileInfoButton).setOnClickListener {
            dismiss()
        }
    }
}