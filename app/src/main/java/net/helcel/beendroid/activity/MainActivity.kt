package net.helcel.beendroid.activity

import kotlinx.coroutines.*
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caverock.androidsvg.RenderOptions
import com.caverock.androidsvg.SVGImageView
import net.helcel.beendroid.R
import net.helcel.beendroid.countries.Visited
import net.helcel.beendroid.countries.World
import net.helcel.beendroid.svg.CSSWrapper
import net.helcel.beendroid.svg.PSVGWrapper


class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var map : SVGImageView
    private lateinit var list : RecyclerView

    private lateinit var visited : Visited
    private lateinit var psvg : PSVGWrapper
    private lateinit var css : CSSWrapper

    private var processor: ImageProcessor = ImageProcessor({ refreshMapCompute() },{ refreshMapDisplay(it) })

    private val bitmap: Bitmap = Bitmap.createBitmap(1200,900, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create action bar
        val colorPrimaryTyped = TypedValue()
        theme.resolveAttribute(android.R.attr.colorPrimary, colorPrimaryTyped, true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(colorPrimaryTyped.data))

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
                        // TODO: Enable editing selected countries
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
        map = findViewById(R.id.map)
        map.setImageBitmap(bitmap)
        refreshMapDisplay(refreshMapCompute())

        // Populate list below the map
        list = findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        list.adapter = FoldingListAdapter(this, World.WWW.children, visited) { processor.process() }
    }

    private fun refreshMapDisplay(css_value: String){
        // Set or reset background (replaces canvas.drawColor(0, 0, 0))
        val colorBackgroundTyped = TypedValue()
        theme.resolveAttribute(android.R.attr.colorBackground, colorBackgroundTyped, true)
        canvas.drawColor(colorBackgroundTyped.data)

        // Render all countries and visited ones
        psvg.getFill().renderToCanvas(canvas, RenderOptions.create().css(css_value))

        // Render all contours in the same color as the background to make them much clearer
        psvg.getDraw().renderToCanvas(canvas)
    }

    private fun refreshMapCompute() : String {
        return css.get()
    }



    class ImageProcessor(private val refreshMapCompute: ()->String, private val refreshMapDisplay: (String)->Unit) {

        private var currentJob : Job? = null
        fun process() {
            currentJob?.cancel()
            currentJob = CoroutineScope(Dispatchers.Main).launch {
                try {
                    refreshMapDisplay(refreshMapCompute())
                } catch (_: CancellationException) {
                }
            }
        }
    }

}