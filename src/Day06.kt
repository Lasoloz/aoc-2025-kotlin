import util.solution
import java.io.File

fun main() {
    solution(::readInputFile, ::part1, ::part2) {
        solve("input/day06_example")
        solve("input/day06")
    }
}

private fun part1(problems: List<Problem>): Long {
    return problems.sumOf { problem ->
        when (problem.operation) {
            Problem.Operation.ADD -> problem.numbers.sumOf { it.toLong() }
            Problem.Operation.MULT -> problem.numbers.fold(1L) { acc, num -> acc * num.toLong() }
        }
    }
}

private fun part2(problems: List<Problem>): Long {
    return 0L
}

private fun readInputFile(filename: String): List<Problem> {
    val numbers = mutableListOf<List<Int>>()
    val operations = mutableListOf<Problem.Operation>()
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
                    "+" -> Problem.Operation.ADD
                    "*" -> Problem.Operation.MULT
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

data class Problem(val numbers: List<Int>, val operation: Operation) {
    enum class Operation {
        ADD,
        MULT,
    }
}
