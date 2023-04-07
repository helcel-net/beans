package net.helcel.beendroid.countries

enum class LocType {
    WORLD, GROUP, CUSTOM_GROUP, COUNTRY, STATE;
}

interface GeoLoc {
    val code : String
    val fullName : String
    val area : Int

    val type : LocType
    val children : List<GeoLoc>

}