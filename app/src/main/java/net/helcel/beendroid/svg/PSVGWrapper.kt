package net.helcel.beendroid.svg

import android.content.Context
import com.caverock.androidsvg.SVG
import net.helcel.beendroid.countries.Country

class PSVGWrapper(ctx: Context) {



    private val cm = HashMap<Country, PSVGLoader>()

    init {
        Country.values().forEach {
            cm[it] = PSVGLoader(ctx, it, Level.ZERO).load()
        }
    }
    fun level(el: Country, level: Level){
        cm[el]?.changeLevel(level)
    }

    fun get(): SVG {
        val fm = cm.values.fold("") { acc, e -> acc + e.data }
        return SVG.getFromString("<svg id=\"map\" xmlns=\"http://www.w3.org/2000/svg\" width=\"1200\" height=\"1200\" x=\"0\" y=\"0\" >$fm</svg>")
    }





}