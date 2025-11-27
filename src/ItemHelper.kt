class ItemHelper {
    companion object {
        val BOARDS = listOf(
            // grant access to new boards
            "Board: FIDE", "Board: Wide"
        )

        val STARTING_PIECES = listOf(
            // allow setting up more pieces on the board
            "Starting Piece: Pawn",  // x8 (+2 per)
            "Starting Piece: Minor", // x5 (+2 per)
            "Starting Piece: Major", // x6
            "Starting Piece: Queen", // x4
            "Starting Piece: King"   // x2 (+1 from initial state)
        )

        val PROGRESSIVES = listOf(
            // swap the AI for a lower level version
            "AI: Skill downgrade", // multiple
            // swap an AI piece for one with lower value
            "AI: Piece downgrade", // multiple
            // swap an AI pocket piece for one with lower value, or remove it entirely
            "AI: Pocket downgrade", // multiple
            // add points to set up your board with
            "Setup: More points", // multiple, +5 per
            // allow placing pawns further on the board (initial allows rank 1/2)
            "Setup: Advanced pawns", // x2 (up to rank 4)
            // allow placing non-pawns further on the board (initial allows rank 1)
            "Setup: Advanced pieces" // x1 (up to rank 2)
        )

        val UNLOCKS = listOf(
            "Unlock: Pocket slot", // x3
            "Unlock: Fairy Pawns",
            "Unlock: Fairy Minors",
            "Unlock: Fairy Majors",
            "Unlock: Fairy Queens",
            "Unlock: Fairy Kings",
        )

        val collected = ArrayList<String>()
        fun grantItem(item: String) {
            collected += item
        }

        // TODO: function to load received items from AP

        fun getItemCount(item: String): Int {
            return collected.count { it == item }
        }

        // helper functions

        fun getCostAllowance(): Int {
            return getItemCount("Setup: More points") * 5 + 10
        }

        fun getPocketCount() = getItemCount("Unlock: Pocket slot")

        fun canUsePiece(type: PieceType): Boolean {
            if (PieceType.FIDE_PIECES.contains(type)) return true // start with standard pieces
            val typeName = when (type.type) {
                PieceClass.PAWN -> "Pawns"
                PieceClass.MINOR -> "Minors"
                PieceClass.MAJOR -> "Majors"
                PieceClass.QUEEN -> "Queens"
                PieceClass.KING -> "Kings"
            }
            // TODO: progressive?
            return getItemCount("Unlock: Fairy $typeName") > 0
        }

        fun getNumPieceClass(type: PieceClass): Int {
            val typeName = when (type) {
                PieceClass.PAWN -> "Pawn"
                PieceClass.MINOR -> "Minor"
                PieceClass.MAJOR -> "Major"
                PieceClass.QUEEN -> "Queen"
                PieceClass.KING -> "King"
            }
            var items = getItemCount("Starting Piece: $typeName")
            if (type == PieceClass.KING) items++ // start with a king
            if (type == PieceClass.PAWN || type == PieceClass.MINOR) items *= 2
            return items
        }

        fun canUseBoard(board: Board): Boolean {
            val boardName = when (board) {
                BoardSetups.MINI_BOARD -> return true // start with mini board
                BoardSetups.FIDE -> "FIDE"
                BoardSetups.WIDE -> "Wide"
                else -> throw IllegalArgumentException()
            }

            return getItemCount("Board: $boardName") > 0
        }

        fun clearBoard(board: Board): Board {
            // removes all white pieces from the board
            val newState = board.state.clone()
            for (a in 0 until newState.size) {
                if (newState[a] != null && newState[a]!!.isWhite) newState[a] = null
            }
            val newPocket = board.pockets.clone()
            newPocket[0] = null
            newPocket[1] = null
            newPocket[2] = null
            return Board(board.width, board.height, newState, newPocket, true, 0, Move.NULL, board.promoteOptions)
        }

        fun validateBoard(board: Board): String? {
            /*
            Make sure the provided board follows the following constraints:
            - Size and black pieces match an unlocked board
            - White pieces are all unlocked
            - White piece type counts do not exceed type limits
            - White pieces are placed within progressive advancement limits
            - White piece cost total does not exceed allowance
            - White pockets are unlocked if used
            Returns null if valid, or the reason it failed
            */
            var availCost = getCostAllowance()
            var availPawn = getNumPieceClass(PieceClass.PAWN)
            var availMinor = getNumPieceClass(PieceClass.MINOR)
            var availMajor = getNumPieceClass(PieceClass.MAJOR)
            var availQueen = getNumPieceClass(PieceClass.QUEEN)
            var availKing = getNumPieceClass(PieceClass.KING)
            var hasKing = false
            val maxPawnRank = getItemCount("Setup: Advanced pawns") + 1
            val maxPieceRank = getItemCount("Setup: Advanced pieces")
            var maxPockets = getPocketCount()
            var possibleBoards = BoardSetups.ALL_BOARDS.filter {
                it.width == board.width &&
                it.height == board.height &&
                canUseBoard(it)
            }

            fun checkPiece(piece: Piece, y: Int): String? {
                availCost -= piece.type.cost
                if (availCost < 0) return "Over cost allowance"
                when (piece.type.type) {
                    PieceClass.PAWN -> {
                        if (--availPawn < 0) return "Too many pawns"
                        if (y > maxPawnRank) return "Pawns too advanced"
                    }
                    PieceClass.MINOR -> {
                        if (--availMinor < 0) return "Too many minors"
                        if (y > maxPieceRank) return "Pieces too advanced"
                    }
                    PieceClass.MAJOR -> {
                        if (--availMajor < 0) return "Too many majors"
                        if (y > maxPieceRank) return "Pieces too advanced"
                    }
                    PieceClass.QUEEN -> {
                        if (--availQueen < 0) return "Too many queens"
                        if (y > maxPieceRank) return "Pieces too advanced"
                    }
                    PieceClass.KING -> {
                        hasKing = true
                        if (--availKing < 0) return "Too many kings"
                        if (y > maxPieceRank) return "Pieces too advanced"
                    }
                }
                return if (canUsePiece(piece.type)) null else "${piece.type.niceName} not unlocked"
            }

            for (x in 0 until board.width) {
                for (y in 0 until board.height) {
                    val idx = y * board.width + x
                    val piece = board.state[idx]
                    if (piece == null) {
                        possibleBoards = possibleBoards.filter {
                            val otherPiece = it.state[idx]
                            otherPiece == null || otherPiece.isWhite
                        }
                        if (possibleBoards.isEmpty()) return "Board not unlocked"
                    }
                    else if (piece.isWhite) {
                        val res = checkPiece(piece, y)
                        if (res != null) return res
                    }
                    else {
                        possibleBoards = possibleBoards.filter {
                            val otherPiece = it.state[idx]
                            otherPiece != null && !otherPiece.isWhite
                        }
                        if (possibleBoards.isEmpty()) return "Board not unlocked"
                    }
                }
            }
            for (a in 0 until 3) {
                val piece = board.pockets[a]
                if (piece != null) {
                    if (--maxPockets < 0) return "Too many pocket pieces"
                    val res = checkPiece(piece, 0)
                    if (res != null) return res
                }
            }
            return if (hasKing) null else "No king"
        }
    }
}