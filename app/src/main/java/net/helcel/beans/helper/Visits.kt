package net.helcel.beans.helper

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.Json
import net.helcel.beans.countries.GeoLoc
import java.io.InputStream


@Serializable
class Visits(val id: Int, private val locs: HashMap<String, Int>) {

    fun setVisited(key: GeoLoc?, b: Int) {
        if (b == 0 || key == null)
            return
        locs[key.code] = b
    }

    fun deleteVisited(key: Int) {
        val keysToDelete = locs
            .filter { it.value == key }
            .map { it.key }
        keysToDelete.forEach {
            locs.remove(it)
        }
    }

    fun getVisited(key: GeoLoc): Int {
        return locs.getOrDefault(key.code, 0)
    }

    private fun getVisited(key: String): Int {
        return locs.getOrDefault(key, 0)
    }

    fun countVisited(key: Int): Int {
        return locs.filter { it.value == key }.size
    }

    fun getVisitedByValue(): Map<Int, List<String>> {
        return locs.keys.groupBy { getVisited(it) }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Serializer(Visits::class)
    class VisitsSerializer {
        val defaultValue: Visits
            get() = Visits(Int.MIN_VALUE, hashMapOf())

        fun readFrom(input: InputStream): Visits {
            return Json.decodeFromString(serializer(), input.readBytes().decodeToString())
        }

        fun writeTo(t: Visits): String {
            return Json.encodeToString(serializer(), t).encodeToByteArray().decodeToString()
        }

    }

}