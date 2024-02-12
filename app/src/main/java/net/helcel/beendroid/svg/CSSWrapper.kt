package net.helcel.beendroid.svg

import android.content.Context
import net.helcel.beendroid.helper.Visited
import net.helcel.beendroid.countries.World
import net.helcel.beendroid.helper.colorToHex6
import net.helcel.beendroid.helper.colorWrapper

class CSSWrapper(ctx: Context, private val visited: Visited) {


    private val visitedColor = colorToHex6(colorWrapper(ctx,android.R.attr.colorPrimary))

    fun get() : String {
        return listOf(World.WWW.children
            .filter { visited.getVisited(it)>0}
            .map { ".${it.code}{fill:$visitedColor;}"}
            .fold(""){acc, s-> acc + s},
        World.WWW.children
            .filter { visited.getVisited(it)==0 }
            .map { cg -> cg.children
                .filter { visited.getVisited(it)>0 }
                .map { ".${it.code}{fill:$visitedColor;}"}
                .fold(""){acc, s-> acc + s}
            }.fold(""){acc,s->acc+s},
        ).fold(""){acc,s-> acc+s}
    }

}