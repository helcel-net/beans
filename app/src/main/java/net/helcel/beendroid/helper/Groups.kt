package net.helcel.beendroid.helper

import android.graphics.drawable.ColorDrawable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import kotlin.random.Random


private const val randSeed = 0
private val rnd = Random(randSeed)
@Serializable
class Groups(val id: Int, private val grps: HashMap<Int,Group>) {

    fun setGroup(key: Int, name: String, col: ColorDrawable) {
        grps[key] = Group(key,name,col)
    }

    fun deleteGroup(key: Int) {
        grps.remove(key)
    }

    fun getGroupFromKey(key: Int): Group? {
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

    fun getUniqueEntry(): Group? {
        assert(size() == 1)
        return if (grps.size == 1) {
            grps[grps.keys.first()]
        } else {
            null
        }
    }

    fun getGroupFromPos(pos: Int): Pair<Int,Group> {
        val key = grps.keys.toList()[pos]
        return Pair(key,getGroupFromKey(key)!!)
    }

    fun findGroupPos(key: Int): Int {
        return grps.keys.toList().indexOf(key)
    }

    @Serializable
    class Group(val key: Int, val name: String, @Serializable(with = ColorDrawableSerializer::class) val color: ColorDrawable)

    @OptIn(ExperimentalSerializationApi::class)
    @Serializer(Groups::class)
    class GroupsSerializer{
        val defaultValue: Groups
            get() = Groups(Int.MIN_VALUE,hashMapOf())

        fun readFrom(input: InputStream): Groups {
            return Json.decodeFromString(serializer(),input.readBytes().decodeToString())
        }

        fun writeTo(t: Groups): String {
            return Json.encodeToString(serializer(),t).encodeToByteArray().decodeToString()
        }

    }

}