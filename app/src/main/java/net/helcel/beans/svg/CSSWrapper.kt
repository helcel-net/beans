package net.helcel.beans.svg

import android.content.Context
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toDrawable
import net.helcel.beans.countries.World
import net.helcel.beans.helper.AUTO_GROUP
import net.helcel.beans.helper.Data.groups
import net.helcel.beans.helper.Data.visits
import net.helcel.beans.helper.NO_GROUP
import net.helcel.beans.helper.Settings
import net.helcel.beans.helper.Theme.colorToHex6


class CSSWrapper(private val ctx: Context) {

    private val continents: String = World.WWW.children.joinToString(",") { "#${it.code}2" }
    private val countries: String = World.WWW.children.joinToString(",") { itt ->
        itt.children.joinToString(",") { "#${it.code}2" }
    }
    private val regional: String = World.WWW.children.joinToString(",") { itt ->
        itt.children.joinToString(",") { "#${it.code}1" }
    }

    @Composable
    fun getBaseColors() : Pair<String, String> {
        val colorForeground = colorToHex6(MaterialTheme.colors.onBackground.toArgb().toDrawable())
        val colorBackground = colorToHex6(MaterialTheme.colors.background.toArgb().toDrawable())

        return Pair(colorForeground, colorBackground)
    }

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
                if (k == AUTO_GROUP) colorToHex6(groups.getGroupFromPos(0).second.color) 
                else colorToHex6(groups.getGroupFromKey(k).color)
            };}"
        }.joinToString("")
    }
    @Composable
    fun get(): String {
        val (colorForeground,colorBackground) = getBaseColors()
        refresh()
        return if (Settings.isRegional(ctx)) {
            val countryRegionalCSS: String =
                "svg{fill:$colorForeground;stroke:$colorBackground;stroke-width:0.01;}" +
                        "$continents,$countries{fill:none;stroke:$colorBackground;stroke-width:0.1;}"
            countryRegionalCSS + customCSS
        } else {
            val countryOnlyCSS: String =
                "svg{fill:$colorForeground;stroke:$colorBackground;stroke-width:0.1;}" +
                        "${regional}{display:none;}"
            countryOnlyCSS + customCSS
        }
    }

}