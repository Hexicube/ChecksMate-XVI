enum class LocationState {
    UNREACHABLE, HARD, AVAILABLE, COLLECTED
}

class LocationHelper {
    companion object {
        val PIECES = listOf(
            "Capture: Queenside Rook", "Capture: Queenside Knight", "Capture: Queenside Bishop",
            "Capture: Queenside Attendant", "Capture: Queen", "Capture: Kingside Attendant",
            "Capture: Kingside Bishop", "Capture: Kingside Knight", "Capture: Kingside Rook",

            "Capture: Queenside Rook Pawn", "Capture: Queenside Knight Pawn", "Capture: Queenside Bishop Pawn",
            "Capture: Queenside Attendant Pawn", "Capture: Queen Pawn", "Capture: King Pawn", "Capture: Kingside Attendant Pawn",
            "Capture: Kingside Bishop Pawn", "Capture: Kingside Knight Pawn", "Capture: Kingside Rook Pawn",

            // TODO: more piece IDs for larger boards
        )

        val THREATS = listOf(
            "Threaten: Minor", "Threaten: Major", "Threaten: Queen", "Threaten: King"
        )

        val STATES = listOf(
            "False Fork: 2", "False Fork: 3", "False Fork: Royal",
            "True Fork: 2", "True Fork: 3", "True Fork: Royal"
        )

        val PLACES = listOf(
            "King: Early Forward", "King: To Centre", "King: To Edge"
        )

        val SURVIVAL = listOf(
            "Survive: 5 Turns", "Survive: 10 Turns", "Survive: 20 Turns"
        )

        val OTHER = listOf(
            "Win: Mini Board", "Win: FIDE Board", "Win: Wide Board"
            // TODO: larger boards
        )

        val ALL_CHECKS = PIECES union THREATS union STATES union PLACES union SURVIVAL union OTHER

        fun examineBoardPreMove(board: Board) {
            // used to check for survival
            if (!board.isGameOver()) {
                val locStr = "Survive: ${board.curPly / 2} Turns"
                if (SURVIVAL.contains(locStr)) collectLocation(locStr)
            }
        }

        fun examineMove(board: Board, move: Move) {
            // used to check for captures
            if (move.capture != -1) {
                val piece = board.state[move.capture]!!
                val locStr = "Capture: ${piece.identifier}"
                if (PIECES.contains(locStr)) collectLocation(locStr)
            }
        }

        fun examineBoardPostMove(board: Board) {
            // used to check for threats, forks, position of pieces, wins
            if (board.isGameOver()) {
                if (board.isWhiteWin()) {
                    // TODO: check what board is in play and award that location
                }
            }
            // TODO: check king position
            // TODO: look for threats and forks
        }

        fun needsToCollectPiece(piece: Piece): Boolean {
            if (piece.isWhite) return false
            val locStr = "Capture: ${piece.identifier}"
            if (!PIECES.contains(locStr)) return false
            return !collected.contains(locStr)
        }

        fun getLocationState(board: Board, location: String): LocationState {
            if (collected.contains(location)) return LocationState.COLLECTED
            val locType = location.substringBefore(':')
            when (locType) {
                "Capture" -> {
                    val piece = location.substring(location.indexOf(':') + 2)
                    if (board.state.any { it != null && !it.isWhite && it.identifier == piece })
                        return LocationState.AVAILABLE
                    return LocationState.UNREACHABLE
                }
                "Threaten" -> {
                    // TODO: determine if available or hard
                    return LocationState.AVAILABLE
                }
                "False Fork" -> {
                    // TODO: determine if available or hard
                    return LocationState.AVAILABLE
                }
                "True Fork" -> {
                    // TODO: determine if available or hard
                    return LocationState.HARD
                }
                "King" -> {
                    // move king to specific places
                    return LocationState.AVAILABLE
                }
                "Survive" -> {
                    // TODO: determine if available or hard
                    return LocationState.AVAILABLE
                }
                "Win" -> {
                    // TODO: check on correct board
                    return LocationState.AVAILABLE
                }
                else -> throw NotImplementedError()
            }
        }

        val collected = ArrayList<String>()
        fun collectLocation(location: String) {
            if (collected.contains(location)) return
            collected.add(location)

            // TODO: AP stuff
            println("[COLLECT] $location")
        }
    }
}