import java.util.Random
import kotlin.math.max
import kotlin.math.min

interface ChessAI {
    fun getName(): String
    fun getDifficulty(): Int
    fun makeMove(board: Board): Move
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
                val moves = moveList.asArray().sortedByDescending { moveComparator(board, it) }
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
                val moves = moveList.asArray().sortedBy { moveComparator(board, it) }
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
    AI Level 1:
    - Simple minimax depth 6 ply with alpha-beta pruning for speed
    - Evaluation is purely based on piece values
    - Pieces in pocket are valued slightly higher, results in AI only using them to prevent losing material
    - Random value added to prevent shuffling
    */

    companion object {
        val PIECE_WORTH = mapOf(
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

    override fun getName() = "AI Level 1"
    override fun getDifficulty() = 1
    override fun makeMove(board: Board): Move {
        return SearchHelpers.minimaxAlphaBeta(board, 6, Int.MIN_VALUE, Int.MAX_VALUE, getBoardScore, scoreMove).move
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
        score += rng.nextInt(10) // noise to shuffle moves (will push A pawn and shuffle rook otherwise)
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