from __future__ import annotations

from typing import TYPE_CHECKING

from BaseClasses import ItemClassification, Location

from . import items

if TYPE_CHECKING:
    from .world import APChecksMateXVI

LOCATION_NAME_TO_ID = {
    # specific piece capture
    "Capture: Queenside Rook": 1,
    "Capture: Queenside Knight": 2,
    "Capture: Queenside Bishop": 3,
    "Capture: Queenside Attendant": 4,
    "Capture: Queen": 5,
    "Capture: Kingside Attendant": 7,
    "Capture: Kingside Bishop": 8,
    "Capture: Kingside Knight": 9,
    "Capture: Kingside Rook": 10,
    # specific pawn capture
    "Capture: Queenside Rook Pawn": 51,
    "Capture: Queenside Knight Pawn": 52,
    "Capture: Queenside Bishop Pawn": 53,
    "Capture: Queenside Attendant Pawn": 54,
    "Capture: Queen Pawn": 55,
    "Capture: King Pawn": 56,
    "Capture: Kingside Attendant Pawn": 57,
    "Capture: Kingside Bishop Pawn": 58,
    "Capture: Kingside Knight Pawn": 59,
    "Capture: Kingside Rook Pawn": 60,
    
    # pawn capture
    "Capture Set: 2 Pawns": 101,
    "Capture Set: 4 Pawns": 102,
    "Capture Set: 6 Pawns": 103,
    "Capture Set: 8 Pawns": 104,
    "Capture Set: 10 Pawns": 105,
    # piece capture
    "Capture Set: 2 Pieces": 111,
    "Capture Set: 4 Pieces": 112,
    "Capture Set: 6 Pieces": 113,
    "Capture Set: 8 Pieces": 114,
    "Capture Set: 10 Pieces": 115,
    # type capture
    "Capture Set: 2 Minors": 121,
    "Capture Set: 2 Majors": 122,
    # quantity capture
    "Capture Set: 5 Total": 131,
    "Capture Set: 10 Total": 132,
    "Capture Set: 15 Total": 133,
    "Capture Set: 20 Total": 134,
    
    # threats
    "Threaten: Minor": 151,
    "Threaten: Major": 152,
    "Threaten: Queen": 153,
    "Threaten: King": 154,
    
    # forks
    "Fork: False": 161,
    "Fork: False Triplet": 162,
    "Fork: False Royal": 163,
    "Fork: True": 166,
    "Fork: True Triplet": 167,
    "Fork: True Royal": 168,
    
    # places
    "King: Early Forward": 181,
    "King: To Centre": 182,
    "King: To Edge": 183,
    
    # survival
    "Survive: 5 Turns": 201,
    "Survive: 10 Turns": 202,
    "Survive: 20 Turns": 203,
    
    # fast wins
    "Win Fast: 40 Turns": 206,
    "Win Fast: 20 Turns": 207,
    
    # board wins
    "Win: Mini Board": 211,
    "Win: FIDE Board": 212,
    "Win: Wide Board": 213
}

class ChecksMateLocation(Location):
    game = "ChecksMate XVI"

def get_location_names_with_ids(location_names: list[str]) -> dict[str, int | None]:
    return {location_name: LOCATION_NAME_TO_ID[location_name] for location_name in location_names}

def create_all_locations(world: APChecksMateXVI) -> None:
    region = world.get_region("Game")
    region.add_location(LOCATION_NAME_TO_ID, ChecksMateLocation)