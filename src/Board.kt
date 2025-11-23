import kotlin.math.abs

data class Move(val start: Int, val end: Int, val capture: Int = -1, val promote: PieceType? = null) {
    companion object {
        val NULL = Move(-1, -1)
    }
}

class Board(val width: Int, val height: Int, val state: Array<Piece?>, val pockets: Array<Piece?>, val isWhiteToMove: Boolean, val curPly: Int, val priorMove: Move?, val promoteOptions: Set<PieceType>) {
    val numPieceTypes = Array(10) { 0 }

    private val pawnSkipX: Int
    private val pawnSkipY: Int

    init {
        var piece: Piece?
        for (a in 0 until state.size) {
            piece = state[a] ?: continue
            val col = if (piece.isWhite) 0 else 1
            numPieceTypes[piece.type.type.id * 2 + col]++
        }

        // check if the last move is eligible for en-passant capture
        // NOTE: generalised to work with any pawn that moves two spaces
        if (priorMove != null && priorMove.start >= 0) {
            // verify the piece is an opposing basic pawn
            piece = state[priorMove.end]
            // NOTE: can be null from castling
            if (piece != null && piece.isWhite != isWhiteToMove && piece.type.type == PieceClass.PAWN) {
                // verify the piece skipped over one space
                val startX = priorMove.start % width
                val startY = priorMove.start / width
                val endX = priorMove.end % width
                val endY = priorMove.end / width
                if (abs(endY - startY) == 2 && (endX - startX) % 2 == 0) {
                    pawnSkipX = (startX + endX) / 2
                    pawnSkipY = (startY + endY) / 2
                }
                else {
                    pawnSkipX = -1
                    pawnSkipY = -1
                }
            }
            else {
                pawnSkipX = -1
                pawnSkipY = -1
            }
        }
        else {
            pawnSkipX = -1
            pawnSkipY = -1
        }
    }

    fun isWhiteWin() = numPieceTypes[8] > 0 && numPieceTypes[9] == 0
    fun isBlackWin() = numPieceTypes[8] == 0 && numPieceTypes[9] > 0

    fun isGameOver() = numPieceTypes[8] == 0 || numPieceTypes[9] == 0
    // NOTE: should not be possible for a game to end with neither player winning

