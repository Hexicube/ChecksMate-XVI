enum class LocationState {
    UNREACHABLE, HARD, AVAILABLE, COLLECTED
}

class LocationHelper {
    companion object {
        val PIECES = listOf(
            // specific pieces
            "Capture: Queenside Rook", "Capture: Queenside Knight", "Capture: Queenside Bishop",
            "Capture: Queenside Attendant", "Capture: Queen", "Capture: Kingside Attendant",
            "Capture: Kingside Bishop", "Capture: Kingside Knight", "Capture: Kingside Rook",

            "Capture: Queenside Rook Pawn", "Capture: Queenside Knight Pawn", "Capture: Queenside Bishop Pawn",
            "Capture: Queenside Attendant Pawn", "Capture: Queen Pawn", "Capture: King Pawn", "Capture: Kingside Attendant Pawn",
            "Capture: Kingside Bishop Pawn", "Capture: Kingside Knight Pawn", "Capture: Kingside Rook Pawn",

            // TODO: more piece IDs for larger boards
        )

        val PIECE_SETS = listOf(
            // multiple of a piece type
            "Capture Set: 2 Pawns", "Capture Set: 4 Pawns", "Capture Set: 6 Pawns", "Capture Set: 8 Pawns", "Capture Set: 10 Pawns",
            "Capture Set: 2 Pieces", "Capture Set: 4 Pieces", "Capture Set: 6 Pieces", "Capture Set: 8 Pieces", "Capture Set: 10 Pieces",
            "Capture Set: 2 Minors", "Capture Set: 2 Majors",
            "Capture Set: 5 Total", "Capture Set: 10 Total", "Capture Set: 15 Total", "Capture Set: 20 Total"
        )

        val THREATS = listOf(
            "Threaten: Minor", "Threaten: Major", "Threaten: Queen", "Threaten: King"
        )

        val FORKS = listOf(
            "Fork: False", "Fork: False Triplet", "Fork: False Royal",
            "Fork: True", "Fork: True Triplet", "Fork: True Royal"
        )

        val PLACES = listOf(
            "King: Early Forward", "King: To Centre", "King: To Edge"
        )

        val SURVIVAL = listOf(
            "Survive: 5 Turns", "Survive: 10 Turns", "Survive: 20 Turns"
        )

        val OTHER = listOf(
            "Win: Mini Board", "Win: FIDE Board", "Win: Wide Board",
            // TODO: larger boards
            "Win Fast: 40 Turns", "Win Fast: 20 Turns"
        )

        val ALL_CHECKS = PIECES union PIECE_SETS union THREATS union FORKS union PLACES union SURVIVAL union OTHER

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

        fun examineMove(board: Board, startBoard: Board, move: Move) {
            // king move to place
            val movedPiece = board.state[move.start]!!
            if (movedPiece.type.type == PieceClass.KING) {
                val turns = board.curPly / 2
                val startY = move.start / board.width
                val endX = move.end % board.width
                val endY = move.end / board.width
                if (turns < 5 && move.start >= 0) {
                    if (endY > startY) collectLocation("King: Early Forward")
                }
                if (endX == 0 || endX == board.width - 1) {
                    // make sure its not a castling move
                    if (board.state[move.end] == null) collectLocation("King: To Edge")
                }
                if (endX == board.width / 2 || endX == board.width / 2 - 1) {
                    if (endY == board.height / 2 || endY == board.height / 2 - 1) collectLocation("King: To Centre")
                }
            }
            // check for captures
            if (move.capture != -1) {
                val piece = board.state[move.capture]!!
                val locStr = "Capture: ${piece.identifier}"
                if (PIECES.contains(locStr)) collectLocation(locStr)

                // check for capture sets
                var total = 1 // capturing this move
                for (piece in board.state) {
                    if (piece != null && !piece.isWhite) total--
                }
                for (piece in startBoard.state) {
                    if (piece != null && !piece.isWhite) total++
                }
                var loc = "Capture Set: $total Total"
                collectLocation(loc)
                total = 1
                val isPawn = piece.type.type == PieceClass.PAWN
                for (piece in board.state) {
                    if (piece != null && !piece.isWhite && ((piece.type.type == PieceClass.PAWN) == isPawn)) total--
                }
                for (piece in startBoard.state) {
                    if (piece != null && !piece.isWhite && ((piece.type.type == PieceClass.PAWN) == isPawn)) total++
                }
                loc = "Capture Set: $total ${if (isPawn) "Pawns" else "Pieces"}"
                collectLocation(loc)
                if (piece.type.type == PieceClass.MINOR) {
                    total = 1
                    for (piece in board.state) {
                        if (piece != null && !piece.isWhite && piece.type.type == PieceClass.MINOR) total--
                    }
                    for (piece in startBoard.state) {
                        if (piece != null && !piece.isWhite && piece.type.type == PieceClass.MINOR) total++
                    }
                    loc = "Capture Set: $total Minors"
                    collectLocation(loc)
                }
                if (piece.type.type == PieceClass.MAJOR) {
                    total = 1
                    for (piece in board.state) {
                        if (piece != null && !piece.isWhite && piece.type.type == PieceClass.MAJOR) total--
                    }
                    for (piece in startBoard.state) {
                        if (piece != null && !piece.isWhite && piece.type.type == PieceClass.MAJOR) total++
                    }
                    loc = "Capture Set: $total Majors"
                    collectLocation(loc)
                }
            }
        }

        fun examineBoardPostMove(board: Board, startBoard: Board) {
            // win
            if (board.isGameOver()) {
                if (board.isWhiteWin()) {
                    val boardLoc = getCurrentBoardLocation(startBoard)
                    collectLocation("Win: $boardLoc")
                    val turns = board.curPly / 2
                    for (loc in OTHER) {
                        if (loc.startsWith("Win Fast: ")) {
                            val locTime = loc.substring(10).substringBefore(' ').toInt()
                            if (turns < locTime) collectLocation(loc)
                        }
                    }
                }
            }
            // threaten and fork
            val withNull = board.nullMove()
            val moveList = MoveList()
            val capturesByPiece = HashMap<PieceWithPos, ArrayList<Move>>()
            withNull.getMoves(moveList)
            for (move in moveList) {
                if (move.capture != -1) {
                    val x = move.start % withNull.width
                    val y = move.start / withNull.width
                    val pos = PieceWithPos(withNull.state[move.start]!!, x, y)
                    if (capturesByPiece.containsKey(pos)) capturesByPiece[pos]!!.add(move)
                    else capturesByPiece[pos] = arrayListOf(move)

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
            moveList.reset()
            board.getMoves(moveList)
            for (pieceData in capturesByPiece) {
                if (pieceData.value.size < 2) continue // cant be a fork

                val thisWorth = pieceData.key.piece.type.cost
                val attacks = ArrayList<Piece>()
                for (move in pieceData.value) {
                    val hit = withNull.state[move.capture]!!
                    if (hit.type.cost > thisWorth || hit.type.type == PieceClass.KING) attacks.add(hit)
                    else {
                        val prot = board.getSquareCheapestDefender(move.capture % board.width, move.capture / board.width, false)
                        if (prot == 10000) attacks.add(hit)
                    }
                }
                val cheapestProtector = board.getSquareCheapestDefender(pieceData.key.x, pieceData.key.y, true)
                var trueAttack = true
                val thisPos = pieceData.key.y * board.width + pieceData.key.x
                for (move in moveList) {
                    if (move.capture == thisPos) {
                        if ((thisWorth + cheapestProtector) >= board.state[move.start]!!.type.cost) {
                            trueAttack = false
                            break
                        }
                    }
                }
                if (attacks.size >= 2) {
                    collectLocation("Fork: False")
                    if (trueAttack) collectLocation("Fork: True")
                    if (attacks.size >= 3) {
                        collectLocation("Fork: False Triplet")
                        if (trueAttack) collectLocation("Fork: True Triplet")
                    }
                    if (attacks.count { it.type.type == PieceClass.KING || it.type.type == PieceClass.QUEEN } >= 2) {
                        collectLocation("Fork: False Royal")
                        if (trueAttack) collectLocation("Fork: True Royal")
                    }
                }
            }
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
                "Capture Set" -> {
                    // TODO: determine what the set is
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
                "Fork" -> {
                    // TODO: determine if available or hard
                    return LocationState.AVAILABLE
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
                "Win Fast" -> {
                    // TODO: determine if hard
                    return LocationState.UNREACHABLE
                }
                else -> throw NotImplementedError()
            }
        }

        val collected = ArrayList<String>()
        fun collectLocation(location: String) {
            if (collected.contains(location)) return
            if (!ALL_CHECKS.contains(location)) return
            collected.add(location)

            // TODO: AP stuff
            println("[COLLECT] $location")
        }

        // TODO: function to load collected locations from AP
    }
}