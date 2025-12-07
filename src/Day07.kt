import util.solution
import java.io.File

fun main() {
    solution(::readInputFile, ::part1, ::part2) {
        solve("input/day07_example")
        solve("input/day07")
    }
}

private fun part1(splitters: TachyonSplitters): Int {
    return splitters.performBeamSplitting().splits
}

private fun part2(splitters: TachyonSplitters): Int {
    return 0
}

private fun TachyonSplitters.performBeamSplitting(): SplitState =
    layers.fold(SplitState.start(start)) { acc, layer ->
        SplitState(
            beams = acc.beams.flatMap { beam ->
                when {
                    beam in layer -> listOf(beam - 1, beam + 1)
                    else -> listOf(beam)
                }
            }
                .distinct()
                .toList(),
            splits = acc.splits + acc.beams.count { it in layer }
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

private data class SplitState(val beams: List<Int>, val splits: Int) {
    companion object {
        fun start(beam: Int) = SplitState(beams = listOf(beam), splits = 0)
    }
}
