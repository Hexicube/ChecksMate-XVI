from __future__ import annotations

from typing import TYPE_CHECKING

from BaseClasses import Entrance, Region

if TYPE_CHECKING:
    from .world import APChecksMateXVI

def create_and_connect_regions(world: APChecksMateXVI) -> None:
    world.multiworld.regions += [Region("Game", world.player, world.multiworld)]