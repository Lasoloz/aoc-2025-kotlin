import util.solution
import java.io.File

fun main() {
    solution(::readInputFile, ::part1, ::part2) {
        solve("input/day07_example")
        solve("input/day07")
    }
}

private fun part1(splitters: TachyonSplitters): Long {
    return splitters.performBeamSplitting().splits.toLong()
}

private fun part2(splitters: TachyonSplitters): Long {
    return splitters.runBeamTimelines().timelineBeamCount.values.sum()
}

private fun TachyonSplitters.performBeamSplitting(): SplitState =
    layers.fold(SplitState.start(start)) { acc, layer ->
        SplitState(
            beams = acc.beams.flatMapTo(mutableSetOf()) { beam ->
                when {
                    beam in layer -> setOf(beam - 1, beam + 1)
                    else -> setOf(beam)
                }
            },
            splits = acc.splits + acc.beams.count { it in layer },
        )
    }

private fun TachyonSplitters.runBeamTimelines(): TimelineState =
    layers.fold(TimelineState.start(start)) { acc, layer ->
        TimelineState(
            timelineBeamCount = acc.timelineBeamCount.flatMap { (beam, count) ->
                when {
                    (beam in layer) -> listOf((beam - 1) to count, (beam + 1) to count)
                    else -> listOf(beam to count)
                }
            }
                .groupBy { it.first }
                .mapValues { beamTimelines -> beamTimelines.value.sumOf { it.second } }
        )
    }

private fun readInputFile(filename: String): TachyonSplitters {
    return File(filename).useLines { lines ->
        lines.iterator().let { iterator ->
            val start = iterator.readStart()
            val layers = iterator.readLayers()

            TachyonSplitters(start, layers)
        }
    }
}

private fun Iterator<String>.readLayers() = asSequence()
    .map { line -> line.asSequence().withIndex().filter { it.value == '^' }.map { it.index }.toList() }
    .filter { it.isNotEmpty() }
    .toList()

private fun Iterator<String>.readStart() = next().indexOf('S')

private data class TachyonSplitters(val start: Int, val layers: List<List<Int>>)

private data class SplitState(val beams: Set<Int>, val splits: Int = 0) {
    companion object {
        fun start(beam: Int) = SplitState(beams = setOf(beam))
    }
}

private typealias TimelineBeamCount = Map<Int, Long>

private data class TimelineState(val timelineBeamCount: TimelineBeamCount) {
    companion object {
        fun start(beam: Int) = TimelineState(timelineBeamCount = mapOf(beam to 1L))
    }
}
