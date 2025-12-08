import AI2.Companion.rng
import java.util.Random

class AI1 : ChessAI {
    /*
    AI Level 1:
    - 4 ply depth, no extensions
    - Evaluation is sum of pieces, non-pawns are worth double
    - Random value added to prevent shuffling
    */

    override fun getName() = "AI Level 1"
    override fun getStrengthModifier() = .5f
    override fun makeMove(board: Board): MoveResult {
        return AIHelpers.minimaxAlphaBeta(board, 4, 0, getBoardScore, scoreMove)
    }

    companion object {
        private val rng = Random()
        val getBoardScore: (Board) -> Int = { board ->
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

        val scoreMove: (Board, Move) -> Int = { board, move ->
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
}

class AI2 : ChessAI {
    /*
    AI Level 2:
    - 4 ply depth, no extensions
    - Evaluation is sum of piece values
    - Pieces in pocket are valued slightly higher (+15), results in AI only using them to prevent losing material
    - Random value added to prevent shuffling
    */

    override fun getName() = "AI Level 2"
    override fun getStrengthModifier() = .8f
    override fun makeMove(board: Board): MoveResult {
        return AIHelpers.minimaxAlphaBeta(board, 4, 0, getBoardScore, scoreMove)
    }

    companion object {
        private val rng = Random()
        val getBoardScore: (Board) -> Int = { board ->
            var score = 0
            for (a in 0 until board.state.size) {
                val piece = board.state[a] ?: continue
                val worth = AIHelpers.PIECE_WORTH_SIMPLE[piece.type.ID.toInt()]
                if (piece.isWhite) score += worth else score -= worth
            }
            for (a in 0 until board.pockets.size) {
                val piece = board.pockets[a] ?: continue
                val worth = AIHelpers.PIECE_WORTH_SIMPLE[piece.type.ID.toInt()] + 15 // slight value to keeping pocketed
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
                    val diff = AIHelpers.PIECE_WORTH_SIMPLE[move.promote!!.ID.toInt()] - AIHelpers.PIECE_WORTH_SIMPLE[board.state[move.start]!!.type.ID.toInt()]
                    if (board.isWhiteToMove) change += diff else change -= diff
                }
                if (move.capture != -1) {
                    // capture
                    val piece = board.state[move.capture]!!
                    if (piece.isWhite) change -= AIHelpers.PIECE_WORTH_SIMPLE[piece.type.ID.toInt()] else change += AIHelpers.PIECE_WORTH_SIMPLE[piece.type.ID.toInt()]
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
    override fun getStrengthModifier() = .9f
    override fun makeMove(board: Board): MoveResult {
        return AIHelpers.minimaxAlphaBeta(board, 4, 4, AI2.getBoardScore, AI2.scoreMove)
    }
}

class AI4 : ChessAI {
    /*
    AI Level 4:
    - 4 ply depth
    - Evaluation is sum of piece values, plus mobility
    - Pieces in pocket are valued slightly higher (+5)
    TODO: Replace LV3 with this and give it +2 extension?
    */

    override fun getName() = "AI Level 4"
    override fun getStrengthModifier() = 1f
    override fun makeMove(board: Board): MoveResult {
        return AIHelpers.minimaxAlphaBeta(board, 4, 0, getBoardScore, AI1.scoreMove)
    }

    companion object {
        private val moveHolder = MoveList()
        val getBoardScore: (Board) -> Int = { board ->
            // sum of piece values plus points for mobility
            var score = 0
            for (a in 0 until board.state.size) {
                val piece = board.state[a] ?: continue
                val worth = AIHelpers.PIECE_WORTH_SIMPLE[piece.type.ID.toInt()]
                if (piece.isWhite) score += worth else score -= worth
            }
            for (a in 0 until board.pockets.size) {
                val piece = board.pockets[a] ?: continue
                val worth = AIHelpers.PIECE_WORTH_SIMPLE[piece.type.ID.toInt()] + 5
                if (piece.isWhite) score += worth else score -= worth
            }
            moveHolder.reset()
            board.getMoves(moveHolder)
            var mobility = moveHolder.used
            moveHolder.reset()
            board.nullMove().getMoves(moveHolder)
            mobility -= moveHolder.used
            if (board.isWhiteToMove) score += mobility else score -= mobility
            score
        }
    }
}