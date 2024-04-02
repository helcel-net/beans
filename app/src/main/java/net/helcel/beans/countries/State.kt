package net.helcel.beans.countries

class State(override val code: String, override val fullName: String, override val area: Int) :
    GeoLoc {

    override val children = emptyList<GeoLoc>()
    override val type = GeoLoc.LocType.STATE
}