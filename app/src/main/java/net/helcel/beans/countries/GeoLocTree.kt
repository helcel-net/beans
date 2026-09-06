package net.helcel.beans.countries

/**
 * Code to place lookup, plus the chain of parents above a place.
 *
 * Regions only exist once [GeoLocImporter] has read them, so this is rebuilt
 * whenever that set changes rather than derived once and cached forever.
 */
object GeoLocTree {

    private var index: Map<String, GeoLoc> = emptyMap()
    private var parents: Map<String, GeoLoc> = emptyMap()

    fun rebuild() {
        val byCode = HashMap<String, GeoLoc>()
        val parent = HashMap<String, GeoLoc>()
        byCode[World.WWW.code] = World.WWW
        World.WWW.children.forEach { child ->
            byCode[child.code] = child
            parent[child.code] = World.WWW
            child.children.forEach { country ->
                byCode[country.code] = country
                parent[country.code] = child
                country.children.forEach { state ->
                    byCode[state.code] = state
                    parent[state.code] = country
                }
            }
        }
        index = byCode
        parents = parent
    }

    fun find(code: String): GeoLoc? {
        if (index.isEmpty()) rebuild()
        return index[code]
    }

    /** [loc] and everything above it, world first, so it reads as a path. */
    fun chain(loc: GeoLoc): List<GeoLoc> {
        if (index.isEmpty()) rebuild()
        val path = ArrayList<GeoLoc>(4)
        var current: GeoLoc? = loc
        while (current != null && current != World.WWW) {
            path.add(current)
            current = parents[current.code]
        }
        return path.reversed()
    }
}
