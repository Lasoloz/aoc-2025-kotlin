import util.solution
import java.io.File

fun main() {
    solution(::readInputFile, ::part1, ::part2) {
        solve("input/day08_example")
        solve("input/day08")
    }
}

private fun part1(coordinates: List<Coordinate>): Long {
    val coordinatesWithId = coordinates.mapIndexed { index, coordinate -> CoordinateWithId(index, coordinate) }
    val take = if (coordinates.size <= 20) 10 else 1000 // TODO: Move to solver abstraction
    val shortestPairs = coordinatesWithId.findFirstNShortestPairs(take)
    return calculateCircuitsOf(coordinatesWithId, shortestPairs).first.toLong()
}

private fun part2(coordinates: List<Coordinate>): Long {
    val coordinatesWithId = coordinates.mapIndexed { index, coordinate -> CoordinateWithId(index, coordinate) }
    val shortestPairs = coordinatesWithId.findFirstNShortestPairs(Int.MAX_VALUE)
    val lastPair = calculateCircuitsOf(coordinatesWithId, shortestPairs).second
    return coordinatesWithId[lastPair.id1].x.toLong() * coordinatesWithId[lastPair.id2].x.toLong()
    // This is a good reminder that I always shall start with Long responses, no matter what, and optimize down
    // if needed
}

private fun List<CoordinateWithId>.findFirstNShortestPairs(take: Int): Collection<PairDistanceSq> {
    val shortest = sortedSetOf<PairDistanceSq>()

    for ((i, first) in this.withIndex()) {
        for (j in (i + 1) until this.size) {
            val second = this[j]
            val distance = first distanceSq second
            if (shortest.size < take) {
                shortest.add(PairDistanceSq(i, j, distance))
                continue
            }

            val longest = shortest.last
            if (distance >= longest.distanceSq) {
                continue
            }

            shortest.remove(longest)
            shortest.add(PairDistanceSq(i, j, distance))
        }
    }

    return shortest
}

private fun calculateCircuitsOf(
    coordinatesWithId: List<CoordinateWithId>,
    shortestPairs: Collection<PairDistanceSq>
): Pair<Int, PairDistanceSq> {
    val unwired = coordinatesWithId.asSequence().map { it.id }.toMutableSet()
    val wires = mutableListOf<MutableSet<Int>>()

    var lastConnection = shortestPairs.first()

    for (pair in shortestPairs) {
        if (unwired.isEmpty()) {
            break
        }
        lastConnection = pair
        var unwiredFirst = false
        var unwiredSecond = false
        if (pair.id1 in unwired) {
            unwiredFirst = true
            unwired.remove(pair.id1)
        }
        if (pair.id2 in unwired) {
            unwiredSecond = true
            unwired.remove(pair.id2)
        }
        when {
            unwiredFirst && unwiredSecond -> wires.add(mutableSetOf(pair.id1, pair.id2))
            unwiredFirst && !unwiredSecond -> wires.find { pair.id2 in it }!!.add(pair.id1)
            !unwiredFirst && unwiredSecond -> wires.find { pair.id1 in it }!!.add(pair.id2)
            else -> {
                // merge wire sets
                val set1 = wires.find { pair.id1 in it }!!
                val set2 = wires.find { pair.id2 in it }!!
                if (set1 === set2) {
                    continue
                }
                wires.removeIf { pair.id2 in it }
                set1.addAll(set2)
            }
        }
    }

    return wires.sortedByDescending { it.size }.take(3).fold(1) { acc, set -> acc * set.size } to lastConnection
}

private infix fun CoordinateWithId.distanceSq(other: CoordinateWithId): Long =
    (x - other.x).sq() + (y - other.y).sq() + (z - other.z).sq()

private fun Int.sq(): Long = this.toLong() * this.toLong()

private fun readInputFile(filename: String): List<Coordinate> =
    File(filename).useLines { lines ->
        lines.mapNotNull { line ->
            when {
                line.isBlank() -> null
                else -> line.split(',').mapNotNull { it.toIntOrNull() }
            }
        }
            .filter { it.size >= 3 }
            .map { (x, y, z) -> Coordinate(x, y, z) }
            .toList()
    }

private data class Coordinate(val x: Int, val y: Int, val z: Int)
private data class CoordinateWithId(val id: Int, val x: Int, val y: Int, val z: Int) {
    constructor(id: Int, coordinate: Coordinate) : this(id, coordinate.x, coordinate.y, coordinate.z)
}

private data class PairDistanceSq(val id1: Int, val id2: Int, val distanceSq: Long) : Comparable<PairDistanceSq> {
    override fun compareTo(other: PairDistanceSq): Int = distanceSq.compareTo(other.distanceSq)
    override fun toString(): String = "{ $distanceSq; $id1, $id2 }"
}
