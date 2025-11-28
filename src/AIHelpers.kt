import kotlin.math.max
import kotlin.math.min

interface ChessAI {
    fun getName(): String
    fun makeMove(board: Board): MoveResult
}

data class MoveResult(var move: Move, val score: Int)
class AIHelpers {
    companion object {
        val PIECE_WORTH_SIMPLE = mapOf(
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

        private val moveList = MoveList()
        fun minimaxAlphaBeta(board: Board, depth: Int, extension: Int, scoreFunc: (Board) -> Int, moveComparator: (Board, Move) -> Int, alpha: Int = Int.MIN_VALUE, beta: Int = Int.MAX_VALUE): MoveResult {
            if (board.isGameOver()) {
                val depthLeft = depth + extension
                if (board.isWhiteWin()) return MoveResult(Move.NULL, 1000000 + depthLeft)
                if (board.isBlackWin()) return MoveResult(Move.NULL, -1000000 - depthLeft)
            }
            if (depth + extension <= 0) return MoveResult(Move.NULL, scoreFunc(board))

            var value: Int
            var result: MoveResult
            var bestMove = MoveResult(Move.NULL, scoreFunc(board))
            moveList.reset()
            board.getMoves(moveList)
            var a = alpha
            var b = beta
            var newDepth: Int
            var newExtension: Int
            if (board.isWhiteToMove) {
                value = Int.MIN_VALUE
                val moves = moveList.sortedByDescending { moveComparator(board, it) }
                for (move in moves) {
                    if (move.capture == -1) {
                        if (depth <= 0) continue
                        newDepth = depth - 1
                        newExtension = extension
                    }
                    else {
                        newDepth = depth
                        newExtension = extension - 1
                    }
                    result = minimaxAlphaBeta(board.withMove(move), newDepth, newExtension, scoreFunc, moveComparator, a, b)
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
                    if (move.capture == -1) {
                        if (depth <= 0) continue
                        newDepth = depth - 1
                        newExtension = extension
                    }
                    else {
                        newDepth = depth
                        newExtension = extension - 1
                    }
                    result = minimaxAlphaBeta(board.withMove(move), newDepth, newExtension, scoreFunc, moveComparator, a, b)
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