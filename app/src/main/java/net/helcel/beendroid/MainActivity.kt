package net.helcel.beendroid

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGImageView
import net.helcel.beendroid.R
import java.nio.charset.StandardCharsets


class MainActivity : AppCompatActivity() {

    private lateinit var map : SVGImageView
    private lateinit var list : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        map = findViewById(R.id.map)

        val cm = HashMap<Country,SVGWrapper>()
        Country.values().forEach { c->
            cm[c] = SVGWrapper(applicationContext,c,Level.ZERO).load()
        }

        val fm = cm.values.fold("") { acc, e -> acc + e.data }
        val svg = SVG.getFromString("<svg id=\"map\" xmlns=\"http://www.w3.org/2000/svg\" width=\"1200\" height=\"1200\" x=\"0\" y=\"0\" >"+fm+"</svg>")

        val bitmap = Bitmap.createBitmap(1200,1200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawRGB(255, 255, 255)

        svg.renderToCanvas(canvas)
        map.setImageBitmap(bitmap)

        list = findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        list.adapter = ListAdapter(Country.values().map{ it.code })
    }
}