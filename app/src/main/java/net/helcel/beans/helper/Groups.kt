package net.helcel.beans.helper

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
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
class Groups(val id: Int, @SerialName("grps") private val groups: HashMap<Int, Group>) {
    @kotlinx.serialization.Transient
    private val _groupsFlow = MutableStateFlow<List<Group>>(groups.values.toList())
    @kotlinx.serialization.Transient
    val groupsFlow: StateFlow<List<Group>> = _groupsFlow.asStateFlow()

    fun setGroup(key: Int, name: String, col: ColorDrawable) {
        val old = groups[key]
        if (old != null && old.name == name && old.color.color == col.color) return
        groups[key] = Group(key, name, col)
        updateFlow()
    }

    fun deleteGroup(key: Int) {
        groups.remove(key)
        updateFlow()
    }

    fun keepOnly(key: Int) {
        val keep = groups[key] ?: return
        groups.clear()
        groups[key] = keep
        updateFlow()
    }

    private fun updateFlow() {
        _groupsFlow.value = groups.values.toList()
    }

    fun getGroupFromKey(key: Int): Group {
        return groups.getOrDefault(key, EmptyGroup())
    }

    fun genKey(): Int {
        val key = rnd.nextInt()
        if (groups.containsKey(key) || key in listOf(NO_GROUP, DEFAULT_GROUP, AUTO_GROUP)) return genKey()
        return key
    }

    fun size(): Int {
        return groups.size
    }

    fun getUniqueEntry(): Group? {
        assert(size() == 1)
        return if (groups.size == 1) {
            groups[groups.keys.first()]
        } else {
            null
        }
    }

    fun getGroupFromPos(pos: Int): Pair<Int, Group> {
        if(groups.keys.isEmpty()) return Pair(NO_GROUP,Group(NO_GROUP,"-"))
        val key = groups.keys.toList()[pos]
        return Pair(key, getGroupFromKey(key))
    }

    fun findGroupPos(key: Int): Int {
        return groups.keys.toList().indexOf(key)
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