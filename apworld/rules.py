from __future__ import annotations

from typing import TYPE_CHECKING

from BaseClasses import CollectionState
from worlds.generic.Rules import add_rule, set_rule

if TYPE_CHECKING:
    from .world import APChecksMateXVI

def set_all_rules(world: APChecksMateXVI) -> None:
    set_all_location_rules(world)
    set_completion_condition(world)

PIECE_BOARDS = {
    "Queenside Rook": ["FIDE", "Wide"],
    "Queenside Rook Pawn": ["FIDE", "Wide"],
    "Kingside Rook": ["FIDE", "Wide"],
    "Kingside Rook Pawn": ["FIDE", "Wide"],
    
    "Queenside Attendant": ["Wide"],
    "Queenside Attendant Pawn": ["Wide"],
    "Kingside Attendant": ["Wide"],
    "Kingside Attendant Pawn": ["Wide"]
}

def set_all_location_rules(world: APChecksMateXVI) -> None:
    for piece,places in PIECE_BOARDS:
        set_rule(
            world.get_location("Capture: " .. piece),
            lambda state: state.has_any(list(map(lambda p: "Board: " .. p, places)), world.player)
        )
    # TODO: expected points/unlocks for locations

def set_completion_condition(world: APChecksMateXVI) -> None:
    # TODO: has all boards and has expected points/unlocks for their victory items
    # TODO: handle options - world.options.??? = ???
    
    world.multiworld.completion_condition[world.player] = lambda state: state.has_all(("Board: FIDE", "Board: Wide"), world.player)
