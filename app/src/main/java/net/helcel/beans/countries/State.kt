package net.helcel.beans.countries

class State(override val code: String, override val fullName: String, override val area: Int) :
    GeoLoc {

    override val children = emptySet<GeoLoc>()
    override val type = GeoLoc.LocType.STATE

    override fun hashCode(): Int {
        return code.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is GeoLoc) {
            return other.code == this.code
        }
        return false
    }
}