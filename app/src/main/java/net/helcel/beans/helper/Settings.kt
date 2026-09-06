package net.helcel.beans.helper

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import net.helcel.beans.R
import net.helcel.beans.activity.MainScreen

/**
 * Default distance, in dp, around a tap that still counts as hitting a place.
 * Kept small because the radius is measured on screen: zoomed out to the whole
 * world, even a few dp reach across several countries.
 */
const val DEFAULT_TOUCH_RADIUS = 4

/** Largest tap radius the setting offers. */
const val MAX_TOUCH_RADIUS = 48

object Settings {

    private lateinit var sp: SharedPreferences
    private lateinit var mainActivity: MainScreen
    fun start(ctx: MainScreen) {
        mainActivity = ctx
        sp = PreferenceManager.getDefaultSharedPreferences(ctx)
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

    /**
     * How far around a tap, in dp, the map looks for other places. Anything
     * within it is offered as a candidate instead of painting straight away.
     */
    fun getTouchRadius(ctx: Context): Float {
        return sp.getInt(ctx.getString(R.string.key_touch_radius), DEFAULT_TOUCH_RADIUS).toFloat()
    }

    fun isCascadeStats(ctx: Context): Boolean {
        return getBooleanValue(
            ctx,
            sp.getString(ctx.getString(R.string.key_cascade_stats), ctx.getString(R.string.off))
        )
    }

    fun getStatPref(ctx: Context): String? {
        return sp.getString(
            ctx.getString(R.string.key_stats),
            ctx.getString(R.string.counters)
        )
    }

    fun refreshProjection(): Boolean {
        (mainActivity).refreshProjection()
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