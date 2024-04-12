package net.helcel.beans.activity

import android.content.Intent
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.caverock.androidsvg.RenderOptions
import net.helcel.beans.R
import net.helcel.beans.countries.GeoLocImporter
import net.helcel.beans.databinding.ActivityMainBinding
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.Settings
import net.helcel.beans.svg.CSSWrapper
import net.helcel.beans.svg.SVGWrapper


class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding

    private lateinit var psvg: SVGWrapper
    private lateinit var css: CSSWrapper

    override fun onRestart() {
        refreshProjection()
        refreshMap()
        super.onRestart()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val d = when (item.itemId) {
            R.id.action_edit -> EditActivity::class.java
            R.id.action_stats -> StatsActivity::class.java
            R.id.action_settings -> SettingsActivity::class.java
            else -> throw Exception("Non Existent Menu Item")
        }
        startActivity(Intent(this@MainActivity, d))
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        Settings.start(this)

        setContentView(_binding.root)

        _binding.photoView.minimumScale = 1f
        _binding.photoView.maximumScale = 40f

        GeoLocImporter.importStates(this)
        Data.loadData(this, Int.MIN_VALUE)

        refreshProjection()
        refreshMap()
    }

    private fun refreshMap() {
        val opt: RenderOptions = RenderOptions.create()
        opt.css(css.get())
        _binding.photoView.setImageDrawable(PictureDrawable(psvg.get()?.renderToPicture(opt)))
    }

    fun refreshProjection() {
        psvg = SVGWrapper(this)
        css = CSSWrapper(this)
    }

}