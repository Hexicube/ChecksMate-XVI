import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

enum class APPermission(val id: Int) {
    DISABLED(0b000),
    ENABLED(0b001),
    GOAL(0b010),
    AUTO(0b110),
    AUTO_ENABLED(0b111);

    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }
}

data class APNetworkPlayer(val team: Int, val slot: Int, val alias: String, val name: String) {
    companion object {
        fun fromJson(json: JsonObject): APNetworkPlayer {
            return APNetworkPlayer(
                json.get("team").asInt,
                json.get("slot").asInt,
                json.get("alias").asString,
                json.get("name").asString
            )
        }
    }
}

enum class APItemFlags(val id: Int) {
    ADVANCEMENT(0b001),
    VERY_USEFUL(0b010),
    TRAP(0b100);

    companion object {
        fun fromID(id: Int): List<APItemFlags> {
            val flags = ArrayList<APItemFlags>()
            if (id and 0b001 != 0) flags.add(ADVANCEMENT)
            if (id and 0b010 != 0) flags.add(VERY_USEFUL)
            if (id and 0b100 != 0) flags.add(TRAP)
            return flags.toList()
        }
    }
}

data class APNetworkItem(val item: Int, val location: Int, val player: Int, val flags: Int) {
    companion object {
        fun fromJson(json: JsonObject): APNetworkItem {
            return APNetworkItem(
                json.get("item").asInt,
                json.get("location").asInt,
                json.get("player").asInt,
                json.get("flags").asInt
            )
        }
    }

    fun toJson(): JsonObject {
        TODO()
    }
}

data class APNetworkVersion(val major: Int, val minor: Int, val build: Int) {
    companion object {
        fun fromJson(json: JsonObject): APNetworkVersion {
            return APNetworkVersion(json.get("major").asInt, json.get("minor").asInt, json.get("build").asInt)
        }
    }
    fun toJson(classStr: String? = null): JsonElement {
        val obj = JsonObject()
        obj.add("major", JsonPrimitive(major))
        obj.add("minor", JsonPrimitive(minor))
        obj.add("build", JsonPrimitive(build))
        if (classStr != null) obj.add("class", JsonPrimitive(classStr))
        return obj
    }
}