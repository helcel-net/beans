package net.helcel.beans.countries

import android.content.Context
import net.helcel.beans.helper.AUTO_GROUP
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.NO_GROUP
import net.helcel.beans.helper.Settings
import java.io.BufferedReader
import java.io.InputStreamReader

object GeoLocImporter {

    fun importStates(ctx: Context, force: Boolean = false) {
        if (!Settings.isRegional(ctx) and !force) {
            return
        }
        val fs = BufferedReader(InputStreamReader(ctx.assets.open("geoloc_state.txt")))
        while (fs.ready()) {
            val line = fs.readLine().split("|")
            val state = State(line[0], line[2], line[3].toInt())
            val country = Country.entries.find { it.code == line[1] }
            country?.children?.add(state)
            country?.let {
                if (Data.visits.getVisited(it) == NO_GROUP) {
                    Data.visits.setVisited(state, NO_GROUP)
                }
            }
        }
    }

    fun clearStates() {
        Country.entries.forEach { country ->
            if (country.children.any { region ->
                    Data.visits.getVisited(region) != NO_GROUP
                }) {
                if (Data.visits.getVisited(country) == NO_GROUP) {
                    Data.visits.setVisited(country, AUTO_GROUP)
                }
            }
            country.children.clear()
        }
        Data.saveData()
    }
}