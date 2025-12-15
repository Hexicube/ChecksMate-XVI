enum class LocationState {
    UNREACHABLE, HARD, AVAILABLE, COLLECTED
}

class LocationHelper {
    companion object {
        val PIECES = mapOf(
            // specific pieces
             1 to "Capture: Queenside Rook",
             2 to "Capture: Queenside Knight",
             3 to "Capture: Queenside Bishop",
             4 to "Capture: Queenside Attendant",
             5 to "Capture: Queen",
             7 to "Capture: Kingside Attendant",
             8 to "Capture: Kingside Bishop",
             9 to "Capture: Kingside Knight",
            10 to "Capture: Kingside Rook",

            51 to "Capture: Queenside Rook Pawn",
            52 to "Capture: Queenside Knight Pawn",
            53 to "Capture: Queenside Bishop Pawn",
            54 to "Capture: Queenside Attendant Pawn",
            55 to "Capture: Queen Pawn",
            56 to "Capture: King Pawn",
            57 to "Capture: Kingside Attendant Pawn",
            58 to "Capture: Kingside Bishop Pawn",
            59 to "Capture: Kingside Knight Pawn",
            60 to "Capture: Kingside Rook Pawn",

            // TODO: more piece IDs for larger boards
        )

        val PIECE_SETS = mapOf(
            // multiple of a piece type
            101 to "Capture Set: 2 Pawns",
            102 to "Capture Set: 4 Pawns",
            103 to "Capture Set: 6 Pawns",
            104 to "Capture Set: 8 Pawns",
            105 to "Capture Set: 10 Pawns",

            111 to "Capture Set: 2 Pieces",
            112 to "Capture Set: 4 Pieces",
            113 to "Capture Set: 6 Pieces",
            114 to "Capture Set: 8 Pieces",
            115 to "Capture Set: 10 Pieces",

            121 to "Capture Set: 2 Minors",
            122 to "Capture Set: 2 Majors",

            131 to "Capture Set: 5 Total",
            132 to "Capture Set: 10 Total",
            133 to "Capture Set: 15 Total",
            134 to "Capture Set: 20 Total"
        )

        val THREATS = mapOf(
            151 to "Threaten: Minor",
            152 to "Threaten: Major",
            153 to "Threaten: Queen",
            154 to "Threaten: King"
        )

        val FORKS = mapOf(
            161 to "Fork: False",
            162 to "Fork: False Triplet",
            163 to "Fork: False Royal",
            166 to "Fork: True",
            167 to "Fork: True Triplet",
            168 to "Fork: True Royal"
        )

        val PLACES = mapOf(
            181 to "King: Early Forward",
            182 to "King: To Centre",
            183 to "King: To Edge"
        )

        val SURVIVAL = mapOf(
            201 to "Survive: 5 Turns",
            202 to "Survive: 10 Turns",
            203 to "Survive: 20 Turns"
        )

        val OTHER = mapOf(
            206 to "Win Fast: 40 Turns",
            207 to "Win Fast: 20 Turns",
            211 to "Win: Mini Board",
            212 to "Win: FIDE Board",
            213 to "Win: Wide Board"
            // TODO: larger boards
        )

        val ALL_CHECKS = PIECES + PIECE_SETS + THREATS + FORKS + PLACES + SURVIVAL + OTHER

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
                if (SURVIVAL.values.contains(locStr)) collectLocation(locStr)
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
                if (PIECES.values.contains(locStr)) collectLocation(locStr)

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
                    for (loc in OTHER.values) {
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
            if (!PIECES.values.contains(locStr)) return false
            return !collected.contains(locStr)
        }

        fun getEnemyPieceSum(board: Board): Int {
            var count = 0
            for (piece in board.state) {
                if (piece != null && !piece.isWhite) count += piece.type.cost
            }
            return count
        }

        fun getLocationState(board: Board, startBoard: Board, location: String): LocationState {
            if (collected.contains(location)) return LocationState.COLLECTED
            val locType = location.substringBefore(':')
            val locName = location.substring(location.indexOf(':') + 2)
            when (locType) {
                "Capture" -> {
                    var isAvailable = false
                    val curValue = ItemHelper.getEffectiveCostAllowance()
                    for (boardType in BoardSetups.ALL_BOARDS) {
                        if (boardType.key == "Mini" || ItemHelper.getItemCount("Board: ${boardType.key}") > 0) {
                            val reqValue = getEnemyPieceSum(boardType.value) * ChessBoard.currentAI.getStrengthModifier()
                            val idents = BoardSetups.BOARD_ENEMY_PIECE_TYPES[boardType.value]!!
                            for (id in idents) {
                                if (id == locName) {
                                    // pawns are always easy
                                    if (id.contains("Pawn")) return LocationState.AVAILABLE
                                    // check difficulty
                                    if (curValue >= reqValue) return LocationState.AVAILABLE
                                    isAvailable = true
                                }
                            }
                        }
                    }
                    return if (isAvailable) LocationState.HARD else LocationState.UNREACHABLE
                }
                "Capture Set" -> {
                    // TODO: determine what the set is
                    // TODO: go over all boards and calculate how many the player is expected to be able to capture
                    // TODO: unreachable if no board has the quantity needed, hard if the player is not expected to capture them, available otherwise
                    // PAWNS: requires as many pieces are there are pawns (non-pawns worth 2x)
                    // MINORS: requires piece value of 3x piece count requires
                    // MAJORS: requires 5x
                    // TOTAL: requires 3x (assumes player will prioritise pawns)
                    return LocationState.UNREACHABLE
                }
                "Threaten" -> {
                    // TODO: determine if the piece class is available on any unlocked board (mini board has no majors)
                    // TODO: king/queen checks should be hard if player has no unlocks (excluding pawns)
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
                    // TODO: always hard if player has no unlocks or too few points
                    // TODO: false forks are available if player has minors
                    // TODO: true forks are available if player has majors or queens
                    // TODO: royal forks (true or false) are available if the player has a decent advantage (2x? to afford throwing away pieces)
                    return LocationState.AVAILABLE
                }
                "King" -> {
                    // move king to specific places
                    return LocationState.AVAILABLE
                }
                "Survive" -> {
                    val miniBoard = BoardSetups.MINI_BOARD // easiest board
                    val reqValue = getEnemyPieceSum(miniBoard) * ChessBoard.currentAI.getStrengthModifier()
                    val curValue = ItemHelper.getEffectiveCostAllowance()
                    if (curValue >= reqValue) return LocationState.AVAILABLE
                    // nonsense approximation for win speed: assume 50 turns with equal footing and a simple division
                    val expectedTurns = (50f * curValue.toFloat() / reqValue).toInt()
                    val locTurns = locName.substringBefore(' ').toInt()
                    return if (expectedTurns >= locTurns) LocationState.AVAILABLE else LocationState.HARD
                }
                "Win" -> {
                    val boardLoc = getCurrentBoardLocation(startBoard)
                    if (boardLoc != locName) return LocationState.UNREACHABLE
                    val reqValue = getEnemyPieceSum(startBoard) * ChessBoard.currentAI.getStrengthModifier()
                    val curValue = ItemHelper.getEffectiveCostAllowance()
                    return if (curValue >= reqValue) LocationState.AVAILABLE else LocationState.HARD
                }
                "Win Fast" -> {
                    val miniBoard = BoardSetups.MINI_BOARD // easiest board
                    val reqValue = getEnemyPieceSum(miniBoard) * ChessBoard.currentAI.getStrengthModifier()
                    val curValue = ItemHelper.getEffectiveCostAllowance()
                    if (curValue <= reqValue) return LocationState.HARD
                    // nonsense approximation for win speed: assume 50 turns with equal footing and a simple division
                    val expectedTurns = (50f * reqValue / curValue.toFloat()).toInt()
                    val locTurns = locName.substringBefore(' ').toInt()
                    return if (expectedTurns <= locTurns) LocationState.AVAILABLE else LocationState.HARD
                }
                else -> throw NotImplementedError()
            }
        }

        val collected = ArrayList<String>()
        fun collectLocation(location: String) {
            if (collected.contains(location)) return
            if (!ALL_CHECKS.values.contains(location)) return
            collected.add(location)

            // TODO: AP stuff
            println("[COLLECT] $location")
        }

        // TODO: function to load collected locations from AP
    }
}