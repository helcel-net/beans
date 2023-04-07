package net.helcel.beendroid.activity

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var map : SVGImageView
    private lateinit var list : RecyclerView

    private lateinit var visited : Visited
    private lateinit var psvg : PSVGWrapper
    private lateinit var css : CSSWrapper

    private val bitmap: Bitmap = Bitmap.createBitmap(1200,900, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        canvas.drawRGB(255, 255, 255)

        visited = Visited(this)
        visited.load()

        psvg = PSVGWrapper(this)
        css = CSSWrapper(visited)

        setContentView(R.layout.activity_main)
        map = findViewById(R.id.map)
        map.setImageBitmap(bitmap)

        refreshMap()

        list = findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        list.adapter = FoldingListAdapter(this, World.WWW.children, visited) { refreshMap() }
    }

    private fun refreshMap(){
        psvg.get().renderToCanvas(canvas,RenderOptions.create().css(css.get()))
    }
}