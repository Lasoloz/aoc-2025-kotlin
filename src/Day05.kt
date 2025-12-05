import util.solution
import java.io.File

fun main() {
    solution(::readInputFile, ::part1, ::part2) {
        solve("input/day05_example")
        solve("input/day05")
    }
}

private fun part1(cafeteria: Cafeteria): Long {
    return cafeteria.ingredients.count { ingredient -> cafeteria.freshRanges.any { range -> ingredient in range } }
        .toLong()
}

private fun part2(cafeteria: Cafeteria): Long {
    val resultingRanges = cafeteria.mergeRanges()
    // LongRange count is throwing arithmetic overflow exception, that's why we count manually
    return resultingRanges.sumOf { it.endInclusive - it.start + 1L }
}

private fun Cafeteria.mergeRanges(): Set<LongRange> {
    val resultingRanges = mutableSetOf<LongRange>()
    for (range in freshRanges) {
        val mergeableRanges = resultingRanges.filter { range mergesWith it }
        resultingRanges.removeAll(mergeableRanges)
        val merged = mergeableRanges.fold(range) { acc, current -> acc mergeWith current }
        resultingRanges.add(merged)
    }
    return resultingRanges
}

private infix fun LongRange.mergesWith(other: LongRange): Boolean =
    this mergesBefore other || other mergesBefore this || this mergesInside other || other mergesInside this

private infix fun LongRange.mergesBefore(other: LongRange): Boolean =
    this.start < other.start && this.endInclusive >= other.start

private infix fun LongRange.mergesInside(other: LongRange): Boolean =
    this.start >= other.start && this.endInclusive <= other.endInclusive

private infix fun LongRange.mergeWith(other: LongRange): LongRange = when {
    this mergesInside other -> other
    other mergesInside this -> this
    this mergesBefore other -> this.start..other.endInclusive
    other mergesBefore this -> other.start..this.endInclusive
    else -> throw IllegalArgumentException("Ranges $this and $other cannot be merged!")
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
