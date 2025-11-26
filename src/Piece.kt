enum class PieceClass(val id: Int) {
    PAWN(0),
    MINOR(1),
    MAJOR(2),
    QUEEN(3),
    KING(4)
}
enum class PieceType(val ID: UShort, val char: Char, val niceName: String, val cost: Int, val type: PieceClass) {
    // FIDE pieces
    PAWN(1U, 'P', "Pawn", 1, PieceClass.PAWN),
    KNIGHT(2U, 'N', "Knight", 3, PieceClass.MINOR),
    BISHOP(3U, 'B', "Bishop", 3, PieceClass.MINOR),
    ROOK(4U, 'R', "Rook", 5, PieceClass.MAJOR),
    QUEEN(5U, 'Q', "Queen", 8, PieceClass.QUEEN),
    KING(6U, 'K', "King", 5, PieceClass.KING),

    // fairy pawns
    BEROLINA(7U, '?', "Berolina", 1, PieceClass.PAWN), // move diagonal, capture forward
    SOLDIER(8U, '?', "Soldier", 1, PieceClass.PAWN), // move and capture forward
    SERGEANT(9U, '?', "Sergeant", 2, PieceClass.PAWN), // move and capture forward and diagonal

    // fairy minors
    CAMEL(10U, 'M', "Camel", 3, PieceClass.MINOR), // 3,1 jumps
    CLERIC(11U, 'I', "Cleric", 4, PieceClass.MINOR), // Bishop + 2,0 jumps
    PHOENIX(12U, 'X', "Phoenix", 3, PieceClass.MINOR), // 1,0 jumps + 2,2 jumps
    TOWER(13U, 'T', "Tower", 2, PieceClass.MINOR), // 1,0 jumps + 2,0 jumps
    COMMONER(14U, 'O', "Common King", 4, PieceClass.MINOR), // king moves

    // fairy majors
    ARCHBISHOP(15U, 'H', "Archbishop", 6, PieceClass.MAJOR), // Knight + Bishop
    LION(16U, 'L', "Lion", 5, PieceClass.MAJOR), // 1,1 jumps + 2,0 jumps + 3,0 jumps

    // fairy queens
    UNICORN(17U, 'U', "Unicorn", 8, PieceClass.QUEEN), // 1,2 jumps + 1,3 jumps + 2,3 jumps
    ELEPHANT(18U, 'E', "War Elephant", 9, PieceClass.QUEEN), // 1,1 jumps + 2,0 jumps + 2,2 jumps
    CHANCELLOR(19U, 'C', "Chancellor", 9, PieceClass.QUEEN), // Knight + Rook
    DRAGON(20U, 'D', "Dragon", 10, PieceClass.QUEEN), // Rook + 3,2 jumps + 3,3 jumps
    AMAZON(21U, 'A', "Amazon", 10, PieceClass.QUEEN), // Queen + Knight

    // fairy kings
    GENERAL(22U, 'G', "General", 10, PieceClass.KING), // King + Knight
    CROWNQUEEN(23U, 'Y', "Royal Queen", 12, PieceClass.KING); // Queen

    companion object {
        val ID_LOOKUP: List<PieceType?>
        init {
            val arr = Array<PieceType?>(entries.size + 1) { null }
            for (entry in entries) arr[entry.ID.toInt()] = entry
            ID_LOOKUP = arr.toList()
        }

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