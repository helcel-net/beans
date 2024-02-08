package net.helcel.beendroid.svg

import net.helcel.beendroid.countries.Visited
import net.helcel.beendroid.countries.World

class CSSWrapper(private val visited: Visited) {

    private val colorPrimary = "#0187FF"

    fun get() : String {
        return listOf(World.WWW.children
            .filter { visited.getVisited(it)}
            .map { ".${it.code}{fill:$colorPrimary;}"}
            .fold(""){acc, s-> acc + s},
        World.WWW.children
            .filter { !visited.getVisited(it) }
            .map { cg -> cg.children
                .filter { visited.getVisited(it) }
                .map { ".${it.code}{fill:$colorPrimary;}"}
                .fold(""){acc, s-> acc + s}
            }.fold(""){acc,s->acc+s},
        ).fold(""){acc,s-> acc+s}
    }

}