import util.Coordinate2
import util.solution
import java.io.File
import kotlin.math.abs

fun main() {
    solution(::readInputFile, ::part1, ::part2) {
        solve("input/day09_example")
        solve("input/day09")
    }
}

private fun part1(tileCoordinates: List<Coordinate2<Long>>): Long {
    return tileCoordinates.asSequence()
        .flatMap { first -> tileCoordinates.asSequence().map { first to it } }
        .map { (first, second) -> first rectangleWith second }
        .max()
}

private fun part2(tileCoordinates: List<Coordinate2<Long>>): Long {
    return 0L
}

private infix fun Coordinate2<Long>.rectangleWith(other: Coordinate2<Long>): Long =
    (abs(x - other.x) + 1) * (abs(y - other.y) + 1)

private fun readInputFile(filename: String): List<Coordinate2<Long>> {
    return File(filename).useLines { lines ->
        lines.filter { it.isNotBlank() }
            .map { line ->
                val (x, y) = line.split(',')
                    .mapNotNull { it.toLongOrNull() }
                Coordinate2(x, y)
            }
            .toList()
    }
}
