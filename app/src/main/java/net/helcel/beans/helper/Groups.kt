package net.helcel.beans.helper

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import kotlin.random.Random
import androidx.core.graphics.drawable.toDrawable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


private const val randSeed = 0
private val rnd = Random(randSeed)

const val NO_GROUP = 0
const val DEFAULT_GROUP = 1
const val AUTO_GROUP = -1



@Serializable
class Groups(val id: Int, private val grps: HashMap<Int, Group>) {
    @kotlinx.serialization.Transient
    private val _groupsFlow = MutableStateFlow<List<Group>>(grps.values.toList())
    @kotlinx.serialization.Transient
    val groupsFlow: StateFlow<List<Group>> = _groupsFlow.asStateFlow()

    fun setGroup(key: Int, name: String, col: ColorDrawable) {
        grps[key] = Group(key, name, col)
        _groupsFlow.value = grps.values.toList()
    }

    fun deleteGroup(key: Int) {
        grps.remove(key)
        _groupsFlow.value = grps.values.toList()
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
        if(grps.keys.isEmpty()) return Pair(NO_GROUP,Group(NO_GROUP,"-"))
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
        @Serializable(with = Theme.ColorDrawableSerializer::class) val color: ColorDrawable = Color.GRAY.toDrawable()
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