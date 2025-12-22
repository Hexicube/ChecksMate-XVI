from dataclasses import dataclass

from Options import Choice, OptionGroup, PerGameCommonOptions, Range, Toggle

# In this file, we define the options the player can pick.
# The most common types of options are Toggle, Range and Choice.

# Options will be in the game's template yaml.
# They will be represented by checkboxes, sliders etc. on the game's options page on the website.
# (Note: Options can also be made invisible from either of these places by overriding Option.visibility.
#  APQuest doesn't have an example of this, but this can be used for secret / hidden / advanced options.)

# For further reading on options, you can also read the Options API Document:
# https://github.com/ArchipelagoMW/Archipelago/blob/main/docs/options%20api.md

class Goal(Choice):
    """
    What boards are required for completion.
    """
    display_name = "Goal"

    option_win_wide_board = 0
    option_win_all_boards = 1
    # TODO: more options when more boards are added

    default = option_win_wide_board

class SetupPoints(Range):
    """
    How many setup points to start with.
    """
    display_name = "Initial Setup Points"

    range_start = 6 # king costs 6
    range_end = 30
    default = 10

@dataclass
class APChecksMateXVIOptions(PerGameCommonOptions):
    goal: Goal
    points: SetupPoints

option_groups = [
    OptionGroup(
        "Gameplay Options",
        [Goal, SetupPoints],
    ),
]

option_presets = {
    "default": {
        "goal": Goal.option_win_wide_board,
        "points": SetupPoints.default,
    }
}
