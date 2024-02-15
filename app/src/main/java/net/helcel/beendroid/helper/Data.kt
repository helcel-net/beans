package net.helcel.beendroid.helper

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import net.helcel.beendroid.countries.GeoLoc

var visits : Visits? = null
var groups : Groups? = null

var selected_group : Groups.Group? = null
var selected_geoloc: GeoLoc? = null

val groupsSerial = Groups.GroupsSerializer()
val visitsSerial = Visits.VisitsSerializer()

private var sharedPreferences: SharedPreferences? = null

fun loadData(ctx: Context, id:Int) {
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)

    val groupsString = sharedPreferences!!.getString("groups_$id",null)
    val visitsString = sharedPreferences!!.getString("visits_$id",null)

    groups = if(!groupsString.isNullOrEmpty()) groupsSerial.readFrom(groupsString.byteInputStream()) else groupsSerial.defaultValue
    visits = if(!visitsString.isNullOrEmpty()) visitsSerial.readFrom(visitsString.byteInputStream()) else visitsSerial.defaultValue
}

fun saveData() {
    if(groups!!.id != visits!!.id) return
    val id = groups!!.id
    val editor = sharedPreferences!!.edit()
    editor.putString("groups_$id", groupsSerial.writeTo(groups!!))
    editor.putString("visits_$id", visitsSerial.writeTo(visits!!))
    editor.apply()
}