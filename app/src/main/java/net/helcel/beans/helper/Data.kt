package net.helcel.beans.helper

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import net.helcel.beans.R
import net.helcel.beans.countries.GeoLoc
import java.util.HashMap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.content.edit
import android.content.Intent
import java.io.File

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
            groups.setGroup(DEFAULT_GROUP, "Visited",
                ContextCompat.getColor(ctx, R.color.blue).toDrawable())
            saveData()
        }
    }

    fun saveData() {
        if(groups.id != visits.id) return
        val id = groups.id
        sharedPreferences.edit {
            putString("groups_$id", groupsSerial.writeTo(groups))
            putString("visits_$id", visitsSerial.writeTo(visits))
        }
    }

    fun exportData(ctx: Context, filepath: Uri){
        val groupsJson = groupsSerial.writeTo(groups)
        val visitsJson = visitsSerial.writeTo(visits)
        val outputStream = ctx.contentResolver.openOutputStream(filepath)
        outputStream?.write(
            buildString {
                append(groupsJson)
                append("\n---\n") // optional separator
                append(visitsJson)
            }.toByteArray())
        outputStream?.flush()
        outputStream?.close()
    }


    fun importData(ctx: Context, filePath: Uri) {
        val inputStream = ctx.contentResolver.openInputStream(filePath)
        val data = inputStream?.bufferedReader().use { it?.readText() }
        if(data==null) return
        val lines = data.split("\n---\n")
        val groupsJson = lines[0]
        val visitsJson = lines[1]

        groups = if(groupsJson.isNotEmpty()) groupsSerial.readFrom(groupsJson.byteInputStream()) else groupsSerial.defaultValue
        visits = if(visitsJson.isNotEmpty()) visitsSerial.readFrom(visitsJson.byteInputStream()) else visitsSerial.defaultValue

        // Add default group "Visited" with app's color if there is no group already
        if (groups.size() == 0) {
            groups.setGroup(DEFAULT_GROUP, "Visited",
                ContextCompat.getColor(ctx, R.color.blue).toDrawable())
        }
        saveData()
    }

    fun doImport(ctx: Context, file: Uri?){
        if(file!=null) {
            importData(ctx, file)
            val intent = ctx.packageManager
                .getLaunchIntentForPackage(ctx.packageName)?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            ctx.startActivity(intent)
        }
    }

    fun doExport(ctx: Context){
        val fileName = "beans_backup.json"
        val resolver = ctx.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName) // "backup.json"
            put(MediaStore.Downloads.MIME_TYPE, "text/*")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            Uri.fromFile(file)
        }
        if(uri!=null) exportData(ctx, uri)
    }
}