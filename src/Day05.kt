import util.solution
import java.io.File

fun main() {
    solution(::readInputFile, ::part1, ::part2) {
        solve("input/day05_example")
        solve("input/day05")
    }
}

private fun part1(cafeteria: Cafeteria): Int {
    return cafeteria.ingredients.count { ingredient -> cafeteria.freshRanges.any { range -> ingredient in range }}
}

private fun part2(cafeteria: Cafeteria): Int {
    return 0
}

private fun readInputFile(filename: String): Cafeteria {
    val freshRanges = mutableListOf<LongRange>()
    val ingredients = mutableListOf<Long>()
    var readRanges = true

    File(filename).forEachLine { line ->
        if (line.isBlank()) {
            readRanges = false
            return@forEachLine
        }

        if (readRanges) {
            val (first, second) = line.split('-')
                .take(2)
                .map { it.toLong() }

            freshRanges.add(first..second)
        } else {
            val ingredient = line.toLong()
            ingredients.add(ingredient)
        }
    }

    return Cafeteria(freshRanges, ingredients)
}

private data class Cafeteria(val freshRanges: List<LongRange>, val ingredients: List<Long>)
