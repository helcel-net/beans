package net.helcel.beendroid.svg

import android.content.Context
import android.util.Log
import com.caverock.androidsvg.SVG
import net.helcel.beendroid.R
import net.helcel.beendroid.countries.Country
import net.helcel.beendroid.countries.GeoLoc
import net.helcel.beendroid.countries.World
import net.helcel.beendroid.helper.colorWrapper

@OptIn(ExperimentalStdlibApi::class)
class PSVGWrapper(ctx: Context) {

    private val cm = HashMap<GeoLoc, PSVGLoader>()
    private var fm = ""

    private val colorForeground: String = colorWrapper(ctx, android.R.attr.panelColorBackground).color.toHexString().substring(2)
    private val colorBackground: String = colorWrapper(ctx, android.R.attr.colorBackground).color.toHexString().substring(2)

    init {
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
        return SVG.getFromString("<svg id=\"map\" xmlns=\"http://www.w3.org/2000/svg\" width=\"1200\" height=\"1200\" x=\"0\" y=\"0\" fill=\"#$colorForeground\" stroke=\"#$colorBackground\" stroke-width=\"0.25px\">$fm</svg>")
    }

}