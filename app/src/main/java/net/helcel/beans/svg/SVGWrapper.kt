package net.helcel.beans.svg

import android.content.Context
import com.caverock.androidsvg.SVG

class SVGWrapper(ctx: Context) {

    private var svg: SVG? = SVG.getFromAsset(ctx.assets, "mercator01.svg")

    fun get(): SVG? {
        return svg
    }
}