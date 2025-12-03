import kotlin.math.min

class ItemHelper {
    companion object {
        val ALL_ITEMS = mapOf(
            // new boards
            "Board: FIDE" to 1,
            "Board: Wide" to 1,
            // additional quantity of start piece types
            "Starting Piece: 2 Pawns" to 8, // +2 per
            "Starting Piece: 2 Minors" to 5, // +2 per
            "Starting Piece: Major" to 6,
            "Starting Piece: Queen" to 4,
            "Starting Piece: King" to 2, // +1 initial
            // reduce AI level of opponent
            "AI: Skill Downgrade" to 4, // planned 5 total levels
            // swap out pieces for worse ones
            "AI: Piece Downgrade" to 4, // unknown how many to use or if this will be split up
            // more initial points to set up board with
            "Setup: More Points" to 10, // +5 per
            // further advanced pawns in setup
            "Setup: Advanced Pawns" to 2, // max rank 4 (initial 2)
            // further advanced pieces in setup
            "Setup: Advanced Pieces" to 1, // max rank 2 (initial 1)
            // unlocks a pocket slot
            "Unlock: Pocket Slot" to 3,
            // unlocks fairy piece types
            "Unlock: Fairy Pawns" to 1,
            "Unlock: Fairy Minors" to 1,
            "Unlock: Fairy Majors" to 1,
            "Unlock: Fairy Queens" to 1,
            "Unlock: Fairy Kings" to 1
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
            // NOTE: FIDE setup is 43pts, max points is 10x5+10=60pts
            return getItemCount("Setup: More Points") * 5 + 10
        }

        fun getEffectiveCostAllowance(): Int {
            // used for location checks
            val costAllowance = getCostAllowance()
            // TODO: increase multipliers if fairy pieces are in play
            val pieceAllowance =
                getNumPieceClass(PieceClass.PAWN) +
                getNumPieceClass(PieceClass.MINOR) * 3 +
                getNumPieceClass(PieceClass.MAJOR) * 5 +
                getNumPieceClass(PieceClass.QUEEN) * 8 +
                getNumPieceClass(PieceClass.KING) * 5
            return min(costAllowance, pieceAllowance)
        }

        fun getPocketCount() = getItemCount("Unlock: Pocket Slot")

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
            val boards = BoardSetups.ALL_BOARDS.filter { it.value == board }
            if (boards.isEmpty()) return false
            val theBoard = boards.toList().first() // should only ever be one
            if (theBoard.first == "Mini") return true
            return getItemCount("Board: ${theBoard.first}") > 0
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
            - White kings are all central and not pocket
            Returns null if valid, or the reason it failed
            */
            var availCost = getCostAllowance()
            var availPawn = getNumPieceClass(PieceClass.PAWN)
            var availMinor = getNumPieceClass(PieceClass.MINOR)
            var availMajor = getNumPieceClass(PieceClass.MAJOR)
            var availQueen = getNumPieceClass(PieceClass.QUEEN)
            var availKing = getNumPieceClass(PieceClass.KING)
            var hasKing = false
            val maxPawnRank = getItemCount("Setup: Advanced Pawns") + 1
            val maxPieceRank = getItemCount("Setup: Advanced Pieces")
            var maxPockets = getPocketCount()
            var possibleBoards = BoardSetups.ALL_BOARDS.values.filter {
                it.width == board.width &&
                it.height == board.height &&
                canUseBoard(it)
            }

            fun checkPiece(piece: Piece, x: Int, y: Int): String? {
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
                        if (x == -1) return "King in pocket"
                        if (x < 2 || x >= board.width - 2) return "King not central"
                        if (--availKing < 0) return "Too many kings"
                        if (y > maxPieceRank) return "Pieces too advanced"
                        hasKing = true
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
                        val res = checkPiece(piece, x, y)
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
                    val res = checkPiece(piece, -1, 0)
                    if (res != null) return res
                }
            }
            return if (hasKing) null else "No king"
        }
    }
}