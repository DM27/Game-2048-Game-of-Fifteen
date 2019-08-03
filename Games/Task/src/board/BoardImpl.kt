package board

import board.Direction.*
import java.lang.IllegalArgumentException

open class SquareBoardImpl(final override val width: Int) : SquareBoard {

    var cells : Array<Array<Cell>> = emptyArray()

    init {
        for (i in 1..width) {
            var array: Array<Cell> = emptyArray()
            for (j in 1..width) {
                array += Cell(i, j)
            }
            cells += array
        }
    }

    override fun getCellOrNull(i: Int, j: Int): Cell? =
            when {
                i > width || j > width || i == 0 || j == 0 -> null
                else -> cells[i - 1][j - 1]
            }

    override fun getCell(i: Int, j: Int): Cell = getCellOrNull(i, j)?: throw IllegalArgumentException()

    override fun getAllCells(): Collection<Cell> = IntRange(1, width)
            .flatMap { i: Int -> IntRange(1, width).map{ j:Int -> getCell(i,j) } }
            .toList()

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> = jRange
            .mapNotNull { getCellOrNull(i, it) }
            .toList()

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> = iRange
            .mapNotNull { getCellOrNull(it, j) }
            .toList()

    override fun Cell.getNeighbour(direction: Direction): Cell? =
            when (direction) {
                UP -> getCellOrNull(i - 1, j)
                LEFT -> getCellOrNull(i, j - 1)
                DOWN -> getCellOrNull(i + 1, j)
                RIGHT -> getCellOrNull(i, j + 1)
            }
}

class GameBoardImpl<T>(width: Int): SquareBoardImpl(width), GameBoard<T> {

    val cellValues = mutableMapOf<Cell, T?>()

    init {
        cells.forEach {
            it.forEach { cell: Cell ->
                cellValues += cell to null
            }
        }
    }

    override fun get(cell: Cell): T? = cellValues.get(cell)

    override fun set(cell: Cell, value: T?) {
        cellValues += cell to value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> = cellValues.filterValues { predicate.invoke(it) }.keys

    override fun find(predicate: (T?) -> Boolean): Cell? = cellValues.filter{ predicate.invoke(it.value) }.keys.first()

    override fun any(predicate: (T?) -> Boolean): Boolean = cellValues.values.any(predicate)

    override fun all(predicate: (T?) -> Boolean): Boolean = cellValues.values.all(predicate)
}

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)

fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(width)