import util.solution
import java.io.File

fun main() {
    solution(::readInputFile, ::part1, ::part2) {
        solve("input/day03_example")
        solve("input/day03")
    }
}

private fun part1(batteryBank: List<BatteryBank>): Long = batteryBank.sumOf { it.calculateMaxJoltageOfTwo() }

private fun part2(batteryBank: List<BatteryBank>): Long =
    batteryBank.sumOf { it.calculateMaxJoltageOf(batteryCount = 12) }

private fun BatteryBank.calculateMaxJoltageOfTwo(): Long {
    var currentMax = 0
    var currentFirst = 0

    for (firstIndex in 0 until joltages.size - 1) {
        val first = joltages[firstIndex]
        if (first < currentFirst) {
            continue
        }
        currentFirst = first

        for (secondIndex in (firstIndex + 1) until joltages.size) {
            val joltage = first * 10 + joltages[secondIndex]
            if (joltage > currentMax) {
                currentMax = joltage
            }
        }
    }

    return currentMax.toLong()
}

private fun BatteryBank.calculateMaxJoltageOf(batteryCount: Int): Long =
    joltages.findGreatestDigitCombination(batteryCount)

private fun List<Int>.findGreatestDigitCombination(take: Int): Long = findGreatestDigitCombination(0..lastIndex, take)
    .concatDecimal()

private fun List<Int>.findGreatestDigitCombination(range: IntRange, take: Int): List<Int> {
    val possibleFirstDigitRange = range.start..(range.endInclusive - (take - 1))
    val (firstGreatestDigitIndex, digit) = findFirstGreatestDigitIndexedIn(possibleFirstDigitRange)
    if (take == 1) {
        return listOf(digit)
    }

    return listOf(digit) + findGreatestDigitCombination((firstGreatestDigitIndex + 1)..lastIndex, take - 1)
}

private fun List<Int>.findFirstGreatestDigitIndexedIn(range: IntRange): Pair<Int, Int> {
    var maxIndex = 0
    var maxValue = 0
    for (index in range) {
        val current = this[index]
        if (current > maxValue) {
            maxIndex = index
            maxValue = current
        }
    }
    return maxIndex to maxValue
}

private fun List<Int>.concatDecimal() = fold(0L) { acc, curr -> acc * 10L + curr.toLong() }

private fun readInputFile(filename: String): List<BatteryBank> {
    return mutableListOf<BatteryBank>().apply {
        File(filename).forEachLine { line ->
            if (line.isBlank()) {
                return@forEachLine
            }

            line.asSequence()
                .map { it.toString().toInt() }
                .toList()
                .let { add(BatteryBank(it)) }
        }
    }
}

private data class BatteryBank(val joltages: List<Int>)
