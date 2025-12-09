package util

fun List<Coordinate2<Long>>.drawAsPolygonToSvg(): String {
    val (width, _) = this.maxBy { it.x }
    val (_, height) = this.maxBy { it.y }

    val polyPoints = joinToString(separator = " ") { (x, y) -> "$x,$y" }

    //language=SVG
    return """
        <svg height="$height" width="$width" xmlns="http://www.w3.org/2000/svg">
          <polygon points="$polyPoints" style="fill:green;stroke:red;stroke-width:1" />
        </svg> 
    """.trimIndent()
}
