import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*

fun main() {
    val window = GameWindow()
    window.pack()
    window.isResizable = false
    window.isVisible = true
    window.defaultCloseOperation = JFrame.EXIT_ON_CLOSE // TODO: confirm exit
}

class GameWindow : JFrame("ChecksMate XVI V0.1") {
    val scoreLabel = JLabel("AI score estimate: ?")
    val board = ChessBoard(scoreLabel)
    init {
        val mainContainer = JPanel()
        mainContainer.layout = BoxLayout(mainContainer, BoxLayout.X_AXIS)

        // left panel: AP stuff

        // main panel: game board + pocket pieces
        val boardContainer = JPanel()
        boardContainer.layout = BoxLayout(boardContainer, BoxLayout.Y_AXIS)
        boardContainer.add(scoreLabel)
        boardContainer.add(board)
        mainContainer.add(boardContainer)

        // right panel: list of checks

        contentPane = mainContainer
    }
}

class ChessBoard(val scoreLabel: JLabel) : JPanel() {
    companion object {
        const val DEBUG_TWOPLAYER = false

        const val CELL_SIZE = 45
        const val BORDER_SIZE = 20

        val CELL_LIGHT = Color(170, 170, 170)
        val CELL_DARK = Color(140, 140, 140)
        val CELL_OUTER_LIGHT = Color(60, 60, 60)
        val CELL_OUTER_DARK = Color(40, 40, 40)
        val CELL_POCKET = Color(150, 150, 100)
        val CELL_HIGHLIGHT = Color(255, 255, 150)
        val CELL_MOVETO = Color(150, 255, 255)
        val CELL_CAPTURE = Color(255, 150, 150)
        val CELL_SPECIALMOVE = Color(100, 255, 100)
        val CELL_LASTMOVE = Color(255, 255, 255)

        // TODO: make the images
        val PIECE_ICONS_WHITE = mapOf(
            "Pawn" to ImageIO.read(File("images/white/FIDE/Pawn.png")),
            "Knight" to ImageIO.read(File("images/white/FIDE/Knight.png")),
            "Bishop" to ImageIO.read(File("images/white/FIDE/Bishop.png")),
            "Rook" to ImageIO.read(File("images/white/FIDE/Rook.png")),
            "Queen" to ImageIO.read(File("images/white/FIDE/Queen.png")),
            "King" to ImageIO.read(File("images/white/FIDE/King.png")),
            // fairy pawns
            "Berolina" to ImageIO.read(File("images/white/PAWN/Berolina.png")),
            "Soldier" to ImageIO.read(File("images/white/white.png")),
            "Sergeant" to ImageIO.read(File("images/white/white.png")),
            // fairy minors
            "Camel" to ImageIO.read(File("images/white/white.png")),
            "Cleric" to ImageIO.read(File("images/white/white.png")),
            "Phoenix" to ImageIO.read(File("images/white/white.png")),
            "Tower" to ImageIO.read(File("images/white/white.png")),
            "Common King" to ImageIO.read(File("images/white/MINOR/King.png")),
            // fairy majors
            "Archbishop" to ImageIO.read(File("images/white/MAJOR/Archbishop.png")),
            "Lion" to ImageIO.read(File("images/white/white.png")),
            // fairy queens
            "Unicorn" to ImageIO.read(File("images/white/QUEEN/Unicorn.png")),
            "War Elephant" to ImageIO.read(File("images/white/QUEEN/Elephant.png")),
            "Chancellor" to ImageIO.read(File("images/white/QUEEN/Chancellor.png")),
            "Dragon" to ImageIO.read(File("images/white/QUEEN/Dragon.png")),
            "Amazon" to ImageIO.read(File("images/white/QUEEN/Amazon.png")),
            // fairy kings
            "General" to ImageIO.read(File("images/white/white.png")),
            "Royal Queen" to ImageIO.read(File("images/white/white.png"))
        )
        val PIECE_ICONS_BLACK = mapOf(
            "Pawn" to ImageIO.read(File("images/black/FIDE/Pawn.png")),
            "Knight" to ImageIO.read(File("images/black/FIDE/Knight.png")),
            "Bishop" to ImageIO.read(File("images/black/FIDE/Bishop.png")),
            "Rook" to ImageIO.read(File("images/black/FIDE/Rook.png")),
            "Queen" to ImageIO.read(File("images/black/FIDE/Queen.png")),
            "King" to ImageIO.read(File("images/black/FIDE/King.png")),
            // fairy pawns
            "Berolina" to ImageIO.read(File("images/black/PAWN/Berolina.png")),
            "Soldier" to ImageIO.read(File("images/black/black.png")),
            "Sergeant" to ImageIO.read(File("images/black/black.png")),
            // fairy minors
            "Camel" to ImageIO.read(File("images/black/black.png")),
            "Cleric" to ImageIO.read(File("images/black/black.png")),
            "Phoenix" to ImageIO.read(File("images/black/black.png")),
            "Tower" to ImageIO.read(File("images/black/black.png")),
            "Common King" to ImageIO.read(File("images/black/MINOR/King.png")),
            // fairy majors
            "Archbishop" to ImageIO.read(File("images/black/MAJOR/Archbishop.png")),
            "Lion" to ImageIO.read(File("images/black/black.png")),
            // fairy queens
            "Unicorn" to ImageIO.read(File("images/black/QUEEN/Unicorn.png")),
            "War Elephant" to ImageIO.read(File("images/black/QUEEN/Elephant.png")),
            "Chancellor" to ImageIO.read(File("images/black/QUEEN/Chancellor.png")),
            "Dragon" to ImageIO.read(File("images/black/QUEEN/Dragon.png")),
            "Amazon" to ImageIO.read(File("images/black/QUEEN/Amazon.png")),
            // fairy kings
            "General" to ImageIO.read(File("images/black/black.png")),
            "Royal Queen" to ImageIO.read(File("images/black/black.png"))
        )

        val CAPTURE = ImageIO.read(File("images/capture.png"))
    }

