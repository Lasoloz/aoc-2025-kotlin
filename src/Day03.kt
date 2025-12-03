import util.solution
import java.io.File

fun main() {
    solution(::readInputFile, ::part1, ::part2) {
        solve("input/day03_example")
        solve("input/day03")
    }
}

private fun part1(batteryBank: List<BatteryBank>): Int {
    return batteryBank.sumOf { it.calculateMaxJoltage() }
}

private fun part2(batteryBank: List<BatteryBank>): Int {
    return 0
}

private fun BatteryBank.calculateMaxJoltage(): Int {
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

    return currentMax
}

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
