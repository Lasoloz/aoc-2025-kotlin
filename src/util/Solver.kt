package util

interface Solver<I, O : Number> {
    fun solve(filename: String) {
        println("Solving for $filename...")
        val rotations = readInput(filename)

        val result1 = part1(rotations)
        println("Part 1: $result1")

        val result2 = part2(rotations)
        println("Part 2: $result2")
    }

    fun readInput(filename: String): I
    fun part1(input: I): O
    fun part2(input: I): O
}

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
