import util.solution
import java.io.File

fun main() {
    solution(::readInputFile, ::part1, ::part2) {
        solve("input/day06_example")
        solve("input/day06")
    }
}

private fun part1(worksheet: CombinedWorksheet): Long = solveProblem(worksheet.problems1)

private fun part2(worksheet: CombinedWorksheet): Long = solveProblem(worksheet.problems2)

private fun solveProblem(problems: List<Problem>): Long {
    return problems.sumOf { problem ->
        when (problem.operation) {
            Operation.ADD -> problem.numbers.sumOf { it.toLong() }
            Operation.MULT -> problem.numbers.fold(1L) { acc, num -> acc * num.toLong() }
        }
    }
}

private fun readInputFile(filename: String): CombinedWorksheet {
    return CombinedWorksheet(readPart1Structure(filename), readPart2Structure(filename))
}

private fun readPart1Structure(filename: String): List<Problem> {
    val numbers = mutableListOf<List<Int>>()
    val operations = mutableListOf<Operation>()
    var expectedWidth = 0

    File(filename).forEachLine { line ->
        val elements = line.split(' ').filter { it.isNotBlank() }
        if (expectedWidth == 0) {
            expectedWidth = elements.size
        } else if (expectedWidth != elements.size) {
            throw IllegalStateException("Row elements must have the same width (incorrect input data)!")
        }

        val possiblyNumbers = elements.mapNotNull { it.toIntOrNull() }
        if (possiblyNumbers.size == elements.size) {
            numbers.add(possiblyNumbers)
        } else {
            operations.addAll(elements.asSequence().map {
                when (it) {
                    "+" -> Operation.ADD
                    "*" -> Operation.MULT
                    else -> throw IllegalStateException("Incorrect operation!")
                }
            })
        }
    }

    if (operations.size != expectedWidth) {
        throw IllegalStateException("Not enough operations")
    }

    return (0 until expectedWidth).map { index ->
        Problem(
            numbers.map { row -> row[index] },
            operations[index]
        )
    }
}

private fun readPart2Structure(filename: String): List<Problem> {
    val lines = File(filename).readLines()
    val maxWidth = lines.maxOf { it.length }
    val worksheetMatrix = lines.map { it.padEnd(maxWidth, ' ').toCharArray() }

    val columnRanges = readChunkRanges(worksheetMatrix)

    return columnRanges.map { range ->
        Problem(
            worksheetMatrix.parseNumbersFromRange(range),
            worksheetMatrix.parseOperationFromRange(range),
        )
    }
}

private fun List<CharArray>.parseNumbersFromRange(range: IntProgression): List<Int> =
    range.map { index ->
        mapNotNull { it[index].digitToIntOrNull() }
            .reduce { acc, current -> acc * 10 + current }
    }

private fun List<CharArray>.parseOperationFromRange(range: IntProgression): Operation =
    when (last()[range.last]) {
        '+' -> Operation.ADD
        '*' -> Operation.MULT
        else -> throw IllegalStateException("Incorrect operation!")
    }

private fun readChunkRanges(worksheetMatrix: List<CharArray>): List<IntProgression> {
    val indexSequence = worksheetMatrix.last().asSequence()
        .withIndex()
        .filter { (_, ch) -> !ch.isWhitespace() }
        .map { (index, _) -> index }
    return (indexSequence + sequenceOf(worksheetMatrix.last().size + 1))
        .zipWithNext()
        .map { (first, second) -> (second - 2) downTo first }
        .toList()
}

private data class Problem(val numbers: List<Int>, val operation: Operation)

private data class CombinedWorksheet(val problems1: List<Problem>, val problems2: List<Problem>)

private enum class Operation {
    ADD,
    MULT,
}
