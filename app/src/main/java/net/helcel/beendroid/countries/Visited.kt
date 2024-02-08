package net.helcel.beendroid.countries

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Visited(ctx: Context) {
    private var locs: MutableMap<GeoLoc, Boolean> = HashMap()
    private val pref = ctx.getSharedPreferences("Visited", Context.MODE_PRIVATE)
    private val editor = pref.edit()

    fun load(): Visited {
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
        return this
    }

    fun setVisited(key: GeoLoc, b: Boolean) {
        locs[key] = b
        CoroutineScope(Dispatchers.Main).launch {
            editor.putBoolean(key.code, b)
            editor.apply()
        }
    }

    fun getVisited(key: GeoLoc): Boolean {
        return locs.getOrDefault(key,false)
    }

}