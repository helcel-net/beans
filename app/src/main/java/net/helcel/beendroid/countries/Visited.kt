package net.helcel.beendroid.countries

import android.content.Context


class Visited(ctx: Context) {
    private var locs: MutableMap<GeoLoc, Boolean> = HashMap()
    private val pref = ctx.getSharedPreferences("Visited", Context.MODE_PRIVATE)
    private val editor = pref.edit()

    fun load() {

        Group.entries.forEach {
            locs[it] = pref.getBoolean(it.code, false)
        }
        Country.entries.forEach {
            locs[it] = pref.getBoolean(it.code, false)
        }
        State.entries.forEach {
            locs[it] = pref.getBoolean(it.code, false)
        }
        editor.apply()
    }

    fun setVisited(key: GeoLoc, b: Boolean){
        locs[key] = b
        editor.putBoolean(key.code, b)
        editor.apply()
    }

    fun visited(key: GeoLoc): Boolean {
        return locs.getOrDefault(key,false)
    }

}