# Maze Explorer GUI

A maze game written in Kotlin using Java Swing for a graphical interface.
The player navigates through a maze to reach the end while avoiding walls and moving through open spaces.

## Features

- Graphical interface using Kotlin Swing

- Randomly selects one of multiple maze files at startup

- Tracks the number of moves taken by the player

- Allows restarting the game after completion

- Keyboard controls with W/A/S/D keys

- Visual representation of the player, walls, open spaces, and end point

## How to Play

1. Use the following keys to move the player:

- W = Up

- A = Left

- S = Down

- D = Right

2. Navigate from the starting position to the flag.

3. The game counts how long it takes for you to complete maze.

4. After reaching the end, you can restart the game or exit.

## Requirements

- Kotlin 2.3.0 or later

- Java JDK installed

## How to Run

1. Clone the repository:

``` bash
git clone https://github.com/NevinCarroll/Maze-Explorer-GUI.git
cd Maze-Explorer-GUI
```

2. Compile the Kotlin files (from the project root):

``` bash
kotlinc src/main/kotlin/*.kt -include-runtime -d MazeExplorerGUI.jar
```

3. Run the compiled JAR file:

``` bash
java -jar MazeExplorerGUI.jar
```

4. Use the keyboard controls to play the game in the GUI window.

## Editing Mazes

Maze files are located in /resources as maze[num].txt. Each file contains a 10x10 grid using:

- 1 = Wall

- 0 = Open space

- \* = End point

You can edit these files to create custom mazes. Only 0, 1, and * are valid; any other symbol will cause an exception.