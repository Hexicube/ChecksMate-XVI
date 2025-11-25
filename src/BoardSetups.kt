class BoardSetups {
    companion object {
        val TEST_ALL_PIECES = Board.boardWithPieces(12, 8, PieceType.ALL_PROMOTE,
            // pockets
            Array(6) { null },
            // TODO: all pieces here for both sides
            // white pawns
            PieceWithPos(Piece(PieceType.PAWN, true, false, ""), 0, 1),
            PieceWithPos(Piece(PieceType.BEROLINA, true, false, ""), 1, 1),
            PieceWithPos(Piece(PieceType.SOLDIER, true, false, ""), 2, 1),
            PieceWithPos(Piece(PieceType.SERGEANT, true, false, ""), 3, 1),
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

        val MINI_BOARD = Board.boardWithPieces(6, 6, PieceType.FIDE_PROMOTE,
            // pockets
            Array(6) { null },
            // white pieces
            PieceWithPos(Piece(PieceType.KNIGHT, true, false, "Queenside Knight"), 0, 0),
            PieceWithPos(Piece(PieceType.BISHOP, true, false, "Queenside Bishop"), 1, 0),
            PieceWithPos(Piece(PieceType.QUEEN, true, false, "Queen"), 2, 0),
            PieceWithPos(Piece(PieceType.KING, true, false, "King"), 3, 0),
            PieceWithPos(Piece(PieceType.BISHOP, true, false, "Kingside Bishop"), 4, 0),
            PieceWithPos(Piece(PieceType.KNIGHT, true, false, "Kingside Knight"), 5, 0),
            // white pawns
            PieceWithPos(Piece(PieceType.SOLDIER, true, false, "Queenside Knight Pawn"), 0, 1),
            PieceWithPos(Piece(PieceType.SOLDIER, true, false, "Queenside Bishop Pawn"), 1, 1),
            PieceWithPos(Piece(PieceType.SOLDIER, true, false, "Queen Pawn"), 2, 1),
            PieceWithPos(Piece(PieceType.SOLDIER, true, false, "King Pawn"), 3, 1),
            PieceWithPos(Piece(PieceType.SOLDIER, true, false, "Kingside Bishop Pawn"), 4, 1),
            PieceWithPos(Piece(PieceType.SOLDIER, true, false, "Kingside Knight Pawn"), 5, 1),
            // black pawns
            PieceWithPos(Piece(PieceType.SOLDIER, false, false, "Queenside Knight Pawn"), 0, 4),
            PieceWithPos(Piece(PieceType.SOLDIER, false, false, "Queenside Bishop Pawn"), 1, 4),
            PieceWithPos(Piece(PieceType.SOLDIER, false, false, "Queen Pawn"), 2, 4),
            PieceWithPos(Piece(PieceType.SOLDIER, false, false, "King Pawn"), 3, 4),
            PieceWithPos(Piece(PieceType.SOLDIER, false, false, "Kingside Bishop Pawn"), 4, 4),
            PieceWithPos(Piece(PieceType.SOLDIER, false, false, "Kingside Knight Pawn"), 5, 4),
            // black pieces
            PieceWithPos(Piece(PieceType.KNIGHT, false, false, "Queenside Knight"), 0, 5),
            PieceWithPos(Piece(PieceType.BISHOP, false, false, "Queenside Bishop"), 1, 5),
            PieceWithPos(Piece(PieceType.QUEEN, false, false, "Queen"), 2, 5),
            PieceWithPos(Piece(PieceType.KING, false, false, "King"), 3, 5),
            PieceWithPos(Piece(PieceType.BISHOP, false, false, "Kingside Bishop"), 4, 5),
            PieceWithPos(Piece(PieceType.KNIGHT, false, false, "Kingside Knight"), 5, 5),
        )

        val FIDE = Board.boardWithPieces(8, 8, PieceType.FIDE_PROMOTE,
            // pockets
            Array(6) { null },
            // white pieces
            PieceWithPos(Piece(PieceType.ROOK, true, false, "Queenside Rook"), 0, 0),
            PieceWithPos(Piece(PieceType.KNIGHT, true, false, "Queenside Knight"), 1, 0),
            PieceWithPos(Piece(PieceType.BISHOP, true, false, "Queenside Bishop"), 2, 0),
            PieceWithPos(Piece(PieceType.QUEEN, true, false, "Queen"), 3, 0),
            PieceWithPos(Piece(PieceType.KING, true, false, "King"), 4, 0),
            PieceWithPos(Piece(PieceType.BISHOP, true, false, "Kingside Bishop"), 5, 0),
            PieceWithPos(Piece(PieceType.KNIGHT, true, false, "Kingside Knight"), 6, 0),
            PieceWithPos(Piece(PieceType.ROOK, true, false, "Kingside Rook"), 7, 0),
            // white pawns
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Queenside Rook Pawn"), 0, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Queenside Knight Pawn"), 1, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Queenside Bishop Pawn"), 2, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Queen Pawn"), 3, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "King Pawn"), 4, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Kingside Bishop Pawn"), 5, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Kingside Knight Pawn"), 6, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Kingside Rook Pawn"), 7, 1),
            // black pawns
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Queenside Rook Pawn"), 0, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Queenside Knight Pawn"), 1, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Queenside Bishop Pawn"), 2, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Queen Pawn"), 3, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "King Pawn"), 4, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Kingside Bishop Pawn"), 5, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Kingside Knight Pawn"), 6, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Kingside Rook Pawn"), 7, 6),
            // black pieces
            PieceWithPos(Piece(PieceType.ROOK, false, false, "Queenside Rook"), 0, 7),
            PieceWithPos(Piece(PieceType.KNIGHT, false, false, "Queenside Knight"), 1, 7),
            PieceWithPos(Piece(PieceType.BISHOP, false, false, "Queenside Bishop"), 2, 7),
            PieceWithPos(Piece(PieceType.QUEEN, false, false, "Queen"), 3, 7),
            PieceWithPos(Piece(PieceType.KING, false, false, "King"), 4, 7),
            PieceWithPos(Piece(PieceType.BISHOP, false, false, "Kingside Bishop"), 5, 7),
            PieceWithPos(Piece(PieceType.KNIGHT, false, false, "Kingside Knight"), 6, 7),
            PieceWithPos(Piece(PieceType.ROOK, false, false, "Kingside Rook"), 7, 7),
        )

        val WIDE = Board.boardWithPieces(10, 8, PieceType.FIDE_PROMOTE,
            // pockets
            Array(6) { null },
            // white pieces
            PieceWithPos(Piece(PieceType.ROOK, true, false, "Queenside Rook"), 0, 0),
            PieceWithPos(Piece(PieceType.KNIGHT, true, false, "Queenside Knight"), 1, 0),
            PieceWithPos(Piece(PieceType.BISHOP, true, false, "Queenside Bishop"), 2, 0),
            PieceWithPos(Piece(PieceType.ARCHBISHOP, true, false, "Queenside Attendant"), 3, 0),
            PieceWithPos(Piece(PieceType.QUEEN, true, false, "Queen"), 4, 0),
            PieceWithPos(Piece(PieceType.KING, true, false, "King"), 5, 0),
            PieceWithPos(Piece(PieceType.CHANCELLOR, true, false, "Kingside Attendant"), 6, 0),
            PieceWithPos(Piece(PieceType.BISHOP, true, false, "Kingside Bishop"), 7, 0),
            PieceWithPos(Piece(PieceType.KNIGHT, true, false, "Kingside Knight"), 8, 0),
            PieceWithPos(Piece(PieceType.ROOK, true, false, "Kingside Rook"), 9, 0),
            // white pawns
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Queenside Rook Pawn"), 0, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Queenside Knight Pawn"), 1, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Queenside Bishop Pawn"), 2, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Queenside Attendant Pawn"), 3, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Queen Pawn"), 4, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "King Pawn"), 5, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Kingside Attendant Pawn"), 6, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Kingside Bishop Pawn"), 7, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Kingside Knight Pawn"), 8, 1),
            PieceWithPos(Piece(PieceType.PAWN, true, false, "Kingside Rook Pawn"), 9, 1),
            // black pawns
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Queenside Rook Pawn"), 0, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Queenside Knight Pawn"), 1, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Queenside Bishop Pawn"), 2, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Queenside Attendant Pawn"), 3, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Queen Pawn"), 4, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "King Pawn"), 5, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Kingside Attendant Pawn"), 6, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Kingside Bishop Pawn"), 7, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Kingside Knight Pawn"), 8, 6),
            PieceWithPos(Piece(PieceType.PAWN, false, false, "Kingside Rook Pawn"), 9, 6),
            // black pieces
            PieceWithPos(Piece(PieceType.ROOK, false, false, "Queenside Rook"), 0, 7),
            PieceWithPos(Piece(PieceType.KNIGHT, false, false, "Queenside Knight"), 1, 7),
            PieceWithPos(Piece(PieceType.BISHOP, false, false, "Queenside Bishop"), 2, 7),
            PieceWithPos(Piece(PieceType.ARCHBISHOP, false, false, "Queenside Attendant"), 3, 7),
            PieceWithPos(Piece(PieceType.QUEEN, false, false, "Queen"), 4, 7),
            PieceWithPos(Piece(PieceType.KING, false, false, "King"), 5, 7),
            PieceWithPos(Piece(PieceType.CHANCELLOR, false, false, "Kingside Attendant"), 6, 7),
            PieceWithPos(Piece(PieceType.BISHOP, false, false, "Kingside Bishop"), 7, 7),
            PieceWithPos(Piece(PieceType.KNIGHT, false, false, "Kingside Knight"), 8, 7),
            PieceWithPos(Piece(PieceType.ROOK, false, false, "Kingside Rook"), 9, 7),
        )
    }
}