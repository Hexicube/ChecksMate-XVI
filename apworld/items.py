from __future__ import annotations

from typing import TYPE_CHECKING

from BaseClasses import Item, ItemClassification

if TYPE_CHECKING:
    from .world import APChecksMateXVI

# Every item must have a unique integer ID associated with it.
# We will have a lookup from item name to ID here that, in world.py, we will import and bind to the world class.
# Even if an item doesn't exist on specific options, it must be present in this lookup.
ITEM_NAME_TO_ID = {
    # boards
    "Board: FIDE": 2,
    "Board: Wide": 3,
    # extra starting pieces
    "Starting Piece: 2 Pawns": 11,
    "Starting Piece: 2 Minors": 12,
    "Starting Piece: Major": 13,
    "Starting Piece: Queen": 14,
    "Starting Piece: King": 15,
    # AI downgrades
    "AI: Skill Downgrade": 21,
    "AI: Piece Downgrade": 22,
    # setup bonuses
    "Setup: More Points": 31,
    "Setup: Advanced Pawns": 32,
    "Setup: Advanced Pieces": 33,
    # setup unlocks
    "Unlock: Pocket Slot": 41,
    "Unlock: Fairy Pawns": 42,
    "Unlock: Fairy Minors": 43,
    "Unlock: Fairy Majors": 44,
    "Unlock: Fairy Queens": 45,
    "Unlock: Fairy Kings": 46
}

# only if >1
ITEM_COUNTS = {
    "Starting Piece: 2 Pawns": 8,
    "Starting Piece: 2 Minors": 5,
    "Starting Piece: Major": 6,
    "Starting Piece: Queen": 4,
    "Starting Piece: King": 2,
    "AI: Skill Downgrade": 4,
    "AI: Piece Downgrade": 4,
    "Setup: More Points": 10,
    "Setup: Advanced Pawns": 2,
    "Unlock: Pocket Slot": 3
}

# unspecified are useful
DEFAULT_ITEM_CLASSIFICATIONS = {
    "Board: FIDE": ItemClassification.progression,
    "Board: Wide": ItemClassification.progression,
}

class APChecksMateXVIItem(Item):
    game = "ChecksMate XVI"

def get_random_filler_item_name(world: APChecksMateXVI) -> str:
    # should never be needed
    return "Setup: More Points"

def create_item_with_correct_classification(world: APChecksMateXVI, name: str) -> ChecksMateItem:
    classification = DEFAULT_ITEM_CLASSIFICATIONS[name] or ItemClassification.useful
    return ChecksMateItem(name, classification, ITEM_NAME_TO_ID[name], world.player)

def create_all_items(world: APChecksMateXVI) -> None:
    itempool = []
    for name,id in ITEM_NAME_TO_ID:
        qty = ITEM_COUNTS[name] or 1
        for _ in range(qty):
            itempool += world.create_item(name)
    
    number_of_items = len(itempool)
    number_of_unfilled_locations = len(world.multiworld.get_unfilled_locations(world.player))
    needed_number_of_filler_items = number_of_unfilled_locations - number_of_items
    itempool += [world.create_filler() for _ in range(needed_number_of_filler_items)]
    world.multiworld.itempool += itempool