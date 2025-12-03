import java.io.File

fun main() {
    solve("input/day01_example")
    solve("input/day01")
}

private fun solve(input: String) {
    println("Solving for $input...")
    val rotations = readInput(input)

    val result1 = part1(rotations)
    println("Part 1: $result1")

    val result2 = part2(rotations)
    println("Part 2: $result2")
}

private fun part1(rotations: List<Rotation>): Int {
    val state = DialState()
    var zeroStateCount = 0
    for (rotation in rotations) {
        state.applyRotation(rotation)
        if (state.dial == 0) {
            ++zeroStateCount
        }
    }
    return zeroStateCount
}

private fun part2(rotations: List<Rotation>): Int {
    val state = DialState()
    return rotations.asSequence()
        .map { state.applyRotation(it) }
        .sumOf { it.zeroClicks }
}

private fun readInput(input: String): List<Rotation> {
    return mutableListOf<Rotation>().apply {
        File(input).forEachLine { line ->
            if (line.isBlank()) {
                return@forEachLine
            }

            val direction = when (line.first()) {
                'L' -> Rotation.Direction.LEFT
                'R' -> Rotation.Direction.RIGHT
                else -> throw IllegalStateException("Incorrect input line: $line")
            }

            val amount = line.slice(1 until line.length).toInt()

            add(Rotation(direction, amount))
        }
    }
}

private data class Rotation(val direction: Direction, val amount: Int) {
    enum class Direction {
        LEFT,
        RIGHT,
    }
}

private class DialState {
    var dial = 50
        private set

    fun applyRotation(rotation: Rotation): RotationResult {
        val delta = when (rotation.direction) {
            Rotation.Direction.LEFT -> -1
            Rotation.Direction.RIGHT -> 1
        }

        var zeroClickCount = 0
        repeat(rotation.amount) {
            dial += delta
            when {
                dial % 100 == 0 -> ++zeroClickCount
                dial < 0 -> dial += 100
                dial > 99 -> dial -= 100
            }
        }
        return RotationResult(zeroClickCount)
    }

    data class RotationResult(val zeroClicks: Int)
}
