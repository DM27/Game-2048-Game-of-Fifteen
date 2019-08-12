package games.gameOfFifteen

import board.Cell
import board.Direction
import board.createGameBoard
import games.game.Game

/*
 * Implement the Game of Fifteen (https://en.wikipedia.org/wiki/15_puzzle).
 * When you finish, you can play the game by executing 'PlayGameOfFifteen'.
 */
fun newGameOfFifteen(initializer: GameOfFifteenInitializer = RandomGameInitializer()): Game =
        GameOfFifteen(initializer)


class GameOfFifteen(private val initializer: GameOfFifteenInitializer) : Game {

    private val board = createGameBoard<Int?>(4)

    override fun initialize() = initializer.initialPermutation
            .forEachIndexed { index, value ->
                val i = index / 4 + 1
                val j = index % 4 + 1
                val cell = board.getCell(i, j)
                board.set(cell, value)
            }

    override fun get(i: Int, j: Int): Int? = board.run { get(getCell(i, j)) }

    override fun canMove() = true

    override fun hasWon() = board.getAllCells()
            .map { board.get(it) }
            .zipWithNext()
            .all { (it.second?: 16) - (it.first?: 0) == 1 }

    override fun processMove(direction: Direction) {
        val emptyCell: Cell = board.find { it == null }!!
        val neighbour = board.run { emptyCell.getNeighbour(direction.reversed()) }
        neighbour?.let {
            board.set(emptyCell, board.get(neighbour))
            board.set(neighbour, null)
        }
    }
}
