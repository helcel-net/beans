package net.helcel.beans.countries


interface GeoLoc {

    enum class LocType(val txt: String) {
        WORLD("World"), GROUP("Group"), CUSTOM_GROUP("Group"), COUNTRY("Country"), STATE("State");
    }

    val code: String
    val fullName: String
    val area: Int

    val type: LocType
    val children: Set<GeoLoc>
}


