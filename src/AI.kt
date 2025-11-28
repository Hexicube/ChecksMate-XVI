import java.util.Random
import kotlin.math.max
import kotlin.math.min

interface ChessAI {
    fun getName(): String
    fun makeMove(board: Board): MoveResult
}

data class MoveResult(var move: Move, val score: Int)
class SearchHelpers {
    companion object {
        // TODO: killer moves
        private val moveList = MoveList()
        fun minimaxAlphaBeta(board: Board, depth: Int, alpha: Int, beta: Int, scoreFunc: (Board) -> Int, moveComparator: (Board, Move) -> Int): MoveResult {
            if (depth == 0) return MoveResult(Move.NULL, scoreFunc(board))
            if (board.isGameOver()) {
                if (board.isWhiteWin()) return MoveResult(Move.NULL, 1000000)
                if (board.isBlackWin()) return MoveResult(Move.NULL, -1000000)
            }

            var value: Int
            var result: MoveResult
            lateinit var bestMove: MoveResult
            moveList.reset()
            board.getMoves(moveList)
            var a = alpha
            var b = beta
            if (board.isWhiteToMove) {
                value = Int.MIN_VALUE
                val moves = moveList.sortedByDescending { moveComparator(board, it) }
                for (move in moves) {
                    result = minimaxAlphaBeta(board.withMove(move), depth - 1, a, b, scoreFunc, moveComparator)
                    if (result.score > value) {
                        bestMove = MoveResult(move, result.score)
                        value = result.score
                        if (value >= b) break
                        a = max(value, a)
                    }
                }
                return bestMove
            }
            else {
                value = Int.MAX_VALUE
                val moves = moveList.sortedBy { moveComparator(board, it) }
                for (move in moves) {
                    result = minimaxAlphaBeta(board.withMove(move), depth - 1, a, b, scoreFunc, moveComparator)
                    result.move = move
                    if (result.score < value) {
                        bestMove = MoveResult(move, result.score)
                        value = result.score
                        if (value <= a) break
                        b = min(value, b)
                    }
                }
                return bestMove
            }
        }
    }
}

class AI1 : ChessAI {
    /*
    AI Level 2:
    - 4 ply depth, no extensions
    - Evaluation is sum of pieces, non-pawns are worth double
    - Random value added to prevent shuffling
    */

    override fun getName() = "AI Level 1"
    override fun makeMove(board: Board): MoveResult {
        return SearchHelpers.minimaxAlphaBeta(board, 4, Int.MIN_VALUE, Int.MAX_VALUE, getBoardScore, scoreMove)
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

    companion object {
        private val PIECE_WORTH = mapOf(
            PieceType.PAWN to 100,
            PieceType.KNIGHT to 250,
            PieceType.BISHOP to 300,
            PieceType.ROOK to 500,
            PieceType.QUEEN to 800,
            PieceType.KING to 400,

            PieceType.BEROLINA to 100,
            PieceType.SOLDIER to 100,
            PieceType.SERGEANT to 150,

            PieceType.CAMEL to 275,
            PieceType.CLERIC to 325,
            PieceType.PHOENIX to 300,
            PieceType.TOWER to 200,
            PieceType.COMMONER to 375,

            PieceType.ARCHBISHOP to 550,
            PieceType.LION to 550,

            PieceType.UNICORN to 800,
            PieceType.ELEPHANT to 925,
            PieceType.CHANCELLOR to 950,
            PieceType.DRAGON to 1100,
            PieceType.AMAZON to 1250,

            PieceType.GENERAL to 650,
            PieceType.CROWNQUEEN to 1000
        )
    }

    override fun getName() = "AI Level 2"
    override fun makeMove(board: Board): MoveResult {
        return SearchHelpers.minimaxAlphaBeta(board, 4, Int.MIN_VALUE, Int.MAX_VALUE, getBoardScore, scoreMove)
    }

    private val rng = Random()
    private val getBoardScore: (Board) -> Int = { board ->
        var score = 0
        for (a in 0 until board.state.size) {
            val piece = board.state[a] ?: continue
            val worth = PIECE_WORTH[piece.type]!!
            if (piece.isWhite) score += worth else score -= worth
        }
        for (a in 0 until board.pockets.size) {
            val piece = board.pockets[a] ?: continue
            val worth = PIECE_WORTH[piece.type]!! + 15 // slight value to keeping pocketed
            if (piece.isWhite) score += worth else score -= worth
        }
        score += rng.nextInt(10) // noise to shuffle moves
        score
    }

    private val scoreMove: (Board, Move) -> Int = { board, move ->
        if (move.start < 0) {
            // using a pocket
            if (move.start < -3) 15 else -15
        }
        else {
            var change = 0
            if (move.promote != null) {
                // promoting
                val diff = PIECE_WORTH[move.promote]!! - PIECE_WORTH[board.state[move.start]!!.type]!!
                if (board.isWhiteToMove) change += diff else change -= diff
            }
            if (move.capture != -1) {
                // capture
                val piece = board.state[move.capture]!!
                if (piece.isWhite) change -= PIECE_WORTH[piece.type]!! else change += PIECE_WORTH[piece.type]!!
            }
            change
        }
    }
}