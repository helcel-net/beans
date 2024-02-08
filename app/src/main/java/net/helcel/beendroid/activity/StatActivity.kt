package net.helcel.beendroid.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import net.helcel.beendroid.R
import net.helcel.beendroid.countries.World
import net.helcel.beendroid.helper.createActionBar
import net.helcel.beendroid.helper.visited


class StatActivity : AppCompatActivity() {

    private lateinit var chart : PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_stat)
        createActionBar(this, getString(R.string.action_stat))


        chart = findViewById(R.id.chart)
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.setExtraOffsets(5F, 10F, 5F, 5F)

        chart.setDragDecelerationFrictionCoef(0.95f)

        chart.centerText = "Country Area"

        chart.isDrawHoleEnabled = true
        chart.setTransparentCircleColor(Color.TRANSPARENT)
        chart.setHoleColor(Color.TRANSPARENT)
        chart.setCenterTextColor(Color.WHITE)
        chart.setTransparentCircleAlpha(0)
        chart.holeRadius = 40F
        chart.transparentCircleRadius = 45F
        chart.setDrawCenterText(true)
        chart.setRotationAngle(0F)
        chart.isRotationEnabled = true
        chart.isHighlightPerTapEnabled = false



        // chart.spin(2000, 0, 360);
        val l: Legend = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7F
        l.yEntrySpace = 0F
        l.yOffset = 0F

        chart.setEntryLabelColor(Color.WHITE)
        chart.setEntryLabelTextSize(12f)

        bind()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }


    private fun bind() {
        val entries = ArrayList<PieEntry>()
        val VIS_continents = World.WWW.children.groupBy { visited!!.getVisited(it) }.map { Pair(it.key,it.value.map { c-> c.area }.fold(0){acc,i-> acc+i}) }
        val VIS_country = World.WWW.children.map { it.children }.flatten().groupBy { visited!!.getVisited(it) }.map { Pair(it.key,it.value.map { c-> c.area }.fold(0){acc,i-> acc+i}) }
        val vis = VIS_country
        Log.d("VIS",vis.toString())
        val max = vis.fold(0,) {acc, i -> acc+i.second}
        vis.forEach {
            entries.add(PieEntry(it.second.toFloat().div(max.toFloat()),it.first.toString()))
        }
        val dataSet = PieDataSet(entries, "GG1")

        dataSet.valueTextColor = Color.BLACK
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors
        val colors = ArrayList<Int>()

        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)

        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)

        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)

        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)

        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)

        colors.add(ColorTemplate.getHoloBlue())

        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setDrawValues(false)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.BLACK)
        chart.setEntryLabelColor(Color.BLACK)
        chart.data = data
        chart.highlightValues(null)
        chart.invalidate()
    }




}