import java.awt.BorderLayout
import java.awt.GridLayout
import java.io.File
import javax.swing.*

// Global variables
var player: Player = Player() // Player object representing the player
var maze: List<String>? = null // Holds the raw maze from file
var tiles: Array<Array<Tile>> = Array(4) { Array(10) { Tile(TileType.OPEN) } } // 2D array of Tile objects
var time: Int = 0 // Amount of moves player has taken

// Swing variables
var frame : JFrame = JFrame("Maze Explorer")
var currentPanel : JPanel = JPanel()

/**
 * TODO edit to work with GUI
 */
fun loadMaze(fileNum: Int) {
    maze = File("resources/maze$fileNum.txt").readLines(Charsets.UTF_8)

    // Convert maze file to tile types and load them into an array
    for ((rowIndex, row) in maze!!.withIndex()) {
        for ((columnIndex, col) in row.withIndex()) {
            val tileType = when (col) {
                '0' -> TileType.OPEN
                '1' -> TileType.WALL
                '*' -> TileType.END
                else -> throw IllegalArgumentException("Invalid tile type at row $rowIndex, column $col, in maze$fileNum.text") // Thrown exception when maze file has illegal arguments
            }
            tiles[rowIndex][columnIndex] = Tile(tileType)
        }
    }

    // Set player start position at top-left open tile
    player.setPosition(0, 0)
}

fun menuScreen() {
    // Create a panel to hold components

    currentPanel.removeAll()

    val label = JLabel("Maze Explorer")
    currentPanel.add(label)
    currentPanel.layout = BoxLayout(currentPanel, BoxLayout.Y_AXIS)
    currentPanel.add(Box.createHorizontalGlue())

    // Add a button
    val gameButton = JButton("Play Game")
    gameButton.addActionListener {
        tutorialScreen()
    }
    currentPanel.add(gameButton)

    // Add a button
    val tutorialButton = JButton("Tutorial")
    tutorialButton.addActionListener {
        tutorialScreen()
    }
    currentPanel.add(tutorialButton)

    val highScoreButton = JButton("High Scores")
    highScoreButton.addActionListener {
        highScoresScreen()
    }
    currentPanel.add(highScoreButton)

    // Add panel to frame
    frame.contentPane.add(currentPanel)

    // Make frame visible
    frame.isVisible = true

    frame.repaint()
    frame.revalidate()
}

fun gameScreen() {
    currentPanel.removeAll()

    currentPanel.layout = BorderLayout()

    val mazePanel = JPanel()
    mazePanel.layout = GridLayout(maze!!.size, 20) // TODO Change Hardcoded value

    val button = JButton("Back")
    button.addActionListener {
        menuScreen()
    }
    currentPanel.add(button)

    frame.repaint()
    frame.revalidate()
}

fun tutorialScreen() {
    currentPanel.removeAll()

    // TODO Add Tutorial Text

    val button = JButton("Back")
    button.addActionListener {
        menuScreen()
    }
    currentPanel.add(button)

    frame.repaint()
    frame.revalidate()
}

fun highScoresScreen() {
    currentPanel.removeAll()


    val button = JButton("Back")
    button.addActionListener {
        menuScreen()
    }
    currentPanel.add(button)
    // TODO Read csv file and display top 5 scores

    frame.repaint()
    frame.revalidate()
}

fun main () {
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(400, 300)

    menuScreen()

}