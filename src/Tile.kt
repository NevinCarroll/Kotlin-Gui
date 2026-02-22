/**
 * Tile class
 *
 * Represents a single tile in the maze. Each tile has a type
 * which can be WALL, OPEN, or END.
 *
 * @property tileType The type of the tile (TileType)
 */
class Tile(private var tileType: TileType) {

    /**
     * Get the type of this tile.
     *
     * @return The TileType of this tile (WALL, OPEN, or END)
     */
    fun getType(): TileType {
        return tileType
    }
}