    private val validMoves = MoveList()

    private var _board = BoardSetups.WIDE
    fun getBoard() = _board
    fun setBoard(value: Board) {
        _board = value
        validMoves.reset()
        _board.getMoves(validMoves)
        size = preferredSize

        if (_board.isWhiteToMove) LocationHelper.examineBoardPreMove(_board)

        repaint()

        if (_board.isGameOver()) {
            println("GAME OVER")
            if (_board.isWhiteWin()) println("White wins!")
            else if (_board.isBlackWin()) println("Black wins!")
            else println("Nobody wins???")
            return
        }

        if (DEBUG_TWOPLAYER) return
        if (!_board.isWhiteToMove) Thread {
            val theAI = AI1()
            val move = theAI.makeMove(_board)
            setBoard(_board.withMove(move.move))
            scoreLabel.text = "AI score estimate: ${-move.score}" // AI is black
        }.start()
    }

    private var selectX = -1
    private var selectY = -1

    init {
        //_board.pockets[0] = Piece(PieceType.PAWN, true, false, "")
        //_board.pockets[1] = Piece(PieceType.BISHOP, true, false, "")
        //_board.pockets[2] = Piece(PieceType.QUEEN, true, false, "")

        //_board.pockets[3] = Piece(PieceType.PAWN, false, false, "")
        //_board.pockets[4] = Piece(PieceType.BISHOP, false, false, "")
        //_board.pockets[5] = Piece(PieceType.QUEEN, false, false, "")

        addMouseListener(object : MouseListener {
            override fun mouseClicked(p0: MouseEvent?) {}
            override fun mouseReleased(p0: MouseEvent?) {}
            override fun mouseEntered(p0: MouseEvent?) {}
            override fun mouseExited(p0: MouseEvent?) {}
            override fun mousePressed(p0: MouseEvent?) {
                if (p0 == null || (!_board.isWhiteToMove && !DEBUG_TWOPLAYER)) return

                var py = p0.point.y - BORDER_SIZE
                if (py < 0 || py >= CELL_SIZE * 16) return
                val cellY = 15 - py / CELL_SIZE

                var px = p0.point.x - BORDER_SIZE
                if (px < 0) return
                if (px >= CELL_SIZE * 16) {
                    px -= BORDER_SIZE
                    if (px < CELL_SIZE * 16 || px >= CELL_SIZE * 17) return
                }
                val cellX = px / CELL_SIZE

                if (cellX == 16) {
                    if (_board.isWhiteToMove && (cellY == 1 || cellY == 2 || cellY == 3)) {
                        if (selectY == -1) {
                            if (_board.pockets[cellY - 1] != null) {
                                selectX = -1
                                selectY = cellY
                            }
                        }
                        else if (selectX == -1 && selectY == cellY) selectY = -1
                    }
                    if (!_board.isWhiteToMove && (cellY == 14 || cellY == 13 || cellY == 12)) {
                        if (selectY == -1) {
                            if (_board.pockets[17 - cellY] != null) {
                                selectX = -1
                                selectY = 18 - cellY
                            }
                        }
                        else if (selectX == -1 && selectY == 18 - cellY) selectY = -1
                    }
                }
                else {
                    val offsetX = (16 - _board.width) / 2
                    val offsetY = (16 - _board.height) / 2
                    val boardX = cellX - offsetX
                    if (boardX < 0 || boardX >= _board.width) return
                    val boardY = cellY - offsetY
                    if (boardY < 0 || boardY >= _board.height) return
                    if (selectY == -1) {
                        val id = boardY * _board.width + boardX
                        if (validMoves.asArray().any { it != null && it.start == id }) {
                            selectX = boardX
                            selectY = boardY
                        }
                    }
                    else if (selectX == boardX && selectY == boardY) selectY = -1
                    else {
                        val movesFromStart =
                            if (selectX == -1) validMoves.asArray().filter { it.start == -selectY }
                            else validMoves.asArray().filter { it.start == selectY * _board.width + selectX }
                        val moveOptions = movesFromStart.filter { it.end == boardY * _board.width + boardX }
                        if (moveOptions.size > 1) {
                            val moveMap = HashMap<Move, String>()
                            moveOptions.forEach { move ->
                                moveMap[move] = if (move.capture == -1) {
                                    if (move.promote == null) "Move"
                                    else "Promote to " + move.promote.niceName
                                }
                                else {
                                    val x = move.capture % _board.width
                                    val y = move.capture / _board.width
                                    val pieceName = _board.state[move.capture]!!.type.niceName

                                    if (move.promote == null) "Capture $pieceName at ${'A'+x}${y+1}"
                                    else "Capture $pieceName at ${'A'+x}${y+1} and promote to " + move.promote.niceName
                                }
                            }
                            val values = moveMap.values.toTypedArray()
                            val selected = JOptionPane.showInputDialog(this@ChessBoard,
                                "Choose move",
                                "Move",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                values,
                                values[0]
                            )
                            if (selected != null) {
                                val chosen = moveMap.entries.first { it.value == selected }.key
                                selectX = -1
                                selectY = -1
                                if (_board.isWhiteToMove) LocationHelper.examineMove(_board, chosen)
                                setBoard(_board.withMove(chosen))
                                if (!_board.isWhiteToMove) LocationHelper.examineBoardPostMove(_board)
                            }
                        }
                        else if (moveOptions.size == 1) {
                            selectX = -1
                            selectY = -1
                            if (_board.isWhiteToMove) LocationHelper.examineMove(_board, moveOptions[0])
                            setBoard(_board.withMove(moveOptions[0]))
                            if (!_board.isWhiteToMove) LocationHelper.examineBoardPostMove(_board)
                        }
                    }
                }

                repaint()
            }
        })

        _board.getMoves(validMoves)
    }

