package net.helcel.beans.countries

import net.helcel.beans.countries.Group.*

enum class World(override val fullName: String, override val children: Set<GeoLoc>) : GeoLoc {

    WWW(
        "World", setOf(
            EEE, ABB, FFF, NNN, SRR, UUU, XXX
        )
    );

    override val area = children.fold(0) { acc, i ->
        acc + i.area
    }


    override val type = GeoLoc.LocType.WORLD
    override val code = this.name


}