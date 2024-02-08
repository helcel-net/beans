package net.helcel.beendroid.svg

import android.content.Context
import android.util.TypedValue
import com.caverock.androidsvg.SVG
import net.helcel.beendroid.countries.Country
import net.helcel.beendroid.countries.GeoLoc
import net.helcel.beendroid.countries.World

class PSVGWrapper(ctx: Context) {

    private val cm = HashMap<GeoLoc, PSVGLoader>()
    private var fm = ""

    private val colorForeground: String
    private val colorBackground: String

    init {
        val colorSecondaryTyped = TypedValue()
        ctx.theme.resolveAttribute(android.R.attr.panelColorBackground, colorSecondaryTyped, true)
        colorForeground = "\"#${Integer.toHexString(colorSecondaryTyped.data).subSequence(2, 8)}\""

        val colorBackgroundTyped = TypedValue()
        ctx.theme.resolveAttribute(android.R.attr.colorBackground, colorBackgroundTyped, true)
        colorBackground = "\"#${Integer.toHexString(colorBackgroundTyped.data).subSequence(2, 8)}\""


        Country.entries.forEach {
            cm[it] = PSVGLoader(ctx, it, Level.ZERO).load()
        }
        build()
    }
    fun level(el: Country, level: Level){
        cm[el]?.changeLevel(level)
    }

    private fun build(){
        fm = World.WWW.children.map { gr ->
            gr.children.map {c ->
                val cc = cm[c]
                if (cc!=null) "<g class=\"${c.code} ${gr.code}\">${cc.data}</g>"
                else ""
            }.fold("") { acc, e -> acc + e }
        }.fold("") { acc, e -> acc + e }
    }

    fun get(): SVG {
        return SVG.getFromString("<svg id=\"map\" xmlns=\"http://www.w3.org/2000/svg\" width=\"1200\" height=\"1200\" x=\"0\" y=\"0\" fill=${colorForeground} stroke=${colorBackground} stroke-width=\"0.25px\">$fm</svg>")
    }

}