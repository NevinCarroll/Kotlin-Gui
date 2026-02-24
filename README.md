# Maze Explorer

A simple text-based maze game written in Kotlin.  
The player (@) navigates through a randomly generated maze to reach the end (*), avoiding walls (#) and moving only through open spaces ($).

## Features

- Console-based game with clear text visualization of the maze
- Randomly selects one of multiple maze files on start
- Tracks the number of moves taken by the player
- Supports restarting the game after completion
- Simple controls using W/A/S/D keys

## How to Play

1. Use the following controls to move:
    - `W` = Up
    - `A` = Left
    - `S` = Down
    - `D` = Right
2. Navigate from the starting position (top-left) to the end tile `*`.
3. The game tracks how many moves it takes to finish the maze.
4. After finishing, you can choose to play again or exit.

## Maze Representation

| Symbol | Meaning       |
|--------|---------------|
| @      | Player        |
| #      | Wall          |
| $      | Open space    |
| *      | End of maze   |

## Requirements

- Kotlin 2.3.0
- Java JDK installed

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/NevinCarroll/kotlin-console.git
2. Run the main function in Main.kt
3. Follow the on-screen instructions to play

## Edit Maze

If you wish to edit a maze, go to /resources, and you will see maze\[num].txt files. When opening a file, you
will see a 10x10 grid of 1 and 0, along with an *.

The 1s represent a wall, the 0s open spaces, and the * as the end point. Change any of the symbols to another one to edit
the maze's layout. If a symbol besides 0, 1, or * is in the file, an exception will be thrown for an improper maze file.

