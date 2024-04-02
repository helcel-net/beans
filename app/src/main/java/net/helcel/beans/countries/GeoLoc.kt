package net.helcel.beans.countries


interface GeoLoc {

    enum class LocType {
        WORLD, GROUP, CUSTOM_GROUP, COUNTRY, STATE;
    }

    val code: String
    val fullName: String
    val area: Int

    val type: LocType
    val children: List<GeoLoc>

}


