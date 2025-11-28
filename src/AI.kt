import java.util.Random

class AI1 : ChessAI {
    /*
    AI Level 1:
    - 4 ply depth, no extensions
    - Evaluation is sum of pieces, non-pawns are worth double
    - Random value added to prevent shuffling
    */

    override fun getName() = "AI Level 1"
    override fun makeMove(board: Board): MoveResult {
        return AIHelpers.minimaxAlphaBeta(board, 4, 0, getBoardScore, scoreMove)
    }

    private val rng = Random()
    private val getBoardScore: (Board) -> Int = { board ->
        var score = 0
        for (a in 0 until board.state.size) {
            val piece = board.state[a] ?: continue
            val worth = if (piece.type.type == PieceClass.PAWN) 100 else 200
            if (piece.isWhite) score += worth else score -= worth
        }
        for (a in 0 until board.pockets.size) {
            val piece = board.pockets[a] ?: continue
            val worth = if (piece.type.type == PieceClass.PAWN) 100 else 200
            if (piece.isWhite) score += worth else score -= worth
        }
        score += rng.nextInt(10) // noise to shuffle moves
        score
    }

    private val scoreMove: (Board, Move) -> Int = { board, move ->
        var change = 0
        if (move.promote != null) {
            // promoting
            if (board.isWhiteToMove) change++ else change--
        }
        if (move.capture != -1) {
            // capture
            val piece = board.state[move.capture]!!
            val worth = if (piece.type.type == PieceClass.PAWN) 1 else 2
            if (piece.isWhite) change -= worth else change += worth
        }
        change
    }
}

class AI2 : ChessAI {
    /*
    AI Level 2:
    - 4 ply depth, no extensions
    - Evaluation is sum of piece values defined below
    - Pieces in pocket are valued slightly higher (+15), results in AI only using them to prevent losing material
    - Random value added to prevent shuffling
    */

    override fun getName() = "AI Level 2"
    override fun makeMove(board: Board): MoveResult {
        return AIHelpers.minimaxAlphaBeta(board, 4, 0, getBoardScore, scoreMove)
    }

    companion object {
        private val rng = Random()
        val getBoardScore: (Board) -> Int = { board ->
            var score = 0
            for (a in 0 until board.state.size) {
                val piece = board.state[a] ?: continue
                val worth = AIHelpers.PIECE_WORTH_SIMPLE[piece.type]!!
                if (piece.isWhite) score += worth else score -= worth
            }
            for (a in 0 until board.pockets.size) {
                val piece = board.pockets[a] ?: continue
                val worth = AIHelpers.PIECE_WORTH_SIMPLE[piece.type]!! + 15 // slight value to keeping pocketed
                if (piece.isWhite) score += worth else score -= worth
            }
            score += rng.nextInt(10) // noise to shuffle moves
            score
        }

        val scoreMove: (Board, Move) -> Int = { board, move ->
            if (move.start < 0) {
                // using a pocket
                if (move.start < -3) 15 else -15
            }
            else {
                var change = 0
                if (move.promote != null) {
                    // promoting
                    val diff = AIHelpers.PIECE_WORTH_SIMPLE[move.promote]!! - AIHelpers.PIECE_WORTH_SIMPLE[board.state[move.start]!!.type]!!
                    if (board.isWhiteToMove) change += diff else change -= diff
                }
                if (move.capture != -1) {
                    // capture
                    val piece = board.state[move.capture]!!
                    if (piece.isWhite) change -= AIHelpers.PIECE_WORTH_SIMPLE[piece.type]!! else change += AIHelpers.PIECE_WORTH_SIMPLE[piece.type]!!
                }
                change
            }
        }
    }
}

class AI3 : ChessAI {
    /*
    AI Level 3:
    - 4 ply depth, 4 ply extensions
    - Otherwise identical to AI Level 2
    */

    override fun getName() = "AI Level 3"
    override fun makeMove(board: Board): MoveResult {
        return AIHelpers.minimaxAlphaBeta(board, 4, 4, AI2.getBoardScore, AI2.scoreMove)
    }
}