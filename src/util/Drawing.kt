package util

import org.intellij.lang.annotations.Language
import kotlin.math.abs
import kotlin.math.min

fun List<Coordinate2<Long>>.drawAsPolygonToSvg(): String {
    val (width, _) = this.maxBy { it.x }
    val (_, height) = this.maxBy { it.y }

    //language=SVG
    return """
        <svg height="$height" width="$width" xmlns="http://www.w3.org/2000/svg">
          ${asSvgPolygon()}
        </svg> 
    """.trimIndent()
}

fun List<Coordinate2<Long>>.asSvgPolygon(): String {
    val polyPoints = joinToString(separator = " ") { (x, y) -> "$x,$y" }

    //language=SVG
    return """
        <polygon points="$polyPoints" style="fill:green;stroke:red;stroke-width:1" />
    """.trimIndent()
}

@Language("SVG")
fun Pair<Coordinate2<Long>, Coordinate2<Long>>.asSvgRectangle(): String = """
    <rect style='fill:white;stroke:purple;stroke-width:1'
          x='${min(first.x, second.x)}' y='${min(first.y, second.y)}' 
          width='${abs(first.x - second.x)}' height='${abs(first.y - second.y)}'
    />""".trimIndent()
