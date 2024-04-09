package net.helcel.beans.svg

import android.content.Context
import androidx.preference.PreferenceManager
import com.caverock.androidsvg.SVG
import net.helcel.beans.R

class SVGWrapper(ctx: Context) {

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
    val svgFile = when (sharedPreferences.getString(ctx.getString(R.string.key_projection), ctx.getString(R.string.mercator))) {
        ctx.getString(R.string.azimuthalequidistant) -> "aeqd01.svg"
        ctx.getString(R.string.equirectangular) -> "eqdc01.svg"
        ctx.getString(R.string.equidistant) -> "eqearth01.svg"
        ctx.getString(R.string.mercator) -> "mercator01.svg"
        ctx.getString(R.string.loximuthal) -> "loxim01.svg"
        ctx.getString(R.string.webmercator) -> "webmercator01.svg"
        else -> "webmercator01.svg"
    }

    private var svg: SVG? = SVG.getFromAsset(ctx.assets, svgFile)

    fun get(): SVG? {
        return svg
    }
}