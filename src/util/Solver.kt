package util

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

interface Solver<I, O : Number> {
    fun solve(filename: String) {
        println("Solving for $filename...")
        val rotations = readInput(filename)

        val (result1, time1) = measureTimedValue { part1(rotations) }
        println("Part 1 [${time1.formattedForSolver()}]: $result1")

        val (result2, time2) = measureTimedValue { part2(rotations) }
        println("Part 2 [${time2.formattedForSolver()}]: $result2")
    }

    fun readInput(filename: String): I
    fun part1(input: I): O
    fun part2(input: I): O
}

private fun Duration.formattedForSolver() =
    toString(unit = DurationUnit.MILLISECONDS, decimals = 2).padStart(10, padChar = ' ')

fun <I, O : Number> solution(
    readInput: (String) -> I,
    part1: (I) -> O,
    part2: (I) -> O,
    solveBlock: Solver<I, O>.() -> Unit
) {
    object : Solver<I, O> {
        override fun readInput(filename: String): I = readInput(filename)
        override fun part1(input: I): O = part1(input)
        override fun part2(input: I): O = part2(input)
    }
        .solveBlock()
}
