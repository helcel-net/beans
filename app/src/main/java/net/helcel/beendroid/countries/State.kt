package net.helcel.beendroid.countries

enum class State(override val fullName: String, override val area: Int,) : GeoLoc {
    UNDEFINED("",0);

    override val code = this.name
    override val children = emptyList<GeoLoc>()
    override val type = LocType.STATE
}