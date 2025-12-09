import util.Coordinate2
import util.asSvgPolygon
import util.asSvgRectangle
import util.solution
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

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

// Low answer: 1424092176
private fun part2(tileCoordinates: List<Coordinate2<Long>>): Long {
    return tileCoordinates.asSequence()
        .withIndex()
        .flatMap { (index, first) -> tileCoordinates.asSequence().drop(index + 1).map { first to it } }
        .filter { (first, second) -> tileCoordinates.isPossibleRectangle(first, second) }
        .map { (first, second) -> RectangleWithSize(first, second) }
        .maxBy { it.size }
        .also {
            println("Biggest rectangle: $it, ${it.size}")
            println("Max size: ${tileCoordinates.maxBy(Coordinate2<Long>::x)}, ${tileCoordinates.maxBy(Coordinate2<Long>::y)}")
            val (width, _) = tileCoordinates.maxBy(Coordinate2<Long>::x)
            val (_, height) = tileCoordinates.maxBy(Coordinate2<Long>::y)
            //language=SVG
            val svg = """
                <svg height="$height" width="$width" xmlns="http://www.w3.org/2000/svg">
                    ${tileCoordinates.asSvgPolygon()}
                    ${(it.start to it.end).asSvgRectangle()}
                    ${
                tileCoordinates.map { coord ->
                    "<rect x='${coord.x}' y='${coord.y}' width='1' height='1' style='stroke:blue;stroke-width:1'/>\n"
                }
            }
                </svg>
            """.trimIndent()
            File("misc/day09_withrect_v3.svg").writeText(svg)
        }
        .size
}

private infix fun Coordinate2<Long>.rectangleWith(other: Coordinate2<Long>): Long =
    (abs(x - other.x) + 1) * (abs(y - other.y) + 1)

private fun List<Coordinate2<Long>>.isPossibleRectangle(
    first: Coordinate2<Long>,
    second: Coordinate2<Long>
): Boolean =
    if (first.x == second.x || first.y == second.y) false
    else {
        val rect = Rectangle.normalized(first, second)
        (asSequence() + sequenceOf(first()))
            .zipWithNext()
            .all { (polyLineStart, polyLineEnd) -> !rect.crossedByLine(polyLineStart, polyLineEnd) }
    }

private fun Rectangle.crossedByLine(lineStart: Coordinate2<Long>, lineEnd: Coordinate2<Long>): Boolean =
    when {
        lineStart.x == lineEnd.x -> lineStart.x in betweenExclusive(start.x, end.x)
                && (
                start.y in betweenExclusive(lineStart.y, lineEnd.y)
                        || end.y in betweenExclusive(lineStart.y, lineEnd.y)
                        || (
                        lineStart.y in between(start.y, end.y)
                                && lineEnd.y in between(start.y, end.y)
                        )
                )

        lineStart.y == lineEnd.y -> lineStart.y in betweenExclusive(start.y, end.y)
                && (
                start.x in betweenExclusive(lineStart.x, lineEnd.x)
                        || end.x in betweenExclusive(lineStart.x, lineEnd.x)
                        || (
                        lineStart.x in between(start.x, end.x)
                                && lineEnd.x in between(start.x, end.x)
                        )
                )

        else -> {
            println("Warning: $lineStart, $lineEnd should not exist!")
            false
        }
    }

private fun between(first: Long, second: Long) =
    if (first < second) first..second
    else second..first

private fun betweenExclusive(left: Long, right: Long) =
    if (left < right) (left + 1) until right
    else (right + 1) until left

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
//        .also { coordinates ->
//            val svg = coordinates.drawAsPolygonToSvg()
//            File("$filename.svg").writeText(svg)
//        }
}

private data class Rectangle(val start: Coordinate2<Long>, val end: Coordinate2<Long>) {
    companion object {
        fun normalized(start: Coordinate2<Long>, end: Coordinate2<Long>): Rectangle =
            Rectangle(
                Coordinate2(min(start.x, end.x), min(start.y, end.y)),
                Coordinate2(max(start.x, end.x), max(start.y, end.y)),
            )
    }
}

private data class RectangleWithSize(val start: Coordinate2<Long>, val end: Coordinate2<Long>) {
    val size = start rectangleWith end
}
