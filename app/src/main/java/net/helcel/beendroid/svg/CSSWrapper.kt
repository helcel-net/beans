package net.helcel.beendroid.svg

import android.content.Context
import net.helcel.beendroid.countries.World
import net.helcel.beendroid.helper.colorToHex6
import net.helcel.beendroid.helper.colorWrapper
import net.helcel.beendroid.helper.groups
import net.helcel.beendroid.helper.visits

class CSSWrapper(ctx: Context) {


    fun get() : String {
        return listOf(World.WWW.children
            .filter { visits!!.getVisited(it)!=0}
            .map { ".${it.code}{fill:${colorToHex6(groups!!.getGroupFromKey(visits!!.getVisited(it))!!.color)};}"}
            .fold(""){acc, s-> acc + s},
        World.WWW.children
            .filter { visits!!.getVisited(it)==0 }
            .map { cg -> cg.children
                .filter { visits!!.getVisited(it)!=0 }
                .map { ".${it.code}{fill:${colorToHex6(groups!!.getGroupFromKey(visits!!.getVisited(it))!!.color)};}"}
                .fold(""){acc, s-> acc + s}
            }.fold(""){acc,s->acc+s},
        ).fold(""){acc,s-> acc+s}
    }

}