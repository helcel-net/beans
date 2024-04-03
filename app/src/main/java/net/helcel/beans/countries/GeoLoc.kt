package net.helcel.beans.countries

import android.content.Context
import net.helcel.beans.helper.Settings


interface GeoLoc {

    enum class LocType {
        WORLD, GROUP, CUSTOM_GROUP, COUNTRY, STATE;
    }

    val code: String
    val fullName: String
    val area: Int

    val type: LocType
    val children: List<GeoLoc>

    fun shouldShowChildren(ctx: Context): Boolean {
        if (children.isEmpty())
            return false
        if (type == LocType.COUNTRY && !Settings.isRegional(ctx))
            return false
        return true
    }

}


