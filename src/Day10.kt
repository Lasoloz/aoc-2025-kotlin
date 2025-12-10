import util.solution
import java.io.File

fun main() {
    solution(::readInputFile, ::part1, ::part2) {
        solve("input/day10_example")
        solve("input/day10")
    }
}

private fun part1(machines: List<Machine>): Long {
    return machines.asSequence()
        .map { it.findOptimalSwitchingByCost() }
        .map { it.size }
        .sumOf { it.toLong() }
}

private fun part2(machines: List<Machine>): Long {
    return 0
}

private typealias CostFn = (machine: Machine, switchIndex: Int) -> Int

private fun Machine.findOptimalSwitchingByCost(costFn: CostFn = { _, _ -> 1 }): List<Set<Int>> {
    val startIndicators = List(expectedIndicators.size) { IndicatorState.OFF }
    val startCounts = switchSets.mapIndexed { index, _ -> index to 0 }.toMap()
    val startState = CostLookupState(
        startIndicators,
        lookupCounts = startCounts,
        optimalCost = Int.MAX_VALUE,
        costBuildup = 0,
    )
    return findOptimalSwitchingByCost(startState, costFn)
        .also { optimal -> println("Switching: $optimal"); System.out.flush() }
        .switches
}

private fun Machine.findOptimalSwitchingByCost(
    currentState: CostLookupState,
    costFn: (Machine, Int) -> Int
): CostLookupResult {
    if (currentState.indicators == expectedIndicators) {
        return CostLookupResult(0, emptyList())
    }

    var bestResult = CostLookupResult(Int.MAX_VALUE, emptyList())

    val bestSwitches = switchSets.withIndex().sortedBy { (index, _) -> currentState.lookupCounts[index] }
    for ((index, switchSet) in bestSwitches) {
        if (currentState.lookupCounts[index]!! > 0) {
            continue
        }

        val costBuildup = currentState.costBuildup + costFn(this, index)
        if (costBuildup > currentState.optimalCost) {
            continue
        }

        val result = findOptimalSwitchingByCost(
            currentState.copy(
                indicators = currentState.indicators.applySwitches(switchSet),
                lookupCounts = currentState.lookupCounts.countSwitch(index),
                optimalCost = bestResult.currentCost,
                costBuildup = costBuildup,
            ),
            costFn
        ).applyCurrentCost(costFn(this, index), switchSet)

        if (result.currentCost < bestResult.currentCost) {
            bestResult = result
        }
    }

    return bestResult
}

private fun List<IndicatorState>.applySwitches(switchSet: Set<Int>): List<IndicatorState> = mapIndexed { index, state ->
    if (index in switchSet) state.toggle()
    else state
}

private fun Map<Int, Int>.countSwitch(index: Int): Map<Int, Int> = mapValues { (key, value) ->
    if (key == index) value + 1
    else value
}

private fun CostLookupResult.applyCurrentCost(cost: Int, switchSet: Set<Int>): CostLookupResult = copy(
    currentCost = if (this.currentCost == Int.MAX_VALUE) Int.MAX_VALUE else this.currentCost + cost,
    switches = listOf(switchSet) + this.switches
)

private fun readInputFile(filename: String): List<Machine> =
    File(filename).useLines { lines ->
        lines.map { line ->
            val parts = line.split(' ')
            val expectedIndicators = parts.first().parseExpectedIndicators()
            val switchSets = parts.slice(1 until parts.size - 1).parseSwitchSets()
            val likelySwitchSets = switchSets.sortedByDescending { switch ->
                expectedIndicators.mapIndexed { index, _ -> if (index in switch) 1 else 0 }
                    .count()
            }
            val joltages = parts.last().parseJoltages()

            Machine(expectedIndicators, likelySwitchSets, joltages)
        }
            .toList()
    }

private fun String.parseExpectedIndicators(): List<IndicatorState> =
    mapNotNull {
        when (it) {
            '#' -> IndicatorState.ON
            '.' -> IndicatorState.OFF
            '[', ']' -> null
            else -> {
                println("Badly read data? Indicator state contains $it")
                null
            }
        }
    }

private fun List<String>.parseSwitchSets(): List<Set<Int>> = mapNotNull { rawSet ->
    rawSet.slice(1 until rawSet.length - 1)
        .split(',')
        .map { it.toInt() }
        .toSet()
}

private fun String.parseJoltages(): List<Int> =
    slice(1 until length - 1)
        .split(',')
        .map { it.toInt() }

private data class Machine(
    val expectedIndicators: List<IndicatorState>,
    val switchSets: List<Set<Int>>,
    val joltages: List<Int>
)

private enum class IndicatorState {
    OFF,
    ON;

    fun toggle(): IndicatorState = when (this) {
        OFF -> ON
        ON -> OFF
    }
}

private data class CostLookupResult(val currentCost: Int, val switches: List<Set<Int>>)
private data class CostLookupState(
    val indicators: List<IndicatorState>,
    val lookupCounts: Map<Int, Int>,
    val optimalCost: Int,
    val costBuildup: Int,
)
