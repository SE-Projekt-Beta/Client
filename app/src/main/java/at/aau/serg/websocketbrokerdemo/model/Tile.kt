package at.aau.serg.websocketbrokerdemo.model

import java.util.Locale

open class Tile (private var position: Int, private var name: String?) {

    fun getPosition(): Int {
        return position
    }

    fun getName(): String? {
        return name
    }

    fun getTileType(): String {
        return javaClass.simpleName.lowercase(Locale.getDefault())
    }

}