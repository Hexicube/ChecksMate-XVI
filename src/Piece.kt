enum class PieceClass(val id: Int) {
    PAWN(0),
    MINOR(1),
    MAJOR(2),
    QUEEN(3),
    KING(4)
}
enum class PieceType(val char: Char, val niceName: String, val cost: Int, val type: PieceClass) {
    // FIDE pieces
    PAWN('P', "Pawn", 1, PieceClass.PAWN),
    KNIGHT('N', "Knight", 3, PieceClass.MINOR),
    BISHOP('B', "Bishop", 3, PieceClass.MINOR),
    ROOK('R', "Rook", 5, PieceClass.MAJOR),
    QUEEN('Q', "Queen", 8, PieceClass.QUEEN),
    KING('K', "King", 5, PieceClass.KING),

    // fairy pawns
    BEROLINA('?', "Berolina", 1, PieceClass.PAWN), // move diagonal, capture forward
    SOLDIER('?', "Soldier", 1, PieceClass.PAWN), // move and capture forward
    SERGEANT('?', "Sergeant", 2, PieceClass.PAWN), // move and capture forward and diagonal

    // fairy minors
    CAMEL('M', "Camel", 3, PieceClass.MINOR), // 3,1 jumps
    CLERIC('I', "Cleric", 4, PieceClass.MINOR), // Bishop + 2,0 jumps
    PHOENIX('X', "Phoenix", 3, PieceClass.MINOR), // 1,0 jumps + 2,2 jumps
    TOWER('T', "Tower", 2, PieceClass.MINOR), // 1,0 jumps + 2,0 jumps
    COMMONER('O', "Common King", 4, PieceClass.MINOR), // king moves

    // fairy majors
    ARCHBISHOP('H', "Archbishop", 6, PieceClass.MAJOR), // Knight + Bishop
    LION('L', "Lion", 5, PieceClass.MAJOR), // 1,1 jumps + 2,0 jumps + 3,0 jumps

    // fairy queens
    UNICORN('U', "Unicorn", 8, PieceClass.QUEEN), // 1,2 jumps + 1,3 jumps + 2,3 jumps
    ELEPHANT('E', "War Elephant", 9, PieceClass.QUEEN), // 1,1 jumps + 2,0 jumps + 2,2 jumps
    CHANCELLOR('C', "Chancellor", 9, PieceClass.QUEEN), // Knight + Rook
    DRAGON('D', "Dragon", 10, PieceClass.QUEEN), // Rook + 3,2 jumps + 3,3 jumps
    AMAZON('A', "Amazon", 10, PieceClass.QUEEN), // Queen + Knight

    // fairy kings
    GENERAL('G', "General", 10, PieceClass.KING), // King + Knight
    CROWNQUEEN('Y', "Royal Queen", 12, PieceClass.KING); // Queen

    companion object {
        val FIDE_PROMOTE = setOf(
            KNIGHT, BISHOP, ROOK, QUEEN
        )

        val ALL_PROMOTE = setOf(
            KNIGHT, BISHOP, ROOK, QUEEN,
            CAMEL, CLERIC, PHOENIX, TOWER, COMMONER,
            ARCHBISHOP, UNICORN, LION,
            ELEPHANT, CHANCELLOR, DRAGON, AMAZON
        )
        val ALL_PIECES = setOf(
            PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING,
            BEROLINA, SOLDIER, SERGEANT,
            CAMEL, CLERIC, PHOENIX, TOWER, COMMONER,
            ARCHBISHOP, UNICORN, LION,
            ELEPHANT, CHANCELLOR, DRAGON, AMAZON,
            GENERAL, CROWNQUEEN
        )
    }
}

data class Piece(val type: PieceType, val isWhite: Boolean, val hasMoved: Boolean, val identifier: String)
data class PieceWithPos(val piece: Piece, val x: Int, val y: Int)