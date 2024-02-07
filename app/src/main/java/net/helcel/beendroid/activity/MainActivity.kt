package net.helcel.beendroid.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.preference.PreferenceManager
import com.caverock.androidsvg.RenderOptions
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.helcel.beendroid.R
import net.helcel.beendroid.activity.fragment.SettingsFragment
import net.helcel.beendroid.countries.Visited

import net.helcel.beendroid.svg.CSSWrapper
import net.helcel.beendroid.svg.PSVGWrapper
import net.helcel.beendroid.helper.*


class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var photoView : PhotoView

    private lateinit var visited : Visited
    private lateinit var psvg : PSVGWrapper
    private lateinit var css : CSSWrapper

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        refreshMap()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create action bar
        supportActionBar?.setBackgroundDrawable(colorPrimary(this))

        // Fetch shared preferences to restore app theme upon startup
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        SettingsFragment.setTheme(this, sharedPreferences.getString(getString(R.string.key_theme), getString(R.string.system)))

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
                        // TODO: Write stats activity
                        true
                    }
                    R.id.action_settings -> {
                        // Open settings
                        startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

        })

        // Restore visited countries
        visited = Visited(this)
        visited.load()

        // Wrap lists of countries
        psvg = PSVGWrapper(this)
        css = CSSWrapper(visited)

        // Populate map from list of countries
        setContentView(R.layout.activity_main)

        photoView = findViewById(R.id.photo_view)
        photoView.minimumScale = 1f
        photoView.maximumScale = 30f

        refreshMap()
    }

    private fun refreshMap() {
        val opt : RenderOptions = RenderOptions.create()
        CoroutineScope(Dispatchers.IO).launch {
            opt.css(css.get())
        }
        photoView.setImageLevel(1)
        photoView.setImageDrawable(PictureDrawable(psvg.getFill().renderToPicture(opt)))
    }

}