    override fun getPreferredSize() = Dimension(CELL_SIZE * 17 + BORDER_SIZE * 3, CELL_SIZE * 16 + BORDER_SIZE * 2)

    fun drawCell(g: Graphics, x: Int, y: Int, boardX: Int, boardY: Int) {
        val cell = boardY * _board.width + boardX
        if (_board.priorMove.end == cell) {
            g.color = CELL_LASTMOVE
            g.fillRect(x, y, CELL_SIZE, CELL_SIZE)
        }
        if (selectY != -1) {
            if (selectX == boardX && selectY == boardY) {
                g.color = CELL_HIGHLIGHT
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE)
            }
            else {
                val moves = validMoves.asArray().filter { it.end == cell }
                val relevant = if (selectX == -1) moves.filter { it.start == -selectY } else moves.filter { it.start == selectY * _board.width + selectX }
                if (relevant.size > 1) {
                    g.color = CELL_SPECIALMOVE
                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE)
                }
                else if (relevant.size == 1) {
                    if (relevant[0].capture == -1) g.color = CELL_MOVETO
                    else if (relevant[0].capture == -1 && _board.state[relevant[0].end] != null) g.color = CELL_SPECIALMOVE
                    else g.color = CELL_CAPTURE
                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE)
                }
            }
        }
        val piece = _board.state[cell]
        if (piece != null) drawPiece(g, piece, x, y)
    }

    fun drawPiece(g: Graphics, piece: Piece, x: Int, y: Int) {
        val image = (if (piece.isWhite) PIECE_ICONS_WHITE else PIECE_ICONS_BLACK)[piece.type.niceName]
        g.drawImage(image, x, y, CELL_SIZE, CELL_SIZE, null)
        if (LocationHelper.needsToCollectPiece(piece)) g.drawImage(CAPTURE, x, y, CELL_SIZE, CELL_SIZE, null)
    }

    override fun paint(g: Graphics?) {
        if (g == null) return

        val offsetX = (16 - _board.width) / 2
        val offsetY = (16 - _board.height) / 2
        g.color = Color.BLACK
        g.fillRect(0, 0, size.width, size.height)
        for (x in 0 until 17) {
            for (y in 0 until 16) {
                val boardX = x - offsetX
                val boardY = (15 - y) - offsetY
                if (x < 16) {
                    if (boardX < 0 || boardX >= _board.width || boardY < 0 || boardY >= _board.height) {
                        g.color = if ((x + y) % 2 == 0) CELL_OUTER_LIGHT else CELL_OUTER_DARK
                        g.fillRect(BORDER_SIZE + x * CELL_SIZE, BORDER_SIZE + y * CELL_SIZE, CELL_SIZE, CELL_SIZE)
                    }
                    else {
                        g.color = if ((x + y) % 2 == 0) CELL_LIGHT else CELL_DARK
                        g.fillRect(BORDER_SIZE + x * CELL_SIZE, BORDER_SIZE + y * CELL_SIZE, CELL_SIZE, CELL_SIZE)
                        drawCell(g, BORDER_SIZE + x * CELL_SIZE, BORDER_SIZE + y * CELL_SIZE, boardX, boardY)
                    }
                }
                else {
                    if (y == 1 || y == 2 || y == 3) {
                        // pocket black
                        val pocketID = y + 2
                        g.color = CELL_POCKET
                        if (selectX == -1 && selectY == pocketID + 1) g.color = CELL_HIGHLIGHT
                        g.fillRect(BORDER_SIZE * 2 + x * CELL_SIZE, BORDER_SIZE + y * CELL_SIZE, CELL_SIZE, CELL_SIZE)
                        if (_board.pockets[pocketID] != null) drawPiece(g, _board.pockets[pocketID]!!, BORDER_SIZE * 2 + x * CELL_SIZE, BORDER_SIZE + y * CELL_SIZE)
                    }
                    else if (y == 14 || y == 13 || y == 12) {
                        // pocket white
                        val pocketID = 14 - y
                        g.color = CELL_POCKET
                        if (selectX == -1 && selectY == pocketID + 1) g.color = CELL_HIGHLIGHT
                        g.fillRect(BORDER_SIZE * 2 + x * CELL_SIZE, BORDER_SIZE + y * CELL_SIZE, CELL_SIZE, CELL_SIZE)
                        if (_board.pockets[pocketID] != null) drawPiece(g, _board.pockets[pocketID]!!, BORDER_SIZE * 2 + x * CELL_SIZE, BORDER_SIZE + y * CELL_SIZE)
                    }
                }
                if (x == 0) {
                    if (boardY >= 0 && boardY < _board.height) {
                        g.color = Color.WHITE
                        //g.font
                        val chars = "${boardY+1}".toCharArray()
                        g.drawChars(chars, 0, chars.size, 11 - chars.size * 4, BORDER_SIZE + CELL_SIZE / 2 + y * CELL_SIZE + 4)
                    }
                }
                if (y == 0) {
                    if (boardX >= 0 && boardX < _board.width) {
                        g.color = Color.WHITE
                        //g.font
                        g.drawChars("${'A' + boardX}".toCharArray(), 0, 1, x * CELL_SIZE + 39, BORDER_SIZE + CELL_SIZE * 16 + 14)
                    }
                }
            }
        }
    }
}