    /*
    Move specification notes:
    - Capture cell indicates what piece to remove
      - For en-passant or capture-without-moving pieces this will not match the end cell
    - Promotions apply to any pawn arriving at the back rank (generate a move for all options)
    - Castling is done by moving any king onto any major
    */
    fun getMoves(outList: MoveList) {
        // TODO: make this faster
        // NOTE: leaving/putting king in check is allowed, this prevents stalemate as an end condition
        // NOTE: castling through an attack is NOT allowed

        if (isGameOver()) return

        var move: Move?
        var hit: Piece?
        for (x in 0 until width) {
            for (y in 0 until height) {
                val piece = state[y * width + x]
                if (piece == null) {
                    // pocket pieces
                    if (isWhiteToMove) {
                        if (y < height / 2) {
                            if (pockets[0] != null) outList += Move(-1, y * width + x)
                            if (pockets[1] != null) outList += Move(-2, y * width + x)
                            if (pockets[2] != null) outList += Move(-3, y * width + x)
                        }
                    }
                    else {
                        if (y >= height / 2) {
                            if (pockets[3] != null) outList += Move(-4, y * width + x)
                            if (pockets[4] != null) outList += Move(-5, y * width + x)
                            if (pockets[5] != null) outList += Move(-6, y * width + x)
                        }
                    }
                    continue
                }

                if (piece.isWhite != isWhiteToMove) continue
                when (piece.type) {
                    PieceType.PAWN -> {
                        val endY: Int
                        val doubleY: Int
                        var end: Int
                        var cap: Int
                        if (isWhiteToMove) {
                            if ((y + 1) >= height) continue
                            endY = y + 1
                            doubleY = y + 2
                        }
                        else {
                            if (y <= 0) continue
                            endY = y - 1
                            doubleY = y - 2
                        }
                        if (x > 0) {
                            end = endY * width + x - 1
                            cap = end
                            hit = state[cap]
                            if (hit == null) {
                                // en-passant
                                if (pawnSkipX == x - 1 && pawnSkipY == endY) {
                                    hit = state[priorMove!!.end]
                                    cap = priorMove.end
                                }
                            }
                            if (hit != null && hit.isWhite != isWhiteToMove) {
                                if (endY == 0 || endY == height - 1) {
                                    for (promote in promoteOptions) {
                                        outList += Move(y * width + x, end, cap, promote = promote)
                                    }
                                }
                                else outList += Move(y * width + x, end, cap)
                            }
                        }
                        if (x + 1 < width) {
                            end = endY * width + x + 1
                            cap = end
                            hit = state[cap]
                            if (hit == null) {
                                // en-passant
                                if (pawnSkipX == x + 1 && pawnSkipY == endY) {
                                    hit = state[priorMove!!.end]
                                    cap = priorMove.end
                                }
                            }
                            if (hit != null && hit.isWhite != isWhiteToMove) {
                                if (endY == 0 || endY == height - 1) {
                                    for (promote in promoteOptions) {
                                        outList += Move(y * width + x, end, cap, promote = promote)
                                    }
                                }
                                else outList += Move(y * width + x, end, cap)
                            }
                        }
                        end = endY * width + x
                        if (state[end] == null) {
                            if (endY == 0 || endY == height - 1) {
                                for (promote in promoteOptions) {
                                    outList += Move(y * width + x, end, promote = promote)
                                }
                            }
                            else outList += Move(y * width + x, end)
                            if (!piece.hasMoved && doubleY >= 0 && doubleY < height) {
                                end = doubleY * width + x
                                if (state[end] == null) {
                                    if (doubleY == 0 || doubleY == height - 1) {
                                        for (promote in promoteOptions) {
                                            outList += Move(y * width + x, end, promote = promote)
                                        }
                                    }
                                    else outList += Move(y * width + x, end)
                                }
                            }
                        }
                    }
                    PieceType.KNIGHT -> {
                        getJumpMoves(outList, x, y, 1, 2)
                    }
                    PieceType.BISHOP -> {
                        getBishopMoves(outList, x, y, isWhiteToMove)
                    }
                    PieceType.ROOK -> {
                        getRookMoves(outList, x, y, isWhiteToMove)
                    }
                    PieceType.QUEEN -> {
                        getQueenMoves(outList, x, y, isWhiteToMove)
                    }
                    PieceType.KING -> {
                        getKingMoves(outList, x, y)
                    }
                    // fairy pawns
                    // NOTE: fairy pawns can only MOVE two spaces on first move, not capture
                    PieceType.BEROLINA -> {
                        // move diagonal, capture forward
                        val endY: Int
                        val doubleY: Int
                        var end: Int
                        var cap: Int
                        if (isWhiteToMove) {
                            if ((y + 1) >= height) continue
                            endY = y + 1
                            doubleY = y + 2
                        }
                        else {
                            if (y <= 0) continue
                            endY = y - 1
                            doubleY = y - 2
                        }
                        end = endY * width + x
                        cap = end
                        hit = state[cap]
                        if (hit == null) {
                            // en-passant
                            if (pawnSkipX == x && pawnSkipY == endY) {
                                hit = state[priorMove!!.end]
                                cap = priorMove.end
                            }
                        }
                        if (hit != null && hit.isWhite != isWhiteToMove) {
                            if (endY == 0 || endY == height - 1) {
                                for (promote in promoteOptions) {
                                    outList += Move(y * width + x, end, cap, promote = promote)
                                }
                            }
                            else outList += Move(y * width + x, end, cap)
                        }
                        if (x > 0) {
                            end = endY * width + x - 1
                            if (state[end] == null) {
                                if (endY == 0 || endY == height - 1) {
                                    for (promote in promoteOptions) {
                                        outList += Move(y * width + x, end, promote = promote)
                                    }
                                }
                                else outList += Move(y * width + x, end)
                                if (!piece.hasMoved && x > 1 && doubleY >= 0 && doubleY < height) {
                                    end = doubleY * width + x - 2
                                    if (state[end] == null) {
                                        if (doubleY == 0 || doubleY == height - 1) {
                                            for (promote in promoteOptions) {
                                                outList += Move(y * width + x, end, promote = promote)
                                            }
                                        }
                                        else outList += Move(y * width + x, end)
                                    }
                                }
                            }
                        }
                        if (x + 1 < width) {
                            end = endY * width + x + 1
                            if (state[end] == null) {
                                if (endY == 0 || endY == height - 1) {
                                    for (promote in promoteOptions) {
                                        outList += Move(y * width + x, end, promote = promote)
                                    }
                                }
                                else outList += Move(y * width + x, end)
                                if (!piece.hasMoved && x + 2 < width && doubleY >= 0 && doubleY < height) {
                                    end = doubleY * width + x + 2
                                    if (state[end] == null) {
                                        if (doubleY == 0 || doubleY == height - 1) {
                                            for (promote in promoteOptions) {
                                                outList += Move(y * width + x, end, promote = promote)
                                            }
                                        }
                                        else outList += Move(y * width + x, end)
                                    }
                                }
                            }
                        }
                    }
                    // NOTE: these pawns need to generate en-passant moves AND moves to just move there
                    PieceType.SOLDIER -> {
                        // forward only
                        val endY: Int
                        val doubleY: Int
                        var end: Int
                        var cap: Int
                        if (isWhiteToMove) {
                            if ((y + 1) >= height) continue
                            endY = y + 1
                            doubleY = y + 2
                        }
                        else {
                            if (y <= 0) continue
                            endY = y - 1
                            doubleY = y - 2
                        }
                        end = endY * width + x
                        cap = end
                        hit = state[cap]
                        if (hit == null) {
                            // make a normal move before en-passant
                            if (endY == 0 || endY == height - 1) {
                                for (promote in promoteOptions) {
                                    outList += Move(y * width + x, end, promote = promote)
                                }
                            }
                            else outList += Move(y * width + x, end)
                            if (!piece.hasMoved && doubleY >= 0 && doubleY < height) {
                                end = doubleY * width + x
                                if (state[end] == null) {
                                    if (doubleY == 0 || doubleY == height - 1) {
                                        for (promote in promoteOptions) {
                                            outList += Move(y * width + x, end, promote = promote)
                                        }
                                    }
                                    else outList += Move(y * width + x, end)
                                }
                            }
                            // en-passant
                            end = endY * width + x
                            if (pawnSkipX == x && pawnSkipY == endY) {
                                hit = state[priorMove!!.end]
                                cap = priorMove.end
                            }
                        }
                        if (hit != null && hit.isWhite != isWhiteToMove) {
                            if (endY == 0 || endY == height - 1) {
                                for (promote in promoteOptions) {
                                    outList += Move(y * width + x, end, cap, promote = promote)
                                }
                            }
                            else outList += Move(y * width + x, end, cap)
                        }
                    }
                    PieceType.SERGEANT -> {
                        // forward and diagonal
                        TODO()
                    }
                    // fairy minors
                    PieceType.CAMEL -> {
                        getJumpMoves(outList, x, y, 1, 3)
                    }
                    PieceType.CLERIC -> {
                        getBishopMoves(outList, x, y, isWhiteToMove)
                        getOrthJumpMoves(outList, x, y, 2)
                    }
                    PieceType.PHOENIX -> {
                        getOrthJumpMoves(outList, x, y, 1)
                        getDiagJumpMoves(outList, x, y, 2)
                    }
                    PieceType.TOWER -> {
                        getOrthJumpMoves(outList, x, y, 1)
                        getOrthJumpMoves(outList, x, y, 2)
                    }
                    PieceType.COMMONER -> {
                        getKingMoves(outList, x, y)
                    }
                    // fairy majors
                    PieceType.ARCHBISHOP -> {
                        getBishopMoves(outList, x, y, isWhiteToMove)
                        getJumpMoves(outList, x, y, 1, 2)
                    }
                    PieceType.UNICORN -> {
                        getJumpMoves(outList, x, y, 1, 2)
                        getJumpMoves(outList, x, y, 1, 3)
                        getJumpMoves(outList, x, y, 2, 3)
                    }
                    PieceType.LION -> {
                        getDiagJumpMoves(outList, x, y, 1)
                        getOrthJumpMoves(outList, x, y, 2)
                        getOrthJumpMoves(outList, x, y, 3)
                    }
                    // fairy queens
                    PieceType.ELEPHANT -> {
                        getDiagJumpMoves(outList, x, y, 1)
                        getOrthJumpMoves(outList, x, y, 2)
                        getDiagJumpMoves(outList, x, y, 2)
                    }
                    PieceType.CHANCELLOR -> {
                        getRookMoves(outList, x, y, isWhiteToMove)
                        getJumpMoves(outList, x, y, 1, 2)
                    }
                    PieceType.DRAGON -> {
                        getRookMoves(outList, x, y, isWhiteToMove)
                        getJumpMoves(outList, x, y, 2, 3)
                        getDiagJumpMoves(outList, x, y, 3)
                    }
                    PieceType.AMAZON -> {
                        getQueenMoves(outList, x, y, isWhiteToMove)
                        getJumpMoves(outList, x, y, 1, 2)
                    }
                    // fairy kings
                    PieceType.GENERAL -> {
                        getKingMoves(outList, x, y)
                        getJumpMoves(outList, x, y, 1, 2)
                    }
                    PieceType.CROWNQUEEN -> {
                        getQueenMoves(outList, x, y, isWhiteToMove)
                    }
                    else -> throw NotImplementedError()
                }
                // castling (handled separately to cover all king types)
                if (!piece.hasMoved && piece.type.type == PieceClass.KING) {
                    for (x2 in x-1 downTo 0) {
                        val hit = state[y * width + x2] ?: continue
                        if (!hit.hasMoved && hit.isWhite == isWhiteToMove && hit.type.type == PieceClass.MAJOR) {
                            move = getCastlingMove(x, y, x - 2, x2)
                            if (move != null) outList += move
                        }
                        break
                    }
                    for (x2 in x+1 until width) {
                        val hit = state[y * width + x2] ?: continue
                        if (!hit.hasMoved && hit.isWhite == isWhiteToMove && hit.type.type == PieceClass.MAJOR) {
                            move = getCastlingMove(x, y, x + 2, x2)
                            if (move != null) outList += move
                        }
                        break
                    }
                }
            }
        }
    }
    fun isEnemyJumper(piece: Piece, a: Int, b: Int, white: Boolean): Boolean {
        if (a > b) return isEnemyJumper(piece, b, a, white)

        // NOTE: no need to check directly adjacent jumps
        return when (piece.type) {
            PieceType.CLERIC, PieceType.TOWER -> (a == 0 && b == 2)
            PieceType.KNIGHT, PieceType.CHANCELLOR, PieceType.AMAZON, PieceType.GENERAL -> (a == 1 && b == 2)
            PieceType.PHOENIX -> (a == 2 && b == 2)
            PieceType.ELEPHANT -> (a == 0 && b == 2) || (a == 2 && b == 2)
            PieceType.LION -> (a == 0 && b == 2) || (a == 0 && b == 3)
            PieceType.CAMEL -> (a == 1 && b == 3)
            PieceType.UNICORN -> (a == 1 && b == 2) || (a == 1 && b == 3) || (a == 2 && b == 3)
            PieceType.DRAGON -> (a == 2 && b == 3) || (a == 3 && b == 3)
            else -> false
        }
    }
    fun isSquareAttacked(x: Int, y: Int, white: Boolean = isWhiteToMove): Boolean {
        var piece: Piece?
        // pawns
        val dir = if (white) 1 else -1
        val oneStep = y + dir
        if (oneStep in 0 until height) {
            if (x - 1 >= 0) {
                piece = state[oneStep * width + (x - 1)]
                if (piece != null && piece.isWhite != white) {
                    if (piece.type == PieceType.PAWN) return true
                    if (piece.type == PieceType.SERGEANT) return true
                }
            }
            if (x + 1 < width) {
                piece = state[oneStep * width + (x + 1)]
                if (piece != null && piece.isWhite != white) {
                    if (piece.type == PieceType.PAWN) return true
                    if (piece.type == PieceType.SERGEANT) return true
                }
            }
            piece = state[oneStep * width + x]
            if (piece != null && piece.isWhite != white) {
                if (piece.type == PieceType.BEROLINA) return true
                if (piece.type == PieceType.SOLDIER) return true
                if (piece.type == PieceType.SERGEANT) return true
            }
        }
        // sliding and adjacent
        for (dx in -1 .. 1) {
            for (dy in -1 .. 1) {
                if (dx == 0 && dy == 0) continue
                var px = x
                var py = y
                var firstStep = true
                while (true) {
                    px += dx
                    if (px < 0 || px >= width) break
                    py += dy
                    if (py < 0 || py >= height) break
                    piece = state[py * width + px]
                    if (piece == null) {
                        firstStep = false
                        continue
                    }
                    if (piece.isWhite != white) {
                        if (firstStep) {
                            if (piece.type == PieceType.KING) return true
                            if (piece.type == PieceType.COMMONER) return true
                            if (piece.type == PieceType.GENERAL) return true
                            if (dx == 0 || dy == 0) {
                                if (piece.type == PieceType.PHOENIX) return true
                                if (piece.type == PieceType.TOWER) return true
                            }
                            else {
                                if (piece.type == PieceType.LION) return true
                                if (piece.type == PieceType.ELEPHANT) return true
                            }
                        }
                        if (piece.type == PieceType.QUEEN) return true
                        if (piece.type == PieceType.AMAZON) return true
                        if (piece.type == PieceType.CROWNQUEEN) return true
                        if (dx == 0 || dy == 0) {
                            if (piece.type == PieceType.ROOK) return true
                            if (piece.type == PieceType.CHANCELLOR) return true
                            if (piece.type == PieceType.DRAGON) return true
                        }
                        else {
                            if (piece.type == PieceType.BISHOP) return true
                            if (piece.type == PieceType.CLERIC) return true
                            if (piece.type == PieceType.ARCHBISHOP) return true
                        }
                    }
                    break
                }
            }
        }
        // knights
        for (dx in -3 .. 3) {
            val px = x + dx
            if (px < 0) continue
            if (px >= width) break
            for (dy in -3 .. 3) {
                val py = y + dy
                if (py < 0) continue
                if (py >= height) break
                piece = state[py * width + px]
                if (piece == null || piece.isWhite == white) continue
                if (isEnemyJumper(piece, abs(dx), abs(dy), white)) return true
            }
        }
        return false
    }
    fun getCastlingMove(startX: Int, startY: Int, endX: Int, rookX: Int): Move? {
        // verifies the king is moving through safe squares
        if (endX > startX) {
            for (x2 in startX .. endX) {
                if (isSquareAttacked(x2, startY)) return null
            }
        }
        else {
            for (x2 in startX downTo endX) {
                if (isSquareAttacked(x2, startY)) return null
            }
        }
        return Move(startY * width + startX, startY * width + rookX)
    }
    fun getSingleMove(start: Int, end: Int, canMove: Boolean = true, canCapture: Boolean = true): Move? {
        val endPiece = state[end] ?: return if (canMove) Move(start, end) else null
        if (endPiece.isWhite == isWhiteToMove) return null
        return if (canCapture) Move(start, end, end) else null
    }
    fun getKingMoves(list: MoveList, startX: Int, startY: Int) {
        var move: Move?
        if (startX > 0) {
            if (startY > 0) {
                move = getSingleMove(startY * width + startX, (startY - 1) * width + startX - 1)
                if (move != null) list += move
            }
            move = getSingleMove(startY * width + startX, startY * width + startX - 1)
            if (move != null) list += move
            if ((startY + 1) < height) {
                move = getSingleMove(startY * width + startX, (startY + 1) * width + startX - 1)
                if (move != null) list += move
            }
        }
        if (startY > 0) {
            move = getSingleMove(startY * width + startX, (startY - 1) * width + startX)
            if (move != null) list += move
        }
        if ((startY + 1) < height) {
            move = getSingleMove(startY * width + startX, (startY + 1) * width + startX)
            if (move != null) list += move
        }
        if ((startX + 1) < width) {
            if (startY > 0) {
                move = getSingleMove(startY * width + startX, (startY - 1) * width + startX + 1)
                if (move != null) list += move
            }
            move = getSingleMove(startY * width + startX, startY * width + startX + 1)
            if (move != null) list += move
            if ((startY + 1) < height) {
                move = getSingleMove(startY * width + startX, (startY + 1) * width + startX + 1)
                if (move != null) list += move
            }
        }
    }
    fun getOrthJumpMoves(list: MoveList, startX: Int, startY: Int, a: Int) {
        var move: Move?
        if (startX - a >= 0) {
            move = getSingleMove(startY * width + startX, startY * width + startX - a)
            if (move != null) list += move
        }
        if (startX + a < width) {
            move = getSingleMove(startY * width + startX, startY * width + startX + a)
            if (move != null) list += move
        }
        if (startY - a >= 0) {
            move = getSingleMove(startY * width + startX, (startY - a) * width + startX)
            if (move != null) list += move
        }
        if (startY + a < height) {
            move = getSingleMove(startY * width + startX, (startY + a) * width + startX)
            if (move != null) list += move
        }
    }
    fun getDiagJumpMoves(list: MoveList, startX: Int, startY: Int, a: Int) {
        var move: Move?
        if (startX - a >= 0) {
            if (startY - a >= 0) {
                move = getSingleMove(startY * width + startX, (startY - a) * width + startX - a)
                if (move != null) list += move
            }
            if (startY + a < height) {
                move = getSingleMove(startY * width + startX, (startY + a) * width + startX - a)
                if (move != null) list += move
            }
        }
        if (startX + a < width) {
            if (startY - a >= 0) {
                move = getSingleMove(startY * width + startX, (startY - a) * width + startX + a)
                if (move != null) list += move
            }
            if (startY + a < height) {
                move = getSingleMove(startY * width + startX, (startY + a) * width + startX + a)
                if (move != null) list += move
            }
        }
    }
    fun getJumpMoves(list: MoveList, startX: Int, startY: Int, a: Int, b: Int) {
        // a<b
        var hit: Piece?
        val start = startY * width + startX
        var end: Int
        if (startX + a < width) {
            if (startY + b < height) {
                end = (startY + b) * width + startX + a
                hit = state[end]
                if (hit == null) list += Move(start, end)
                else if(hit.isWhite != isWhiteToMove) list += Move(start, end, end)
            }
            if (startY - b >= 0) {
                end = (startY - b) * width + startX + a
                hit = state[end]
                if (hit == null) list += Move(start, end)
                else if(hit.isWhite != isWhiteToMove) list += Move(start, end, end)
            }
            if (startX + b < width) {
                if (startY + a < height) {
                    end = (startY + a) * width + startX + b
                    hit = state[end]
                    if (hit == null) list += Move(start, end)
                    else if(hit.isWhite != isWhiteToMove) list += Move(start, end, end)
                }
                if (startY - a >= 0) {
                    end = (startY - a) * width + startX + b
                    hit = state[end]
                    if (hit == null) list += Move(start, end)
                    else if(hit.isWhite != isWhiteToMove) list += Move(start, end, end)
                }
            }
        }
        if (startX - a >= 0) {
            if (startY + b < height) {
                end = (startY + b) * width + startX - a
                hit = state[end]
                if (hit == null) list += Move(start, end)
                else if(hit.isWhite != isWhiteToMove) list += Move(start, end, end)
            }
            if (startY - b >= 0) {
                end = (startY - b) * width + startX - a
                hit = state[end]
                if (hit == null) list += Move(start, end)
                else if(hit.isWhite != isWhiteToMove) list += Move(start, end, end)
            }
            if (startX - b >= 0) {
                if (startY + a < height) {
                    end = (startY + a) * width + startX - b
                    hit = state[end]
                    if (hit == null) list += Move(start, end)
                    else if(hit.isWhite != isWhiteToMove) list += Move(start, end, end)
                }
                if (startY - a >= 0) {
                    end = (startY - a) * width + startX - b
                    hit = state[end]
                    if (hit == null) list += Move(start, end)
                    else if(hit.isWhite != isWhiteToMove) list += Move(start, end, end)
                }
            }
        }
    }
    // NOTE: lookup tables are worse than just doing the loop
    fun getSlidingMoves(list: MoveList, startX: Int, startY: Int, dx: Int, dy: Int, white: Boolean) {
        var x = startX
        var y = startY
        while (true) {
            x += dx
            if (x < 0 || x >= width) break
            y += dy
            if (y < 0 || y >= height) break
            val hit = state[y * width + x]
            if (hit != null) {
                if (hit.isWhite != white) list += Move(startY * width + startX, y * width + x, y * width + x)
                break
            }
            list += Move(startY * width + startX, y * width + x)
        }
    }
    fun getRookMoves(list: MoveList, startX: Int, startY: Int, white: Boolean) {
        var piece: Piece?
        val start = startY * width + startX
        var end: Int
        for (x in startX - 1 downTo 0) {
            end = startY * width + x
            piece = state[end]
            if (piece == null) {
                list += Move(start, end)
                continue
            }
            if (piece.isWhite != white) list += Move(start, end, end)
            break
        }
        for (x in startX + 1 until width) {
            end = startY * width + x
            piece = state[end]
            if (piece == null) {
                list += Move(start, end)
                continue
            }
            if (piece.isWhite != white) list += Move(start, end, end)
            break
        }
        for (y in startY - 1 downTo 0) {
            end = y * width + startX
            piece = state[end]
            if (piece == null) {
                list += Move(start, end)
                continue
            }
            if (piece.isWhite != white) list += Move(start, end, end)
            break
        }
        for (y in startY + 1 until height) {
            end = y * width + startX
            piece = state[end]
            if (piece == null) {
                list += Move(start, end)
                continue
            }
            if (piece.isWhite != white) list += Move(start, end, end)
            break
        }
    }
    fun getBishopMoves(list: MoveList, startX: Int, startY: Int, white: Boolean) {
        var piece: Piece?
        val start = startY * width + startX
        var end: Int
        var y = startY
        for (x in startX - 1 downTo 0) {
            if (--y < 0) break
            end = y * width + x
            piece = state[end]
            if (piece == null) {
                list += Move(start, end)
                continue
            }
            if (piece.isWhite != white) list += Move(start, end, end)
            break
        }
        y = startY
        for (x in startX - 1 downTo 0) {
            if (++y >= height) break
            end = y * width + x
            piece = state[end]
            if (piece == null) {
                list += Move(start, end)
                continue
            }
            if (piece.isWhite != white) list += Move(start, end, end)
            break
        }
        y = startY
        for (x in startX + 1 until width) {
            if (--y < 0) break
            end = y * width + x
            piece = state[end]
            if (piece == null) {
                list += Move(start, end)
                continue
            }
            if (piece.isWhite != white) list += Move(start, end, end)
            break
        }
        y = startY
        for (x in startX + 1 until width) {
            if (++y >= height) break
            end = y * width + x
            piece = state[end]
            if (piece == null) {
                list += Move(start, end)
                continue
            }
            if (piece.isWhite != white) list += Move(start, end, end)
            break
        }
    }
    fun getQueenMoves(list: MoveList, startX: Int, startY: Int, white: Boolean) {
        getRookMoves(list, startX, startY, white)
        getBishopMoves(list, startX, startY, white)
    }

