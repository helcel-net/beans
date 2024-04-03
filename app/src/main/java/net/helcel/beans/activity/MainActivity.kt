package net.helcel.beans.activity

import android.content.Intent
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.caverock.androidsvg.RenderOptions
import com.github.chrisbanes.photoview.PhotoView
import net.helcel.beans.R
import net.helcel.beans.countries.GeoLocImporter
import net.helcel.beans.helper.Data.loadData
import net.helcel.beans.helper.Settings
import net.helcel.beans.helper.Theme.colorWrapper
import net.helcel.beans.svg.CSSWrapper
import net.helcel.beans.svg.SVGWrapper


class MainActivity : AppCompatActivity() {

    private lateinit var photoView: PhotoView

    private lateinit var psvg: SVGWrapper
    private lateinit var css: CSSWrapper

    override fun onRestart() {
        refreshMap()
        super.onRestart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create action bar
        supportActionBar?.setBackgroundDrawable(colorWrapper(this, android.R.attr.colorPrimary))

        // restore app theme & settings upon startup
        Settings.start(this)

        // Create menu in action bar
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_edit -> {
                        startActivity(Intent(this@MainActivity, EditActivity::class.java))
                        true
                    }

                    R.id.action_stats -> {
                        startActivity(Intent(this@MainActivity, StatActivity::class.java))
                        true
                    }

                    R.id.action_settings -> {
                        startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                        true
                    }

                    else -> {
                        false
                    }
                }
            }

        })

        // Populate map from list of countries
        setContentView(R.layout.activity_main)

        photoView = findViewById(R.id.photo_view)
        photoView.minimumScale = 1f
        photoView.maximumScale = 40f

        GeoLocImporter.importStates(this)
        loadData(this, Int.MIN_VALUE)
        psvg = SVGWrapper(this)
        css = CSSWrapper(this)

        refreshMap()
    }

    private fun refreshMap() {
        val opt: RenderOptions = RenderOptions.create()
        css.refresh()
        opt.css(css.get())
        photoView.setImageDrawable(PictureDrawable(psvg.get()?.renderToPicture(opt)))
    }

}