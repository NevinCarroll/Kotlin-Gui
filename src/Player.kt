/**
 * Player class
 *
 * Represents the player in the maze. Stores the current position
 * of the player and provides methods to get and set the position.
 */
class PlayerPosition() {

    // Array storing player's current position: [x, y]
    private var position: IntArray = IntArray(2)

    /**
     * Set the player's position in the maze.
     *
     * @param x Integer representing the row (vertical position) in the maze
     * @param y Integer representing the column (horizontal position) in the maze
     */
    fun setPosition(x: Int, y: Int) {
        position[0] = x
        position[1] = y
    }

    /**
     * Get the player's current position in the maze.
     *
     * @return An IntArray of size 2, where position[0] = x (row) and position[1] = y (column)
     */
    fun getPosition(): IntArray {
        return position
    }
}
