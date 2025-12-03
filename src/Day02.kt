import java.io.File

fun main() {
    solve("input/day02_example")
    solve("input/day02")
}

private fun solve(input: String) {
    println("Solving for $input...")
    val rotations = readInput(input)

    val result1 = part1(rotations)
    println("Part 1: $result1")

//    val result2 = part2(rotations)
//    println("Part 2: $result2")
}

private fun part1(input: List<LongRange>): Long {
    return input.asSequence()
        .map { range ->
//            println("Processing range ${range.start}-${range.endInclusive}")
            range.asSequence()
                .filter { it.isFromRepeatingDigits() }
//                .onEach { println("Invalid ID: $it") }
                .sum()
        }
        .sum()
}

private fun part2(input: List<LongRange>): Int {
    return 0
}

private fun Long.isFromRepeatingDigits(): Boolean = toString().isFromRepeatingDigits()

private fun String.isFromRepeatingDigits(): Boolean {
    val verifier = StringBuilder()
    do {
        verifier.append(this[verifier.length])
        if (this.isRepeatedFrom(verifier, times = 2)) return true
    } while (verifier.length < this.length / 2)

    return false
}

private fun String.isRepeatedFrom(part: CharSequence, times: Int): Boolean {
    if (this.length / part.length != times) {
        return false
    }

    if (this.length % part.length != 0) {
        return false
    }

    repeat(this.length) { index ->
        val partInt = index % part.length
        if (this[index] != part[partInt]) {
            return false
        }
    }

    return true
}

private fun readInput(input: String): List<LongRange> {
    return mutableListOf<LongRange>().apply {
        File(input).forEachLine { line ->
            line.splitToSequence(',')
                .filter { it.isNotBlank() }
                .forEach { range ->
                    val (begin, end) = range.splitToSequence('-')
                        .filter { it.isNotBlank() }
                        .map { it.toLong() }
                        .take(2)
                        .toList()
                    add(begin..end)
                }
        }
    }
}
