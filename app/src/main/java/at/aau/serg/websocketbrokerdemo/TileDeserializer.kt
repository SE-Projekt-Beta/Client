package at.aau.serg.websocketbrokerdemo

import at.aau.serg.websocketbrokerdemo.model.Tile
import at.aau.serg.websocketbrokerdemo.model.tiles.*;
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import com.google.gson.GsonBuilder



class TileDeserializer : JsonDeserializer<Tile> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Tile {
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("type").asString
        return when (type) {
            "street" -> context.deserialize<Street>(json, Street::class.java)
            "event" -> context.deserialize<Event>(json, Event::class.java)
            "tax" -> context.deserialize<Tax>(json, Tax::class.java)
            "station" -> context.deserialize<Station>(json, Station::class.java)
            "jail" -> context.deserialize<Jail>(json, Jail::class.java)
            "free" -> context.deserialize<Free>(json, Free::class.java)
            "goToJail" -> context.deserialize<GoToJail>(json, GoToJail::class.java)
            "start" -> context.deserialize<Start>(json, Start::class.java)
            else -> throw JsonParseException("Unknown tile type: $type")
        }
    }
}

val gson = GsonBuilder()
    .registerTypeAdapter(Tile::class.java, TileDeserializer())
    .create()


