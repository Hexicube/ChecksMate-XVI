class BoardSetups {
    companion object {
        val TEST_ALL_PIECES = Board.boardWithPieces(12, 8, PieceType.ALL_PROMOTE,
            // TODO: all pieces here for both sides
            // white pawns
            PieceWithPos(Piece(PieceType.PAWN, true, false, ""), 0, 1),
            PieceWithPos(Piece(PieceType.BEROLINA, true, false, ""), 1, 1),
            PieceWithPos(Piece(PieceType.SOLDIER, true, false, ""), 2, 1),
            //PieceWithPos(Piece(PieceType.SERGEANT, true, false, ""), 3, 1),
            // white minors
            PieceWithPos(Piece(PieceType.KNIGHT, true, false, ""), 4, 1),
            PieceWithPos(Piece(PieceType.BISHOP, true, false, ""), 5, 1),
            PieceWithPos(Piece(PieceType.CAMEL, true, false, ""), 6, 1),
            PieceWithPos(Piece(PieceType.CLERIC, true, false, ""), 7, 1),
            PieceWithPos(Piece(PieceType.PHOENIX, true, false, ""), 8, 1),
            PieceWithPos(Piece(PieceType.TOWER, true, false, ""), 9, 1),
            PieceWithPos(Piece(PieceType.COMMONER, true, false, ""), 10, 1),
            // white majors
            PieceWithPos(Piece(PieceType.ROOK, true, false, ""), 0, 0),
            PieceWithPos(Piece(PieceType.ARCHBISHOP, true, false, ""), 1, 0),
            // white majors (kingside)
            PieceWithPos(Piece(PieceType.LION, true, false, ""), 11, 0),
            // white queens
            PieceWithPos(Piece(PieceType.QUEEN, true, false, ""), 2, 0),
            PieceWithPos(Piece(PieceType.ELEPHANT, true, false, ""), 3, 0),
            PieceWithPos(Piece(PieceType.CHANCELLOR, true, false, ""), 4, 0),
            // white queens (kingside)
            PieceWithPos(Piece(PieceType.DRAGON, true, false, ""), 8, 0),
            PieceWithPos(Piece(PieceType.AMAZON, true, false, ""), 9, 0),
            PieceWithPos(Piece(PieceType.UNICORN, true, false, ""), 10, 0),
            // white kings
            PieceWithPos(Piece(PieceType.KING, true, false, ""), 5, 0),
            PieceWithPos(Piece(PieceType.GENERAL, true, false, ""), 6, 0),
            PieceWithPos(Piece(PieceType.CROWNQUEEN, true, false, ""), 7, 0),

            // black king
            PieceWithPos(Piece(PieceType.KING, false, false, ""), 5, 7),
            // debug berolina pawn
            PieceWithPos(Piece(PieceType.BEROLINA, false, false, ""), 1, 3)
        )

        val FIDE = Board.boardWithPieces(8, 8, PieceType.FIDE_PROMOTE,
            // white pieces
            PieceWithPos(Piece(PieceType.ROOK, true, false, "Rook Queenside"), 0, 0),
            PieceWithPos(Piece(PieceType.KNIGHT, true, false, "Knight Queenside"), 1, 0),
            PieceWithPos(Piece(PieceType.BISHOP, true, false, "Bishop Queenside"), 2, 0),
            PieceWithPos(Piece(PieceType.QUEEN, true, false, "Queen"), 3, 0),
            PieceWithPos(Piece(PieceType.KING, true, false, "King"), 4, 0),
            PieceWithPos(Piece(PieceType.BISHOP, true, false, "Bishop Kingside"), 5, 0),
            PieceWithPos(Piece(PieceType.KNIGHT, true, false, "Knight Kingside"), 6, 0),
            PieceWithPos(Piece(PieceType.ROOK, true, false, "Rook Kingside"), 7, 0),
            // white pawns
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Pawn A"), 0, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Pawn B"), 1, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Pawn C"), 2, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Pawn D"), 3, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Pawn E"), 4, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Pawn F"), 5, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Pawn G"), 6, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Pawn H"), 7, 1),
            // black pawns
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Pawn A"), 0, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Pawn B"), 1, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Pawn C"), 2, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Pawn D"), 3, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Pawn E"), 4, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Pawn F"), 5, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Pawn G"), 6, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Pawn H"), 7, 6),
            // black pieces
            PieceWithPos(Piece(PieceType.ROOK, false, false, "Rook Queenside"), 0, 7),
            PieceWithPos(Piece(PieceType.KNIGHT, false, false, "Knight Queenside"), 1, 7),
            PieceWithPos(Piece(PieceType.BISHOP, false, false, "Bishop Queenside"), 2, 7),
            PieceWithPos(Piece(PieceType.QUEEN, false, false, "Queen"), 3, 7),
            PieceWithPos(Piece(PieceType.KING, false, false, "King"), 4, 7),
            PieceWithPos(Piece(PieceType.BISHOP, false, false, "Bishop Kingside"), 5, 7),
            PieceWithPos(Piece(PieceType.KNIGHT, false, false, "Knight Kingside"), 6, 7),
            PieceWithPos(Piece(PieceType.ROOK, false, false, "Rook Kingside"), 7, 7),
        )
    }
}