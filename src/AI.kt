import java.util.Random

interface ChessAI {
    fun getName(): String
    fun getDifficulty(): Int
    fun makeMove(board: Board): Move
}

class AI1 : ChessAI {
    // TODO: increase depth to 6 ply when optimised
    /*
    AI Level 1:
    - Simple minimax depth 4 ply
    - Evaluation is purely based on piece values, preferring to get it earlier if possible
    - Pieces in pocket are valued slightly higher, results in AI using them to prevent losing material
    - Random move chosen from best options
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
        return doMinimax(board, 4).first
    }

    private val rng = Random()
    private fun doMinimax(board: Board, depth: Int): Pair<Move, Int> {
        // TODO: this is horrendously slow

        if (board.isGameOver()) {
            if (board.isWhiteWin()) return Pair(Move.NULL, 1000000 + depth)
            if (board.isBlackWin())return Pair(Move.NULL, -(1000000 + depth))
            throw IllegalStateException()
        }

        if (depth <= 0) return Pair(Move.NULL, getBoardScore(board))

        val moves = MoveList()
        board.getMoves(moves)
        val bestMoves = ArrayList<Move>()
        var bestScore = if (board.isWhiteToMove) -10000000 else 10000000
        for (a in 0 until moves.used) {
            val newBoard = board.withMove(moves[a])
            val result = doMinimax(newBoard, depth - 1)
            if (result.second == bestScore) bestMoves.add(moves[a])
            else {
                if (result.second > bestScore && board.isWhiteToMove) {
                    bestMoves.clear()
                    bestMoves.add(moves[a])
                    bestScore = result.second
                }
                if (result.second < bestScore && !board.isWhiteToMove) {
                    bestMoves.clear()
                    bestMoves.add(moves[a])
                    bestScore = result.second
                }
            }
        }
        return Pair(bestMoves[rng.nextInt(bestMoves.size)], bestScore)
    }

    private fun getBoardScore(board: Board): Int {
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
        return score
    }
}