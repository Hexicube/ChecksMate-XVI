import javax.swing.*
import kotlin.system.exitProcess

data class BoardWithMove(val board: Board, val move: Move)

val LEGAL_MOVES = listOf(
    20, 400, 8902, 197281, 4865609,
    119060324, 3195901860, 84998978956, 2439530234167
)
val validMoves = arrayOf(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L)

fun main() {
    // prepare status window
    val frame = JFrame("FIDE Verification")
    val content = JPanel()
    content.layout = BoxLayout(content, BoxLayout.Y_AXIS)
    val progressLabel = JLabel("0/s")
    content.add(progressLabel)
    val validMoveBars = Array(9) {
        val bar = JProgressBar()
        bar.maximum = 10000
        bar.isStringPainted = true
        content.add(bar)
        bar
    }
    frame.contentPane = content
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.pack()
    frame.isResizable = false
    frame.isVisible = true

    // start evaluations
    val worker = FIDEWorker()
    worker.execute()

    var priorSum = 0L
    var lastTime = System.currentTimeMillis()
    while (worker.state != SwingWorker.StateValue.DONE) {
        Thread.sleep(1000)
        var sum = 0L
        for (a in 0 until 9) {
            sum += validMoves[a]
            val pct = validMoves[a].toFloat() / LEGAL_MOVES[a].toFloat() * 100f
            val bar = validMoveBars[a]
            bar.value = (pct * 100f).toInt()
            bar.string = "%3.2f%%".format(pct)
        }
        val justDone = sum - priorSum
        val time = System.currentTimeMillis()
        progressLabel.text = "%,d/s".format(justDone * 1000 / (time - lastTime))
        lastTime = time
        priorSum = sum
    }
    for (a in 0 until 9) println("Ply ${a+1}: ${validMoves[a]} (${validMoves[a]-LEGAL_MOVES[a]})")
    exitProcess(0)
}

class FIDEWorker : SwingWorker<Unit, Unit>() {
    override fun doInBackground() {
        val startingBoard = BoardSetups.FIDE
        val moveList = MoveList()

        val movesLeft = ArrayList<BoardWithMove>(200)
        startingBoard.getMoves(moveList)
        for (a in 0 until moveList.used) movesLeft.add(BoardWithMove(startingBoard, moveList[a]))

        while (movesLeft.isNotEmpty()) {
            val nextMove = movesLeft.removeLast()
            /*
                            20 | 1
                           400 | 2
                         8,902 | 3
                       197,281 | 4
                     4,865,609 | 5
                   119,060,324 | 6
                 3,195,901,860 | 7
                84,998,978,956 | 8
             2,439,530,234,167 | 9
            */
            // 4 ply: respect check
            // 5 ply: en-passant
            // 7 ply: short castle
            // 8 ply: checkmate
            // 9 ply: long castle
            if (nextMove.board.curPly >= 6) continue

            val newBoard = nextMove.board.withMove(nextMove.move)
            moveList.reset()
            newBoard.getMoves(moveList)
            val moves = moveList.asArray()

            // king is exposed
            if (moves.any { move ->
                if (move.capture == -1) false
                else {
                    val piece = newBoard.state[move.capture]
                    piece != null && piece.isWhite != newBoard.isWhiteToMove && piece.type == PieceType.KING
                }
            }) continue

            // move ends in a valid state (3-fold repetition not checked)
            validMoves[nextMove.board.curPly]++
            for (move in moves) movesLeft.add(BoardWithMove(newBoard, move))
        }
    }
}