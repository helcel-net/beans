package net.helcel.beendroid.helper

import android.content.Context
import net.helcel.beendroid.countries.Country
import net.helcel.beendroid.countries.GeoLoc
import net.helcel.beendroid.countries.Group
import net.helcel.beendroid.countries.State
import java.lang.ClassCastException


class Visited(ctx: Context) {
    private var locs: MutableMap<GeoLoc, Int> = HashMap()
    private val pref = ctx.getSharedPreferences("Visited", Context.MODE_PRIVATE)
    private val editor = pref.edit()

    fun load(): Visited {
        Group.entries.forEach {
            try {
                locs[it] = pref.getInt(it.code, 0)
            }catch (e:ClassCastException){
                locs[it] = if (pref.getBoolean(it.code, false)) 1 else 0
            }
        }
        Country.entries.forEach {
            try {
                locs[it] = pref.getInt(it.code, 0)
            }catch (e:ClassCastException){
                locs[it] = if (pref.getBoolean(it.code, false)) 1 else 0
            }
        }
        State.entries.forEach {
            try {
                locs[it] = pref.getInt(it.code, 0)
            }catch (e:ClassCastException){
                locs[it] = if (pref.getBoolean(it.code, false)) 1 else 0
            }
        }
        editor.apply()
        return this
    }

    fun setVisited(key: GeoLoc, b: Int) {
        locs[key] = b
        editor.putInt(key.code, b)
        editor.apply()
    }

    fun getVisited(key: GeoLoc): Int {
        return locs.getOrDefault(key,0)
    }

}