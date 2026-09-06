package net.helcel.beans.map

import android.content.Context
import net.helcel.beans.R
import net.helcel.beans.helper.defaultPreferences

/** Picks the map asset matching the projection the user chose. */
object MapAssets {

    fun assetFor(ctx: Context): String {
        val preferences = defaultPreferences(ctx)
        return when (
            preferences.getString(
                ctx.getString(R.string.key_projection),
                ctx.getString(R.string.mercator),
            )
        ) {
            ctx.getString(R.string.azimuthalequidistant) -> "aeqd01.bmap"
            ctx.getString(R.string.loximuthal) -> "loxim01.bmap"
            else -> "webmercator01.bmap"
        }
    }
}
