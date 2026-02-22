/**
 * TileType enum
 *
 * Represents the different types of tiles that can exist in the maze.
 * Used by the Tile class to determine behavior and display character.
 */
enum class TileType {
    /** An open space that the player can move through */
    OPEN,

    /** A wall that blocks the player's movement */
    WALL,

    /** The end point of the maze that the player aims to reach */
    END
}