package net.helcel.beendroid.svg

import net.helcel.beendroid.countries.Visited
import net.helcel.beendroid.countries.World

class CSSWrapper(private val visited: Visited) {

    fun get() : String {
        return listOf(World.WWW.children
            .filter { visited.visited(it)}
            .map { ".${it.code}{fill:blue;}"}
            .fold(""){acc, s-> acc + s},
        World.WWW.children
            .filter { !visited.visited(it) }
            .map { cg -> cg.children
                .filter { visited.visited(it) }
                .map { ".${it.code}{fill:blue;}"}
                .fold(""){acc, s-> acc + s}
            }.fold(""){acc,s->acc+s},
        ).fold(""){acc,s-> acc+s}
    }

}