## ChecksMate XVI

A chess game for Archipelago, featuring:
- Fairy Pieces
  - Pawns:
    - Berolina (moves diagonally, attacks forward)
    - Soldier (moves and attacks forward)
    - Sergeant (moves and attacks forwards and diagonally)
  - Minors:
    - Camel (3,1 jumper)
    - Cleric (Bishop + 2,0 jumper)
    - Phoenix (1,0 / 2,2 jumper)
    - Tower (1,0 / 2,0 jumper)
    - Common King (King)
  - Majors (can castle with any King piece):
    - Archbishop (Knight + Bishop)
    - Lion (1,1 / 2,0 / 3,0 jumper)
  - Queens:
    - Unicorn (1,2 / 1,3 / 2,3 jumper)
    - War Elephant (1,1 / 2,0 / 2,2 jumper)
    - Chancellor (Knight + Rook)
    - Dragon (Rook + 3,2 / 3,3 jumper)
    - Amazon (Knight + Queen)
  - Kings (can castle with any Major piece):
    - General (King + Knight)
    - Royal Queen (Queen)
- Pocket Pieces (3 slots per side)
- TODO: Initial Position Configuration
- Board sizes from 6x6 to 16x16

## TODO List

- Implement AI levels 4 and 5 (proper board evaluation rather than sum of piece values)
- Come up with starting positions for the AI for various board sizes
  - 6x10 with two pawn layers consisting of soldiers and sergeants
  - 8x8 "pawn storm" with three layers (including piece layer) of pawns
  - 10x10 ultra board with three layers of pieces, and possibly multiple kings
- Reduce max board size to 10x10 (or higher if other starting positions need it)
- Implement a board editor for setting up pieces/pockets prior to starting, with a cost allowance (pockets are +2 cost)
  - Add rule to checker that prohibits kings on outer two files (castling logic breaks)
- Add Archipelago locations/items
  - Items:
    - AI downgrading
    - AI piece worsening
    - More currency for board editor
    - Ability to do various things in board editor (unlock pockets, piece types, etc.)
    - Ability to promote to better pieces
- Modify board renderer to show various locations that are available (such as an attack indicator on any piece that is a location)
- Get images for the remaining fairy pieces that lack one
- Help window to explain how pieces move, what locations there are, and what items will do when received
- Add mouseover text to locations/items to describe them