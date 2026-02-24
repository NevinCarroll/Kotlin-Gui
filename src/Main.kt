/**
 * Maze Explorer Game
 *
 * This is a simple maze navigation game built using Kotlin and Swing.
 * The player navigates through randomly loaded maze files to reach the end tile.
 * The game tracks the time taken by the player and stores high scores in a CSV file.
 *
 * Features:
 * - Random maze selection from resources/mazes folder
 * - Timer tracking player completion time
 * - Tutorial and high scores screens
 * - Player movement using W, A, S, D keys
 * - Image-based tiles for player, walls, and end flag
 */

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.Image
import java.io.File
import javax.swing.*

// Player object representing the current player
var player: Player = Player("")

// Raw maze file content (list of strings)
var maze: List<String>? = null

// 2D array representing the maze with Tile objects
var tiles: Array<Array<Tile>> = Array(10) { Array(10) { Tile(TileType.OPEN) } }

// Timer variables
var secondsElapsed: Int = 0  // Time elapsed in seconds
var gameTimer: Timer? = null
var timerLabel: JLabel? = null

// Swing GUI components
var frame: JFrame = JFrame("Maze Explorer") // Main application window
var currentPanel: JPanel = JPanel()        // Panel currently displayed

// Images for different tile types
var playerImage: Icon? = null
var wallImage: Icon? = null
var flagImage: Icon? = null

/**
 * Load a maze from a text file and populate the tiles array.
 *
 * @param fileNum The maze file number to load (e.g., "maze1.txt")
 */
