package net.helcel.beans.map

import android.graphics.Path
import android.graphics.RectF
import kotlin.math.sqrt

/**
 * One named piece of map geometry: a country outline or a single region.
 *
 * The geometry is kept twice on purpose. [nonZero] and [evenOdd] are what the
 * renderer hands to the canvas, while [rings] keeps the raw points because
 * hit testing needs the distance to an edge, which a [Path] cannot give us.
 */
class MapShape(
    val code: String,
    val isState: Boolean,
    val rings: List<FloatArray>,
    val bounds: RectF,
    val nonZero: Path?,
    val evenOdd: Path?,
) {

    /** True when ([x], [y]) falls inside the shape. Holes and lakes count as outside. */
    fun contains(x: Float, y: Float): Boolean {
        if (x < bounds.left || x > bounds.right || y < bounds.top || y > bounds.bottom) return false
        // Even-odd crossing count over every ring at once: rings never overlap, so
        // an island still reads as inside and a hole still reads as outside.
        var inside = false
        for (ring in rings) {
            var j = ring.size - 2
            var i = 0
            while (i < ring.size) {
                val yi = ring[i + 1]
                val yj = ring[j + 1]
                if ((yi > y) != (yj > y)) {
                    val xi = ring[i]
                    val xj = ring[j]
                    if (x < xi + (y - yi) * (xj - xi) / (yj - yi)) inside = !inside
                }
                j = i
                i += 2
            }
        }
        return inside
    }

    /** Distance from ([x], [y]) to the shape, `0` when the point is inside it. */
    fun distanceTo(x: Float, y: Float): Float {
        if (contains(x, y)) return 0f
        var best = Float.MAX_VALUE
        for (ring in rings) {
            var j = ring.size - 2
            var i = 0
            while (i < ring.size) {
                val d = segmentDistanceSq(x, y, ring[j], ring[j + 1], ring[i], ring[i + 1])
                if (d < best) best = d
                j = i
                i += 2
            }
        }
        return if (best == Float.MAX_VALUE) Float.MAX_VALUE else sqrt(best)
    }

    private fun segmentDistanceSq(
        px: Float, py: Float,
        ax: Float, ay: Float,
        bx: Float, by: Float,
    ): Float {
        val dx = bx - ax
        val dy = by - ay
        val len = dx * dx + dy * dy
        val t = if (len <= 0f) 0f else (((px - ax) * dx + (py - ay) * dy) / len).coerceIn(0f, 1f)
        val cx = px - (ax + t * dx)
        val cy = py - (ay + t * dy)
        return cx * cx + cy * cy
    }
}

/** The whole map, in SVG user units, as a flat set of named shapes. */
class MapWorld(val width: Float, val height: Float, val shapes: List<MapShape>) {

    val byCode: Map<String, MapShape> = shapes.associateBy { it.code }

    /** Country outlines, keyed by ISO code. Drawn as borders when regions are on. */
    val countries: List<MapShape> = shapes.filter { !it.isState }
}
