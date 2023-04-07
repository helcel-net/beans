package net.helcel.beendroid.activity

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caverock.androidsvg.SVGImageView
import net.helcel.beendroid.R
import net.helcel.beendroid.countries.Visited
import net.helcel.beendroid.countries.World
import net.helcel.beendroid.svg.PSVGWrapper


class MainActivity : AppCompatActivity() {

    private lateinit var map : SVGImageView
    private lateinit var list : RecyclerView

    private lateinit var visited : Visited
    private lateinit var psvg : PSVGWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        visited = Visited(this)
        visited.load()

        psvg = PSVGWrapper(this)

        setContentView(R.layout.activity_main)
        map = findViewById(R.id.map)

        val bitmap = Bitmap.createBitmap(1200,900, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawRGB(255, 255, 255)

        psvg.get().renderToCanvas(canvas)
        map.setImageBitmap(bitmap)

        list = findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        list.adapter = FoldingListAdapter(this, World.WWW.children, visited) { }
    }
}