fun loadMaze(fileNum: Int) {
    maze = File("resources/mazes/maze$fileNum.txt").readLines(Charsets.UTF_8)

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

/**
 * Selects a random maze file number from the resources/mazes folder.
 *
 * @return Random maze file number
 */
fun getRandomMazeFileNumber(): Int {
    val mazeFolder = File("resources/mazes")
    val mazeFiles = mazeFolder.listFiles { file -> file.isFile && file.name.matches(Regex("maze\\d+\\.txt")) } // Only get files that follow this format "maze{number}.txt"

    if (mazeFiles.isNullOrEmpty()) {
        throw IllegalStateException("No maze files found in resources/mazes")
    }

    // Extract numbers from filenames
    val mazeNumbers = mazeFiles.map { file ->
        Regex("""maze(\d+)\.txt""").find(file.name)!!.groupValues[1].toInt()
    }

    return mazeNumbers.random() // Pick a random number
}

/**
 * Displays the main menu screen with buttons for game, tutorial, and high scores.
 */
fun menuScreen() {
    currentPanel.removeAll()
    currentPanel.layout = BoxLayout(currentPanel, BoxLayout.Y_AXIS)

    // Add vertical glue at top to center items vertically
    currentPanel.add(Box.createVerticalGlue())

    // Title
    val titleLabel = JLabel("Maze Explorer")
    titleLabel.alignmentX = JLabel.CENTER_ALIGNMENT // Center label
    titleLabel.font = titleLabel.font.deriveFont(24f) // larger font
    currentPanel.add(titleLabel)
    currentPanel.add(Box.createRigidArea(Dimension(0, 30))) // space after title

    // Buttons
    val gameButton = JButton("Play Game")
    gameButton.alignmentX = JButton.CENTER_ALIGNMENT
    gameButton.maximumSize = Dimension(200, 40)
    gameButton.addActionListener { gameScreen() } // Send to other screen
    currentPanel.add(gameButton)
    currentPanel.add(Box.createRigidArea(Dimension(0, 15))) // spacing between buttons

    val tutorialButton = JButton("Tutorial")
    tutorialButton.alignmentX = JButton.CENTER_ALIGNMENT
    tutorialButton.maximumSize = Dimension(200, 40)
    tutorialButton.addActionListener { tutorialScreen() }
    currentPanel.add(tutorialButton)
    currentPanel.add(Box.createRigidArea(Dimension(0, 15)))

    val highScoreButton = JButton("High Scores")
    highScoreButton.alignmentX = JButton.CENTER_ALIGNMENT
    highScoreButton.maximumSize = Dimension(200, 40)
    highScoreButton.addActionListener { highScoresScreen() }
    currentPanel.add(highScoreButton)

    // Add vertical glue at bottom to center content
    currentPanel.add(Box.createVerticalGlue())

    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.repaint()
    frame.revalidate()
}

/**
 * Displays the main game screen with the maze and player.
 */
fun gameScreen() {
    currentPanel.removeAll()
    currentPanel.layout = BorderLayout()

    val randomMazeNum = getRandomMazeFileNumber()
    loadMaze(randomMazeNum)

    val mazePanel = JPanel()
    mazePanel.layout = GridLayout(tiles.size, tiles[0].size)

    // Set correct preferred size for maze panel
    val tileSize = 64
    mazePanel.preferredSize = Dimension(
        tiles[0].size * tileSize,
        tiles.size * tileSize
    )

    updateScreen(mazePanel)

    setMazePanelInput(mazePanel)

    startTimer()

    // Put maze inside scroll pane
    val scrollPane = JScrollPane(mazePanel)
    currentPanel.add(scrollPane, BorderLayout.CENTER)

    // Bottom panel for back button and timer
    val bottomPanel = JPanel()
    bottomPanel.layout = BorderLayout()

    // Back button
    val button = JButton("Back")
    button.addActionListener {
        menuScreen()
        stopTimer()
        secondsElapsed = 0
    }
    bottomPanel.add(button, BorderLayout.WEST)

    // Timer label
    timerLabel = JLabel("Time: 0s")
    timerLabel!!.horizontalAlignment = JLabel.CENTER
    bottomPanel.add(timerLabel, BorderLayout.EAST)

    currentPanel.add(bottomPanel, BorderLayout.SOUTH)

    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.repaint()
    frame.revalidate()
}

/**
 * Updates the maze panel to reflect the current state of the player and tiles.
 *
 * @param mazePanel The JPanel representing the maze
 */
fun updateScreen(mazePanel: JPanel) {
    mazePanel.removeAll()

    for ((rowIndex, row) in tiles.withIndex()) {
        for ((columnIndex, col) in row.withIndex()) {

            // Label that holds tile image
            val label = JLabel()
            label.horizontalAlignment = JLabel.CENTER

            val tileType = tiles[rowIndex][columnIndex].getType()

            // Render each tile type, or render player if they are on open tile
            when (tileType) {
                TileType.WALL -> label.icon = wallImage
                TileType.END -> label.icon = flagImage
                TileType.OPEN -> {
                    if (player.getPosition()[0] == rowIndex &&
                        player.getPosition()[1] == columnIndex
                    ) {
                        label.icon = playerImage
                    }
                }
            }
            mazePanel.add(label)
        }
    }

    mazePanel.revalidate()
    mazePanel.repaint()
}

/**
 * Displays tutorial instructions with images and controls.
 */
fun tutorialScreen() {
    currentPanel.removeAll()
    currentPanel.layout = BoxLayout(currentPanel, BoxLayout.Y_AXIS)

    // Title
    val titleLabel = JLabel("Maze Explorer Tutorial")
    titleLabel.alignmentX = JLabel.CENTER_ALIGNMENT
    titleLabel.font = titleLabel.font.deriveFont(18f)
    currentPanel.add(titleLabel)
    currentPanel.add(Box.createRigidArea(Dimension(0, 15)))

    // Game Goal
    val goalLabel = JLabel("Goal: Navigate through the maze and reach the flag as fast as possible!")
    goalLabel.alignmentX = JLabel.CENTER_ALIGNMENT
    currentPanel.add(goalLabel)
    currentPanel.add(Box.createRigidArea(Dimension(0, 15)))

    // Controls
    val controlsLabel = JLabel("Controls: W: Move Up - S: Move Down - A: Move Left - D: Move Right")
    controlsLabel.alignmentX = JLabel.CENTER_ALIGNMENT
    currentPanel.add(controlsLabel)
    currentPanel.add(Box.createRigidArea(Dimension(0, 15)))

    // Show images
    val imagePanel = JPanel()
    imagePanel.layout = GridLayout(1, 3, 10, 10)
    imagePanel.alignmentX = JPanel.CENTER_ALIGNMENT

    val playerLabel = JLabel("Player", playerImage, JLabel.CENTER)
    playerLabel.verticalTextPosition = JLabel.BOTTOM
    playerLabel.horizontalTextPosition = JLabel.CENTER

    val wallLabel = JLabel("Wall", wallImage, JLabel.CENTER)
    wallLabel.verticalTextPosition = JLabel.BOTTOM
    wallLabel.horizontalTextPosition = JLabel.CENTER

    val flagLabel = JLabel("Flag (End)", flagImage, JLabel.CENTER)
    flagLabel.verticalTextPosition = JLabel.BOTTOM
    flagLabel.horizontalTextPosition = JLabel.CENTER

    imagePanel.add(playerLabel)
    imagePanel.add(wallLabel)
    imagePanel.add(flagLabel)

    currentPanel.add(imagePanel)
    currentPanel.add(Box.createRigidArea(Dimension(0, 20)))

    // Back button
    val backButton = JButton("Back")
    backButton.alignmentX = JButton.CENTER_ALIGNMENT
    backButton.addActionListener {
        menuScreen()
    }
    currentPanel.add(backButton)

    frame.pack()
    frame.revalidate()
    frame.repaint()
}

/**
 * Display top 10 high scores
 */
fun highScoresScreen() {
    currentPanel.removeAll()
    currentPanel.layout = BoxLayout(currentPanel, BoxLayout.Y_AXIS)

    // Get high scores files
    val scoreFile = File("resources/highscores.csv")
    val scores = mutableListOf<Pair<String, Int>>()

    // Parse high scores file, (Name, Time)
    if (scoreFile.exists()) {
        scoreFile.readLines().forEach { line ->
            val parts = line.split(",")
            if (parts.size == 2) {
                val name = parts[0]
                val time = parts[1].toIntOrNull()
                if (time != null) scores.add(name to time)
            }
        }
    }

    // Sort by time ascending and take top 10
    val topScores = scores.sortedBy { it.second }.take(10)

    currentPanel.add(JLabel("High Scores").apply { alignmentX = JLabel.CENTER_ALIGNMENT })
    currentPanel.add(Box.createRigidArea(Dimension(0, 10)))

    // Display highs cores if there are any
    if (topScores.isEmpty()) {
        currentPanel.add(JLabel("No high scores yet").apply { alignmentX = JLabel.CENTER_ALIGNMENT })
    } else {
        topScores.forEachIndexed { index, (name, time) ->
            val label = JLabel("${index + 1}. $name - $time seconds")
            label.alignmentX = JLabel.CENTER_ALIGNMENT
            currentPanel.add(label)
        }
    }

    currentPanel.add(Box.createRigidArea(Dimension(0, 20)))
    val button = JButton("Back")
    button.alignmentX = JButton.CENTER_ALIGNMENT
    button.addActionListener { menuScreen() }
    currentPanel.add(button)

    frame.pack()
    frame.revalidate()
    frame.repaint()
}

/**
 * Display time player took to complete maze, and allow them to go back to main menu or play again
 */
fun gameOverScreen() {
    currentPanel.removeAll()
    currentPanel.layout = BoxLayout(currentPanel, BoxLayout.Y_AXIS)

    // Get player name and save their time
    val playerName =
        JOptionPane.showInputDialog(frame, "Enter your name:", "Player Name", JOptionPane.PLAIN_MESSAGE) ?: "Anonymous"
    saveScore(playerName, secondsElapsed)

    // Labels saying player won and their times
    val winLabel = JLabel("You Escaped the Maze!!")
    winLabel.alignmentX = JLabel.CENTER_ALIGNMENT

    val timeLabel = JLabel("Time: $secondsElapsed seconds")
    timeLabel.alignmentX = JLabel.CENTER_ALIGNMENT

    // Allow player to play again or go back to main menu
    val playAgainButton = JButton("Play Again")
    playAgainButton.alignmentX = JButton.CENTER_ALIGNMENT
    playAgainButton.addActionListener {
        gameScreen()
    }

    val menuButton = JButton("Main Menu")
    menuButton.alignmentX = JButton.CENTER_ALIGNMENT
    menuButton.addActionListener {
        menuScreen()
    }

    currentPanel.add(Box.createVerticalGlue())
    currentPanel.add(winLabel)
    currentPanel.add(Box.createRigidArea(Dimension(0, 10)))
    currentPanel.add(timeLabel)
    currentPanel.add(Box.createRigidArea(Dimension(0, 20)))
    currentPanel.add(playAgainButton)
    currentPanel.add(Box.createRigidArea(Dimension(0, 10)))
    currentPanel.add(menuButton)
    currentPanel.add(Box.createVerticalGlue())

    frame.pack()
    frame.revalidate()
    frame.repaint()
}

/**
 * Moves the player by the specified row and column delta.
 * Prevents moving through walls or out-of-bounds.
 * Ends game if player reaches the END tile.
 *
 * @param rowDelta Change in row (-1, 0, 1)
 * @param colDelta Change in column (-1, 0, 1)
 */
fun movePlayer(rowDelta: Int, colDelta: Int) {
    val currentRow = player.getPosition()[0]
    val currentCol = player.getPosition()[1]

    // Tile player will be moving to
    val newRow = currentRow + rowDelta
    val newCol = currentCol + colDelta

    // Bounds check
    if (newRow !in tiles.indices || newCol !in tiles[0].indices) return

    val targetTile = tiles[newRow][newCol].getType()

    // Prevent walking through walls
    if (targetTile == TileType.WALL) return

    player.setPosition(newRow, newCol)

    // Check for win condition
    if (targetTile == TileType.END) {
        stopTimer()
        gameOverScreen()
        return
    }
}

/**
 * Sets the keyboard input bindings for the maze panel.
 *
 * @param panel The JPanel representing the maze
 */
fun setMazePanelInput(panel: JPanel) {
    val inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
    val actionMap = panel.actionMap

    // Assign keys to certain actions
    inputMap.put(KeyStroke.getKeyStroke("W"), "moveUp")
    inputMap.put(KeyStroke.getKeyStroke("S"), "moveDown")
    inputMap.put(KeyStroke.getKeyStroke("A"), "moveLeft")
    inputMap.put(KeyStroke.getKeyStroke("D"), "moveRight")

    // Assign methods to keys
    actionMap.put("moveUp", object : AbstractAction() {
        override fun actionPerformed(e: java.awt.event.ActionEvent?) {
            movePlayer(-1, 0)
            updateScreen(panel)

        }
    })

    actionMap.put("moveDown", object : AbstractAction() {
        override fun actionPerformed(e: java.awt.event.ActionEvent?) {
            movePlayer(1, 0)
            updateScreen(panel)
        }
    })

    actionMap.put("moveLeft", object : AbstractAction() {
        override fun actionPerformed(e: java.awt.event.ActionEvent?) {
            movePlayer(0, -1)
            updateScreen(panel)
        }
    })

    actionMap.put("moveRight", object : AbstractAction() {
        override fun actionPerformed(e: java.awt.event.ActionEvent?) {
            movePlayer(0, 1)
            updateScreen(panel)
        }
    })
}

/**
 * Starts the game timer and updates the timer label every second.
 */
fun startTimer() {
    secondsElapsed = 0

    gameTimer = Timer(1000) {   // fires every 1000ms (1 second)
        secondsElapsed++
        timerLabel?.text = "Time: ${secondsElapsed}s"  // update label
    }

    gameTimer!!.start()
}

/**
 * Stops the game timer.
 */
fun stopTimer() {
    gameTimer?.stop()
}

/**
 * Loads and scales images for player, wall, and flag tiles.
 */
fun loadImages() {
    val tileSize = 64  // Scale images to size of tiles

    // Get image and scale it down so it fits on screen better
    playerImage = ImageIcon(
        ImageIcon("resources/images/player.png")
            .image.getScaledInstance(tileSize, tileSize, Image.SCALE_SMOOTH)
    )

    wallImage = ImageIcon(
        ImageIcon("resources/images/wall.png")
            .image.getScaledInstance(tileSize, tileSize, Image.SCALE_SMOOTH)
    )

    flagImage = ImageIcon(
        ImageIcon("resources/images/flag.png")
            .image.getScaledInstance(tileSize, tileSize, Image.SCALE_SMOOTH)
    )
}

/**
 * Saves the player's score to a CSV file.
 *
 * @param playerName Name of the player
 * @param time Time in seconds taken to complete the maze
 */
fun saveScore(playerName: String, time: Int) {
    val scoreFile = File("resources/highscores.csv")
    if (!scoreFile.exists()) {
        scoreFile.parentFile.mkdirs() // create resources folder if needed
        scoreFile.createNewFile()
    }

    // Append the new score
    scoreFile.appendText("$playerName,$time\n")
}

/**
 * Entry point of the Maze Explorer game.
 * Initializes frame, loads images, and displays the main menu.
 */
fun main() {
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(400, 300)

    // Add panel to frame
    frame.contentPane.add(currentPanel)

    // Load images for repeated use rather than each time they are needed
    loadImages()
    menuScreen() // Go to main menu

    frame.isVisible = true
}