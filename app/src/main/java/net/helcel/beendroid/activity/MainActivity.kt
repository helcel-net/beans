package net.helcel.beendroid.activity

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGImageView
import net.helcel.beendroid.R
import net.helcel.beendroid.countries.Country
import net.helcel.beendroid.countries.WORLD
import net.helcel.beendroid.svg.Level
import net.helcel.beendroid.svg.PSVGWrapper


class MainActivity : AppCompatActivity() {

    private lateinit var map : SVGImageView
    private lateinit var list : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        map = findViewById(R.id.map)

        val cm = HashMap<Country, PSVGWrapper>()
        Country.values().forEach { c->
            cm[c] = PSVGWrapper(applicationContext,c, Level.ZERO).load()
        }

        val fm = cm.values.fold("") { acc, e -> acc + e.data }
        val svg = SVG.getFromString("<svg id=\"map\" xmlns=\"http://www.w3.org/2000/svg\" width=\"1200\" height=\"1200\" x=\"0\" y=\"0\" >$fm</svg>")

        val bitmap = Bitmap.createBitmap(1200,900, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawRGB(255, 255, 255)

        svg.renderToCanvas(canvas)
        map.setImageBitmap(bitmap)

        list = findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        list.adapter = FoldingListAdapter(this, WORLD)
    }
}