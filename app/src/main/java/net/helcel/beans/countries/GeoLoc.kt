package net.helcel.beans.countries

interface GeoLoc {

    enum class LocType(val txt: String) {
        WORLD("Continents"), GROUP("Groups"), CUSTOM_GROUP("Groups"), COUNTRY("Countries"), STATE("Regions");
    }

    val code: String
    val fullName: String
    val area: Int

    val type: LocType
    val children: Set<GeoLoc>
}