package net.helcel.beans.helper

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import net.helcel.beans.R
import net.helcel.beans.activity.MainActivity
import net.helcel.beans.activity.fragment.SettingsFragment

object Settings {

    private lateinit var sp: SharedPreferences
    private lateinit var mainActivity: MainActivity
    fun start(ctx: MainActivity) {
        mainActivity = ctx
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

    fun refreshProjection(): Boolean {
        mainActivity.refreshProjection()
        return true
    }

    private fun getBooleanValue(ctx: Context, key: String?): Boolean {
        return when (key) {
            ctx.getString(R.string.on) -> true
            ctx.getString(R.string.off) -> false
            else -> false
        }
    }

    fun getStats(ctx: Context, numerator: Int?, denominator: Int?, unit: String = ""): String {
        if (numerator == null || denominator == null || denominator == 0) {
            return ""
        }
        return when (getStatPref(ctx)) {
            ctx.getString(R.string.percentages) -> ctx.getString(
                R.string.percentage,
                (100 * (numerator.toFloat() / denominator.toFloat())).toInt()
            )

            else -> {
                if (unit == "") {
                    ctx.getString(R.string.rate, numerator, denominator)
                } else {
                    ctx.getString(R.string.rate_with_unit, numerator, denominator, unit)
                }
            }
        }
    }
}