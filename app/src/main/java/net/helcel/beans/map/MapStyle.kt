package net.helcel.beans.map

import android.content.Context
import net.helcel.beans.countries.GeoLoc
import net.helcel.beans.countries.World
import net.helcel.beans.helper.AUTO_GROUP
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.NO_GROUP
import net.helcel.beans.helper.Settings
import kotlin.math.ln

/**
 * Border width in device pixels at each zoom, interpolated in between.
 *
 * Zoom counts how far in the map is from its fitted size, so 1 is the whole
 * world on screen and 64 is as far in as it goes. The stops step by fours and
 * are interpolated on a log axis, so the weight changes evenly as you pinch
 * rather than in a rush at one end.
 *
 * Region borders start at nothing on purpose: a sub-national border says very
 * little with the whole world in view, and drawing three and a half thousand of
 * them only muddies the land.
 *
 *   zoom     1x     4x    16x    64x
 *   region  0.0    0.5    1.0    2.0
 *   country 1.0    1.5    3.0    6.0
 */
val STROKE_ZOOM_STOPS = floatArrayOf(1f, 4f, 16f, 64f)

val REGION_STROKE_STOPS = floatArrayOf(0f, 0.5f, 1f, 2f)

val COUNTRY_STROKE_STOPS = floatArrayOf(1f, 1.5f, 3f, 6f)

/** The width for [zoom], straight-line between the stops on a log axis. */
fun strokeWidthAt(zoom: Float, widths: FloatArray): Float {
    if (zoom <= STROKE_ZOOM_STOPS.first()) return widths.first()
    for (i in 1 until STROKE_ZOOM_STOPS.size) {
        if (zoom <= STROKE_ZOOM_STOPS[i]) {
            val low = STROKE_ZOOM_STOPS[i - 1]
            val t = ln(zoom / low) / ln(STROKE_ZOOM_STOPS[i] / low)
            return widths[i - 1] + t * (widths[i] - widths[i - 1])
        }
    }
    return widths.last()
}

/**
 * The colour every shape is drawn with, resolved once per render.
 *
 * This replaces the stylesheet the map used to be rendered through. A place
 * inherits the colour of its parent, so painting a whole continent still shows
 * up on each country inside it, and [fills] ends up holding exactly the shapes
 * that get drawn — which is also exactly what a tap may land on.
 */
class MapStyle(
    val regional: Boolean,
    val land: Int,
    val background: Int,
    val fills: Map<String, Int>,
) {

    companion object {

        fun build(ctx: Context, land: Int, background: Int): MapStyle {
            val regional = Settings.isRegional(ctx)
            val fills = HashMap<String, Int>()
            World.WWW.children.forEach { child ->
                if (child.type == GeoLoc.LocType.COUNTRY) {
                    // A country hanging straight off the world, such as Antarctica.
                    addCountry(fills, child, colorOf(World.WWW, regional), regional, land)
                } else {
                    val continent = colorOf(child, regional) ?: colorOf(World.WWW, regional)
                    child.children.forEach { addCountry(fills, it, continent, regional, land) }
                }
            }
            return MapStyle(regional, land, background, fills)
        }

        private fun addCountry(
            fills: HashMap<String, Int>,
            country: GeoLoc,
            inherited: Int?,
            regional: Boolean,
            land: Int,
        ) {
            val color = colorOf(country, regional) ?: inherited
            // Regions are only drawn for countries that actually have some; the
            // rest keep their outline filled so the map stays complete.
            if (regional && country.children.isNotEmpty()) {
                country.children.forEach { state ->
                    fills[state.code] = colorOf(state, regional) ?: color ?: land
                }
            } else {
                fills[country.code] = color ?: land
            }
        }

        /** The group colour assigned to [loc], or `null` when it has none of its own. */
        private fun colorOf(loc: GeoLoc, regional: Boolean): Int? {
            val key = Data.visits.getVisited(loc)
            val group = Data.groups.getGroupFromKey(key)
            if (group.key != NO_GROUP) return group.color.color
            // A place marked automatically stands in for its children. That only
            // needs a colour of its own where the children are not drawn, and
            // never on a continent, which would swallow the whole map.
            val isParent = loc.type == GeoLoc.LocType.GROUP ||
                    loc.type == GeoLoc.LocType.CUSTOM_GROUP ||
                    loc.type == GeoLoc.LocType.WORLD
            if (key == AUTO_GROUP && !regional && !isParent) {
                return Data.groups.getGroupFromPos(0).second.color.color
            }
            return null
        }
    }
}
