package net.helcel.beans.countries

import android.content.Context
import net.helcel.beans.helper.Settings
import java.io.BufferedReader
import java.io.InputStreamReader

object GeoLocImporter {

    fun importStates(ctx: Context) {
        if (!Settings.isRegional(ctx)) {
            return
        }
        val fs = BufferedReader(InputStreamReader(ctx.assets.open("geoloc_state.txt")))
        while (fs.ready()) {
            val line = fs.readLine().split("|")
            val state = State(line[0], line[2], line[3].toInt())
            Country.entries.find { it.code == line[1] }?.children?.add(state)
        }
    }

    fun clearStates() {
        Country.entries.forEach { it.children.clear() }
    }
}