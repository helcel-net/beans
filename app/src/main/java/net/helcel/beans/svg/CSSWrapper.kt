package net.helcel.beans.svg

import android.content.Context
import net.helcel.beans.countries.World
import net.helcel.beans.helper.Data.groups
import net.helcel.beans.helper.Data.visits
import net.helcel.beans.helper.Theme.colorToHex6
import net.helcel.beans.helper.Theme.colorWrapper

class CSSWrapper(ctx: Context) {

    private val colorForeground: String =
        colorToHex6(colorWrapper(ctx, android.R.attr.panelColorBackground))
    private val colorBackground: String =
        colorToHex6(colorWrapper(ctx, android.R.attr.colorBackground))

    private val baseCSS: String
    private var customCSS: String = ""

    init {
        val www = World.WWW.children.joinToString(",") { "#${it.code}2" }
        val ccc = World.WWW.children.joinToString(",") { itt ->
            itt.children.joinToString(",") { "#${it.code}2" }
        }
        baseCSS = "svg{fill:$colorForeground;stroke:$colorBackground;stroke-width:0.01;}" +
                "$www,$ccc{stroke:$colorBackground;stroke-width:0.1;fill:none}"
        refresh()
    }

    fun refresh() {
        customCSS = visits.getVisitedByValue().map { (k, v) ->
            if (groups.getGroupFromKey(k).key == 0)
                ""
            else
                v.joinToString(",") { "#${it}1,#${it}" } + "{fill:${
                    colorToHex6(
                        groups.getGroupFromKey(
                            k
                        ).color
                    )
                };}"
        }.joinToString("")
    }

    fun get(): String {
        refresh()
        return baseCSS + customCSS
    }

}