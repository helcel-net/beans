package net.helcel.beans.helper

import android.content.Context
import android.content.SharedPreferences

/**
 * The preference file opened directly, under the name and mode androidx's
 * PreferenceManager used. Settings written by earlier versions still load, and
 * the app no longer pulls in the preference UI framework for one call.
 */
fun defaultPreferences(ctx: Context): SharedPreferences =
    ctx.getSharedPreferences(ctx.packageName + "_preferences", Context.MODE_PRIVATE)
