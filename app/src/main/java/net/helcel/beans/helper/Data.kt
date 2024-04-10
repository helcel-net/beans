package net.helcel.beans.helper

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import net.helcel.beans.R
import net.helcel.beans.countries.GeoLoc
import java.util.HashMap

object Data {
    var visits : Visits = Visits(0, HashMap())
    var groups : Groups = Groups(0,HashMap())

    var selected_group : Groups.Group? = null
    var selected_geoloc: GeoLoc? = null
    var clearing_geoloc: GeoLoc? = null

    private val groupsSerial = Groups.GroupsSerializer()
    private val visitsSerial = Visits.VisitsSerializer()

    private lateinit var sharedPreferences: SharedPreferences

    fun loadData(ctx: Context, id:Int) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)

        val groupsString = sharedPreferences.getString("groups_$id",null)
        val visitsString = sharedPreferences.getString("visits_$id",null)

        groups = if(!groupsString.isNullOrEmpty()) groupsSerial.readFrom(groupsString.byteInputStream()) else groupsSerial.defaultValue
        visits = if(!visitsString.isNullOrEmpty()) visitsSerial.readFrom(visitsString.byteInputStream()) else visitsSerial.defaultValue

        // Add default group "Visited" with app's color if there is no group already
        if (groups.size() == 0) {
            groups.setGroup(DEFAULT_GROUP, "Visited", ColorDrawable(ContextCompat.getColor(ctx, R.color.blue)))
            saveData()
        }
    }

    fun saveData() {
        if(groups.id != visits.id) return
        val id = groups.id
        val editor = sharedPreferences.edit()
        editor.putString("groups_$id", groupsSerial.writeTo(groups))
        editor.putString("visits_$id", visitsSerial.writeTo(visits))
        editor.apply()
    }
}