    fun printStateAndError(move: Move, err: String): Nothing {
        println(priorMove)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val piece = state[y * width + x]
                if (piece == null) print('-')
                else {
                    var char = piece.type.char
                    if (!piece.isWhite) char = char.lowercaseChar()
                    print(char)
                }
            }
            println()
        }
        println(move)

        throw IllegalStateException(err)
    }

    // NOTE: this does not validate piece mobility rules, only core logic
    fun withMove(move: Move): Board {
        if (move.end == -1) printStateAndError(move, "Null move")

        val startPiece = (if (move.start <= -1 && move.start >= -6) pockets[-(move.start + 1)] else state[move.start]) ?: printStateAndError(move, "No piece to move")
        if (startPiece.isWhite != isWhiteToMove) printStateAndError(move, "Tried to move piece of wrong colour")

        val newState = state.clone()
        val newPockets = pockets.clone()

        var wasPocket = false
        if (move.capture != -1) {
            val capturePiece = newState[move.capture] ?: printStateAndError(move, "No piece to capture")
            if (capturePiece.isWhite == startPiece.isWhite) printStateAndError(move, "Capturing same colour")
            newState[move.capture] = null
        }

        if (move.start < 0) {
            newPockets[-(move.start + 1)] = null
            wasPocket = true
        }
        else newState[move.start] = null

        val endPiece = newState[move.end]
        if (endPiece != null) {
            if (startPiece.type.type == PieceClass.KING && endPiece.type.type == PieceClass.MAJOR && move.start >= 0) {
                if (move.promote != null) printStateAndError(move, "Castling and promoting in same move")
                if (move.capture != -1) printStateAndError(move, "Castling and capturing in same move")
                val startY = move.start / width
                val endY = move.end / width
                if (startY != endY) printStateAndError(move, "Castling move start and end have different Y position")
                val startX = move.start % width
                val endX = move.end % width
                newState[move.end] = null
                val kingEnd: Int
                val majorEnd: Int
                if (startX > endX) {
                    kingEnd = move.start - 2
                    majorEnd = move.start - 1
                }
                else {
                    kingEnd = move.start + 2
                    majorEnd = move.start + 1
                }
                if (newState[kingEnd] != null) printStateAndError(move, "Castling king end occupied")
                newState[kingEnd] = Piece(startPiece.type, startPiece.isWhite, true, startPiece.identifier)
                if (newState[majorEnd] != null) printStateAndError(move, "Castling major end occupied")
                newState[majorEnd] = Piece(endPiece.type, endPiece.isWhite, true, endPiece.identifier)
            }
            else printStateAndError(move, "End cell occupied")
        }
        else newState[move.end] = Piece(move.promote ?: startPiece.type, startPiece.isWhite, !wasPocket, startPiece.identifier)

        return Board(width, height, newState, newPockets, !isWhiteToMove, curPly + 1, move, promoteOptions)
    }

    fun nullMove(): Board {
        return Board(width, height, state.clone(), pockets.clone(), !isWhiteToMove, curPly + 1, Move.NULL, promoteOptions)
    }

    companion object {
        fun blankBoard(width: Int, height: Int, promoteOptions: Set<PieceType>): Board {
            val state = Array<Piece?>(width * height) { null }
            return Board(width, height, state, Array(6) { null }, true, 0, null, promoteOptions)
        }

        fun boardWithPieces(width: Int, height: Int, promoteOptions: Set<PieceType>, vararg pieces: PieceWithPos): Board {
            val state = Array<Piece?>(width * height) { null }
            for (piece in pieces) {
                if (piece.x < 0 || piece.y < 0 || piece.x >= width || piece.y >= height)
                    throw IllegalArgumentException("Piece not on board")
                if (state[piece.y * width + piece.x] != null)
                    throw IllegalArgumentException("Cell already occupied")
                state[piece.y * width + piece.x] = piece.piece
            }
            return Board(width, height, state, Array(6) { null }, true, 0, null, promoteOptions)
        }

        fun boardFromString(str: String, promoteOptions: Set<PieceType>): Board {
            val rows = str.split('/')
            val height = rows.size
            val width = rows[0].length
            val state = Array<Piece?>(width * height) { null }
            for (x in 0 until width) {
                for (y in 0 until height) {
                    var char = rows[y][x]
                    if (char == '-') continue
                    val isWhite: Boolean
                    if (char.isUpperCase()) isWhite = true
                    else {
                        isWhite = false
                        char = char.uppercaseChar()
                    }
                    val piece = PieceType.ALL_PIECES.firstOrNull { it.char == char }
                    if (piece == null) throw IllegalArgumentException("Unknown piece character $char")
                    state[y * width + x] = Piece(piece, isWhite, false, "") // TODO: identifiers
                }
            }
            return Board(width, height, state, Array(6) { null }, true, 0, null, promoteOptions)
        }
    }
}