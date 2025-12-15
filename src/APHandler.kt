import com.google.gson.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.util.concurrent.CompletionStage

class APConnectionManager : WebSocket.Listener {
    companion object {
        private val GAME_NAME = "ChecksMate XVI"
        private val AP_VERSION = APNetworkVersion(6, 1, 0)
        private val GEN_VERSION = APNetworkVersion(1, 0, 0)
    }

    private var socket: WebSocket? = null
    private var lastIndex = 0

    /*
    Connection handshake:
    - Client connects
    - Server accepts and sends RoomInfo
    - Client sends GetDataPackage (optional)
        - Server responds with DataPackage
    - Client sends Connect
        - Server validates and responds with Connected or ConnectionRefused (ConnectionRefused allows retries)
        - Server may send ReceivedItems
    - Server sends PrintJSON globally
    */

    /*
    0: Awaiting RoomInfo
    1: Awaiting DataPackage (optional)
    2: Awaiting Connected/ConnectionRefused
    3: Connected
    */
    private var connectionState = 0

    private fun disconnect() {
        try {
            socket?.sendClose(0, "")?.get()
            socket = null
        }
        catch (_: Exception) {}
        connectionState = 0
    }

    fun connect(uri: URI) {
        disconnect()
        HttpClient.newHttpClient().newWebSocketBuilder().buildAsync(uri, this)
    }

    override fun onOpen(webSocket: WebSocket?) {
        println("Open: ${webSocket.toString()}")
        if (socket != null) println("[WARN] Socket was overridden")
        socket = webSocket
    }

    override fun onError(webSocket: WebSocket?, error: Throwable?) {
        if (socket != webSocket) println("[WARN] Socket mismatch on error")
        error?.printStackTrace()
    }

    override fun onClose(webSocket: WebSocket?, statusCode: Int, reason: String?): CompletionStage<*>? {
        if (webSocket == socket) socket = null
        println("Socket closed: [$statusCode] $reason")
        return null
    }

    override fun onText(webSocket: WebSocket?, data: CharSequence?, last: Boolean): CompletionStage<*>? {
        if (socket != webSocket) println("[WARN] Socket mismatch on text in")
        val text = data?.toString() ?: return null
        println("Text in: $text")
        val data = JsonParser.parseString(text).asJsonObject
        when (val id = data.get("cmd").asString) {
            "RoomInfo" -> {
                if (connectionState != 0) {
                    println("[WARN] RoomInfo packet received at wrong time, ignoring")
                    return null
                }
                val APVersion = APNetworkVersion.fromJson(data.get("version").asJsonObject)
                if (APVersion != AP_VERSION) println("[WARN] Server AP version does not match supported version")
                val genVersion = APNetworkVersion.fromJson(data.get("generator_version").asJsonObject)
                if (genVersion != GEN_VERSION) println("[WARN] Server gen version does not match supported version")
                val tags = data.get("tags").asJsonArray.map { it.asString }
                val needsPassword = data.get("password")
                val perms = data.get("permissions").asJsonObject.asMap() // TODO: int map?
                val hintCost = data.get("hint_cost").asInt
                val locationCheckPoints = data.get("location_check_points").asInt
                val gameList = data.get("games").asJsonArray.map { it.asString }
                if (!gameList.contains(GAME_NAME)) {
                    // TODO: show this as an error
                    println("[ERR ] Room does not contains this game")
                    disconnect()
                    return null
                }
                // TODO: datapackage_checksums dict[str,str]
                val seed = data.get("seed_name").asString
                val time = data.get("time").asFloat

                connectionState = 1
                // TODO: GetDataPackage?
                connectionState = 2
                // TODO: get details for Connect
                sendConnect("", "", null)
            }
            "ConnectionRefused" -> {
                if (connectionState != 2) {
                    println("[WARN] ConnectionRefused packet received at wrong time, ignoring")
                    return null
                }
                val errors = data.get("errors").asJsonArray.map { it.asString }
                // TODO: handle this
                // InvalidSlot: "name" not found
                // InvalidGame: name does not match game
                // IncompatibleVersion: version mismatch
                // InvalidPassword: missing or wrong password
                // InvalidItemsHandling: Bad flags
            }
            "Connected" -> {
                if (connectionState != 2) {
                    println("[WARN] Connected packet received at wrong time, ignoring")
                    return null
                }
                val team = data.get("team").asInt
                val slot = data.get("slot").asInt
                val players = data.get("players").asJsonArray.map { APNetworkPlayer.fromJson(it.asJsonObject) }
                val missingLocations = data.get("missing_locations").asJsonArray.map { it.asInt }
                val checkedLocations = data.get("checked_locations").asJsonArray.map { it.asInt }
                //val slotData = ??? (not required?)
                val slotInfo = data.get("slot_info").asJsonArray.map { it.asString } // TODO: NetworkSlot
                val hintPoints = data.get("hint_points").asInt

                // TODO: update state

                connectionState = 3
            }
            "ReceivedItems" -> {
                if (connectionState != 3) {
                    println("[WARN] ReceivedItems packet received at wrong time, ignoring")
                    return null
                }
                val index = data.get("index").asInt
                val items = data.get("items").asJsonArray.map { APNetworkItem.fromJson(it.asJsonObject) }

                // TODO: update state
            }
            "LocationInfo" -> {
                // Response packet for LocationScouts
                if (connectionState != 3) {
                    println("[WARN] LocationInfo packet received at wrong time, ignoring")
                    return null
                }
                val locations = data.get("locations").asJsonArray.map { APNetworkItem.fromJson(it.asJsonObject) }
            }
            "RoomUpdate" -> {
                // TODO: contains same arguments as RoomInfo
                // TODO: if connected, also contains Connected arguments
                // NOTE: players argument only sent on alias change
                // NOTE: checked_locations only contains new locations and missing_locations is inferred
            }
            "PrintJSON" -> {
                // TODO: has a lot of varied info that will be useful for appropriately grouping messages
            }
            "DataPackage" -> {
                // Response packet for GetDataPackage during handshake
                if (connectionState != 1) {
                    println("[WARN] DataPackage packet received at wrong time, ignoring")
                    return null
                }
                TODO()
            }
            "Bounced" -> {
                // Response packet for Bounce, ignored
                // NOTE: This is needed for DeathLink
            }
            "InvalidPacket" -> {
                val problem = data.get("type").asString
                println("[ERR ] Invalid packet was sent: ${data.get("text").asString}")
                if (problem == "arguments") {
                    println("[ERR ] - Original packet: ${data.get("original_cmd").asString}")
                }
                else println("[ERR ] - Original packet could not be parsed")
            }
            "Retrieved" -> {
                // Response packet for Get, ignored
            }
            "SetReply" -> {
                // Response packet for Set/SetNotify, ignored
            }
            else -> println("[WARN] Unknown packet type: $id")
        }
        return null
    }

