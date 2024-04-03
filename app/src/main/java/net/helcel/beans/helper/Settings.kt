package net.helcel.beans.helper

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import net.helcel.beans.R
import net.helcel.beans.activity.fragment.SettingsFragment

object Settings {

    private lateinit var sp: SharedPreferences
    fun start(ctx: Context) {
        sp = PreferenceManager.getDefaultSharedPreferences(ctx)
        SettingsFragment.setTheme(
            ctx, sp.getString(ctx.getString(R.string.key_theme), ctx.getString(R.string.system))
        )
    }

    fun isSingleGroup(ctx: Context): Boolean {
        return !getBooleanValue(
            ctx,
            sp.getString(ctx.getString(R.string.key_group), ctx.getString(R.string.off))
        )
    }

    fun isRegional(ctx: Context): Boolean {
        return getBooleanValue(
            ctx,
            sp.getString(ctx.getString(R.string.key_regional), ctx.getString(R.string.off))
        )
    }

    fun getStatPref(ctx: Context): String? {
        return sp.getString(
            ctx.getString(R.string.key_stats),
            ctx.getString(R.string.counters)
        )
    }

    private fun getBooleanValue(ctx: Context, key: String?): Boolean {
        return when (key) {
            ctx.getString(R.string.on) -> true
            ctx.getString(R.string.off) -> false
            else -> false
        }
    }
}