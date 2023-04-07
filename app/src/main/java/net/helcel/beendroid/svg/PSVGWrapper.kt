package net.helcel.beendroid.svg

import android.content.Context
import com.caverock.androidsvg.SVG
import net.helcel.beendroid.countries.Country
import net.helcel.beendroid.countries.GeoLoc
import net.helcel.beendroid.countries.World

class PSVGWrapper(ctx: Context) {

    private val cm = HashMap<GeoLoc, PSVGLoader>()
    private var fm = ""

    init {
        Country.values().forEach {
            cm[it] = PSVGLoader(ctx, it, Level.ZERO).load()
        }
        build()
    }
    fun level(el: Country, level: Level){
        cm[el]?.changeLevel(level)
    }

    fun build(){
        fm = World.WWW.children.map { gr ->
            gr.children.map {c ->
                val cc = cm[c]
                if (cc!=null) "<g class=\"${c.code} ${gr.code}\">${cc.data}</g>"
                else ""
            }.fold("") { acc, e -> acc + e }
        }.fold("") { acc, e -> acc + e }
    }

    fun get(): SVG {
        return SVG.getFromString("<svg id=\"map\" xmlns=\"http://www.w3.org/2000/svg\" width=\"1200\" height=\"1200\" x=\"0\" y=\"0\" >$fm</svg>")
    }





}