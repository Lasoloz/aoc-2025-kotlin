import util.solution
import java.io.File

fun main() {
    solution(::readInputFile, ::part1, ::part2) {
        solve("input/day02_example")
        solve("input/day02")
    }
}

private fun part1(input: List<LongRange>): Long {
    return input.asSequence()
        .map { range ->
            range.asSequence()
                .filter { it.isFromRepeatingDigits(times = 2) }
                .sum()
        }
        .sum()
}

private fun part2(input: List<LongRange>): Long {
    return input.asSequence()
        .map { range ->
            range.asSequence()
                .filter { it.isFromRepeatingDigits() }
                .sum()
        }
        .sum()
}

private fun Long.isFromRepeatingDigits(times: Int? = null): Boolean = toString().isFromRepeatingDigits(times)

private fun String.isFromRepeatingDigits(times: Int? = null): Boolean {
    val verifier = StringBuilder()
    do {
        verifier.append(this[verifier.length])
        if (this.isRepeatedFrom(verifier, times)) return true
    } while (verifier.length < this.length / 2)

    return false
}

private fun String.isRepeatedFrom(part: CharSequence, times: Int? = null): Boolean {
    if (times != null && this.length / part.length != times) {
        return false
    }

    if (this.length / part.length < 2) {
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

private fun readInputFile(filename: String): List<LongRange> {
    return mutableListOf<LongRange>().apply {
        File(filename).forEachLine { line ->
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
