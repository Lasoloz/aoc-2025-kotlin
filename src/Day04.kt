import util.solution
import java.io.File

fun main() {
    solution(::readInputFile, ::part1, ::part2) {
        solve("input/day04_example")
        solve("input/day04")
    }
}

private val deltas = listOf(-1 to -1, -1 to 0, -1 to 1, 0 to 1, 1 to 1, 1 to 0, 1 to -1, 0 to -1)

private fun part1(storage: PaperStorage): Int = storage.buildNewLayout().second

private fun part2(storage: PaperStorage): Int {
    var totalChanges = 0
    var isChanging = true
    var currentStorage = storage
    while (isChanging) {
        val (nextStorage, changes) = currentStorage.buildNewLayout()
        isChanging = changes > 0
        currentStorage = nextStorage
        totalChanges += changes
    }
    return totalChanges
}

private fun PaperStorage.buildNewLayout(): Pair<PaperStorage, Int> {
    var changed = 0
    val resultLayout = layout.mapIndexed { cy, line ->
        line.mapIndexed { cx, element ->
            when (element) {
                PaperStorage.UnitState.EMPTY, PaperStorage.UnitState.EMPTIED -> element
                else -> {
                    if (isChangingElement(cx, cy)) {
                        ++changed
                        PaperStorage.UnitState.EMPTIED
                    } else {
                        PaperStorage.UnitState.ROLL
                    }
                }
            }
        }
    }
    return copy(layout = resultLayout) to changed
}

private fun PaperStorage.isChangingElement(cx: Int, cy: Int): Boolean {
    var neighborCount = 0
    for ((dy, dx) in deltas) {
        val y = cy + dy
        val x = cx + dx
        if (y !in 0 until height || x !in 0 until width) {
            continue
        }
        if (layout[y][x] == PaperStorage.UnitState.ROLL) {
            ++neighborCount
        }
    }
    return neighborCount < 4
}

private fun readInputFile(filename: String): PaperStorage {
    val layout = mutableListOf<List<PaperStorage.UnitState>>()
    var width = 0

    File(filename).forEachLine { line ->
        if (line.isBlank()) {
            return@forEachLine
        }

        if (width == 0) {
            width = line.length
        }

        if (line.length != width) {
            throw IllegalStateException("Line length is incorrect")
        }

        val unitState = line.map {
            when (it) {
                '@' -> PaperStorage.UnitState.ROLL
                '.' -> PaperStorage.UnitState.EMPTY
                else -> throw IllegalStateException("Incorrect character $it")
            }
        }

        layout.add(unitState)
    }

    return PaperStorage(layout, width)
}

data class PaperStorage(val layout: List<List<UnitState>>, val width: Int) {
    val height = layout.size

    enum class UnitState {
        EMPTY,
        ROLL,
        EMPTIED;

        override fun toString(): String = when (this) {
            EMPTY -> "."
            ROLL -> "@"
            EMPTIED -> "x"
        }
    }

    override fun toString(): String {
        return "PaperStorage($width, $height)[\n" +
                layout.joinToString(separator = "\n") { inner -> inner.joinToString(separator = "") }
                    .prependIndent("  ") +
                "\n]"
    }
}
