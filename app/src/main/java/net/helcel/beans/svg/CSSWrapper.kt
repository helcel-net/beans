package net.helcel.beans.svg

import android.content.Context
import net.helcel.beans.countries.World
import net.helcel.beans.helper.AUTO_GROUP
import net.helcel.beans.helper.Data.groups
import net.helcel.beans.helper.Data.visits
import net.helcel.beans.helper.NO_GROUP
import net.helcel.beans.helper.Settings
import net.helcel.beans.helper.Theme.colorToHex6
import net.helcel.beans.helper.Theme.colorWrapper

class CSSWrapper(private val ctx: Context) {

    private val colorForeground: String =
        colorToHex6(colorWrapper(ctx, android.R.attr.panelColorBackground))
    private val colorBackground: String =
        colorToHex6(colorWrapper(ctx, android.R.attr.colorBackground))

    private val continents: String = World.WWW.children.joinToString(",") { "#${it.code}2" }
    private val countries: String = World.WWW.children.joinToString(",") { itt ->
        itt.children.joinToString(",") { "#${it.code}2" }
    }
    private val regional: String = World.WWW.children.joinToString(",") { itt ->
        itt.children.joinToString(",") { "#${it.code}1" }
    }
    private val countryOnlyCSS: String =
        "svg{fill:$colorForeground;stroke:$colorBackground;stroke-width:0.1;}" +
                "${regional}{display:none;}"
    private val countryRegionalCSS: String =
        "svg{fill:$colorForeground;stroke:$colorBackground;stroke-width:0.01;}" +
                "$continents,$countries{fill:none;stroke:$colorBackground;stroke-width:0.1;}"
    private var customCSS: String = ""

    init {
        refresh()
    }

    private fun refresh() {
        val id = if (Settings.isRegional(ctx)) "1" else "2"
        customCSS = visits.getVisitedByValue().map { (k, v) ->
            (if (groups.getGroupFromKey(k).key != NO_GROUP) {
                v
            } else if (!Settings.isRegional(ctx) && k == AUTO_GROUP) {
                v.filter { it !in World.WWW.children.map { it1 -> it1.code } }
            } else {
                emptyList()
            }).takeIf { it.isNotEmpty() }
                ?.joinToString(",") { "#${it}$id,#${it}" } + "{fill:${
                colorToHex6(
                    if (k == AUTO_GROUP)
                        colorWrapper(ctx, android.R.attr.colorPrimary)
                    else groups.getGroupFromKey(k).color
                )
            };}"
        }.joinToString("")
    }

    fun get(): String {
        refresh()
        return if (Settings.isRegional(ctx)) {
            countryRegionalCSS + customCSS
        } else {
            countryOnlyCSS + customCSS
        }
    }

}