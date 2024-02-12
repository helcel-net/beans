package net.helcel.beendroid.helper

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import kotlin.random.Random


class Groups(ctx: Context) {
    private val randSeed = 0
    private val rnd = Random(randSeed)
    private var grps: MutableMap<Int,Pair<String, ColorDrawable>> = HashMap()
    private val pref = ctx.getSharedPreferences("Groups", Context.MODE_PRIVATE)
    private val editor = pref.edit()

    fun load(): Groups {
        pref.all.keys.filter { !it.endsWith("_color") }.forEach {
            grps[it.toInt()] = Pair(pref.getString(it, "")!!,
                ColorDrawable(pref.getInt(it+"_color", Color.WHITE)))
        }
        editor.apply()
        return this
    }

    fun setGroup(key: Int, name: String, col: ColorDrawable) {
        grps[key] = Pair(name,col)
        editor.putString(key.toString(), name)
        editor.putInt(key.toString()+"_color", col.color)
        editor.apply()
    }

    fun getGroupFromKey(key: Int): Pair<String,ColorDrawable>? {
        return grps.getOrDefault(key,null)
    }

    fun genKey(): Int {
        val key = rnd.nextInt()
        if(grps.containsKey(key) || key == 0) return genKey()
        return key
    }

    fun size(): Int {
        return grps.size
    }

    fun getGroupFromPos(pos: Int): Pair<Int,Pair<String,ColorDrawable>> {
        val key = grps.keys.toList()[pos]
        return Pair(key,getGroupFromKey(key)!!)
    }

    fun findGroupPos(key: Int): Int {
        return grps.keys.toList().indexOf(key)
    }

}