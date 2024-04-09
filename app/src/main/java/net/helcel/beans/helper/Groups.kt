package net.helcel.beans.helper

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.Json
import net.helcel.beans.R
import java.io.InputStream
import kotlin.coroutines.coroutineContext
import kotlin.random.Random


private const val randSeed = 0
private val rnd = Random(randSeed)

const val NO_GROUP = 0
const val DEFAULT_GROUP = 1
const val AUTO_GROUP = -1

@Serializable
class Groups(val id: Int, private val grps: HashMap<Int, Group>) {

    fun setGroup(key: Int, name: String, col: ColorDrawable) {
        grps[key] = Group(key, name, col)
    }

    fun deleteGroup(key: Int) {
        grps.remove(key)
    }

    fun deleteAllExcept(grp: Int) {
        val keysToDelete = grps.keys.filter { it != grp }
        keysToDelete.forEach { grps.remove(it) }
    }

    fun getGroupFromKey(key: Int): Group {
        return grps.getOrDefault(key, EmptyGroup())
    }

    fun genKey(): Int {
        val key = rnd.nextInt()
        if (grps.containsKey(key) || key in listOf(NO_GROUP, DEFAULT_GROUP, AUTO_GROUP)) return genKey()
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

    fun getGroupFromPos(pos: Int): Pair<Int, Group> {
        val key = grps.keys.toList()[pos]
        return Pair(key, getGroupFromKey(key))
    }

    fun findGroupPos(key: Int): Int {
        return grps.keys.toList().indexOf(key)
    }

    class EmptyGroup : Group(0, "")

    @Serializable
    open class Group(
        val key: Int,
        val name: String,
        @Serializable(with = Theme.ColorDrawableSerializer::class) val color: ColorDrawable = ColorDrawable(
            Color.GRAY
        )
    )

    @OptIn(ExperimentalSerializationApi::class)
    @Serializer(Groups::class)
    class GroupsSerializer {
        val defaultValue: Groups
            get() = Groups(Int.MIN_VALUE, hashMapOf())

        fun readFrom(input: InputStream): Groups {
            return Json.decodeFromString(serializer(), input.readBytes().decodeToString())
        }

        fun writeTo(t: Groups): String {
            return Json.encodeToString(serializer(), t).encodeToByteArray().decodeToString()
        }

    }

}