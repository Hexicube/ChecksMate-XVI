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

        fun getCurrentBoardLocation(board: Board): String {
            // TODO: use a BoardSetups dictionary to get the name rather than a static list
            return when (board) {
                BoardSetups.MINI_BOARD -> "Mini Board"
                BoardSetups.FIDE -> "FIDE Board"
                BoardSetups.WIDE -> "Wide Board"
                else -> ""
            }
        }

        fun examineBoardPreMove(board: Board) {
            // survival
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

        fun examineBoardPostMove(board: Board, startBoard: Board) {
            // win
            if (board.isGameOver()) {
                if (board.isWhiteWin()) {
                    val boardLoc = getCurrentBoardLocation(startBoard)
                    collectLocation("Win: $boardLoc")
                }
            }
            // king move to place
            // TODO: check king position
            // threaten and fork
            val withNull = board.nullMove()
            val moveList = MoveList()
            withNull.getMoves(moveList)
            for (move in moveList) {
                if (move.capture != -1) {
                    val piece = withNull.state[move.capture]!!
                    if (piece.isWhite) continue
                    val type = when(piece.type.type) {
                        PieceClass.PAWN -> "Pawn"
                        PieceClass.MINOR -> "Minor"
                        PieceClass.MAJOR -> "Major"
                        PieceClass.QUEEN -> "Queen"
                        PieceClass.KING -> "King"
                    }
                    collectLocation("Threaten: $type")
                }
            }
            // TODO: look for forks
        }

        fun needsToCollectPiece(piece: Piece): Boolean {
            if (piece.isWhite) return false
            val locStr = "Capture: ${piece.identifier}"
            if (!PIECES.contains(locStr)) return false
            return !collected.contains(locStr)
        }

        fun getLocationState(board: Board, startBoard: Board, location: String): LocationState {
            if (collected.contains(location)) return LocationState.COLLECTED
            val locType = location.substringBefore(':')
            val locName = location.substring(location.indexOf(':') + 2)
            when (locType) {
                "Capture" -> {
                    if (board.state.any { it != null && !it.isWhite && it.identifier == locName })
                        return LocationState.AVAILABLE
                    return LocationState.UNREACHABLE
                }
                "Threaten" -> {
                    val count = when (locName) {
                        "Minor" -> board.numPieceTypes[3]
                        "Major" -> board.numPieceTypes[5]
                        "Queen" -> board.numPieceTypes[7]
                        "King" -> board.numPieceTypes[9]
                        else -> throw NotImplementedError()
                    }
                    if (count == 0) return LocationState.UNREACHABLE
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
                    val boardLoc = getCurrentBoardLocation(startBoard)
                    if (boardLoc != locName) return LocationState.UNREACHABLE
                    // TODO: determine if hard
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

        // TODO: function to load collected locations from AP
    }
}