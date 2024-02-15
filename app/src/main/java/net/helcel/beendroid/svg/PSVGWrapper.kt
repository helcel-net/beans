package net.helcel.beendroid.svg

import android.content.Context
import com.caverock.androidsvg.SVG
import net.helcel.beendroid.countries.Country
import net.helcel.beendroid.countries.GeoLoc
import net.helcel.beendroid.countries.World
import net.helcel.beendroid.helper.colorToHex6
import net.helcel.beendroid.helper.colorWrapper

class PSVGWrapper(ctx: Context) {

    private val cm = HashMap<GeoLoc, PSVGLoader>()
    private var fm = ""

    private val colorForeground: String = colorToHex6(colorWrapper(ctx, android.R.attr.panelColorBackground))
    private val colorBackground: String = colorToHex6(colorWrapper(ctx, android.R.attr.colorBackground))

    init {
        Country.entries.forEach {
            cm[it] = PSVGLoader(ctx, it, Level.ZERO).load()
        }
        build()
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
        return SVG.getFromString("<svg id=\"map\" xmlns=\"http://www.w3.org/2000/svg\" width=\"1200\" height=\"1200\" x=\"0\" y=\"0\" fill=\"$colorForeground\" stroke=\"$colorBackground\" stroke-width=\"0.25px\">$fm</svg>")
    }

}