    fun sendConnect(name: String, uuid: String, password: String?) {
        if (socket == null) {
            println("[WARN] Tried to send Connect before connecting")
            return
        }
        if (connectionState != 0) {
            println("[WARN] Tried to send Connect at wrong time")
            return
        }
        val obj = JsonObject()
        obj.add("cmd", JsonPrimitive("Connect"))
        obj.add("game", JsonPrimitive(GAME_NAME))
        obj.add("name", JsonPrimitive(name))
        obj.add("uuid", JsonPrimitive(uuid))
        if (password != null) obj.add("password", JsonPrimitive(password))
        obj.add("version", AP_VERSION.toJson())
        obj.add("items_handling", JsonPrimitive(0b111))
        val arr = JsonArray()
        arr.add("AP")
        obj.add("tags", arr)
        socket?.sendText(obj.toString(), true)
    }

    fun requestSync() {
        // Used when a mismatch is detected
        if (socket == null) {
            println("[WARN] Tried to send Sync before connecting")
            return
        }
        if (connectionState != 3) {
            println("[WARN] Tried to send Sync at wrong time")
            return
        }
        val obj = JsonObject()
        obj.add("cmd", JsonPrimitive("Sync"))
        socket?.sendText(obj.toString(), true)
    }

    fun sendLocation(location: String) = sendLocations(listOf(location))
    fun sendLocations(locations: List<String>) {
        if (socket == null) {
            println("[WARN] Tried to send LocationChecks before connecting")
            return
        }
        if (connectionState != 3) {
            println("[WARN] Tried to send LocationChecks at wrong time")
            return
        }
        val obj = JsonObject()
        obj.add("cmd", JsonPrimitive("LocationChecks"))
        val arr = JsonArray()
        locations.forEach { arr.add(it) }
        obj.add("locations", arr)
        socket?.sendText(obj.toString(), true)
    }

    fun requestLocationHint(location: String) {
        // TODO: map location to its id
        // TODO: infer the importance of the hint (10 is low priority, 30 is high priority)
    }

    fun updateStatus() {
        // TODO: handle updating status
        // 5: Connected (default state, never set this)
        // 10: Ready (pre-countdown?)
        // 20: Playing
        // 30: Goaled
    }

    fun sendChat(text: String) {
        if (socket == null) {
            println("[WARN] Tried to send Say before connecting")
            return
        }
        if (connectionState != 3) {
            println("[WARN] Tried to send Say at wrong time")
            return
        }
        val obj = JsonObject()
        obj.add("cmd", JsonPrimitive("Say"))
        obj.add("text", JsonPrimitive(text))
        socket?.sendText(obj.toString(), true)
    }
}