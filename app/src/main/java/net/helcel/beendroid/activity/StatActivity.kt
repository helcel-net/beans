package net.helcel.beendroid.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import net.helcel.beendroid.R
import net.helcel.beendroid.countries.World
import net.helcel.beendroid.helper.colorWrapper
import net.helcel.beendroid.helper.createActionBar
import net.helcel.beendroid.helper.groups
import net.helcel.beendroid.helper.visits


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

        chart.centerText = "Country Area"

        chart.setDrawCenterText(true)
        chart.isDrawHoleEnabled = true
        chart.setTransparentCircleColor(Color.TRANSPARENT)
        chart.setHoleColor(Color.TRANSPARENT)
        chart.setCenterTextColor(colorWrapper(this,android.R.attr.colorForeground).color)
        chart.setTransparentCircleAlpha(0)
        chart.holeRadius = 40F
        chart.transparentCircleRadius = 40F
        chart.rotationAngle = 0F
        chart.isRotationEnabled = false
        chart.isHighlightPerTapEnabled = false

        chart.legend.isEnabled = false

        bind()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }


    private fun bind() {
        val entries = ArrayList<PieEntry>()
        val VIS_continents = World.WWW.children.groupBy { visits?.getVisited(it)?:0 }.map { Pair(it.key,it.value.map { c-> c.area }.fold(0){ acc, i-> acc+i}) }
        val VIS_country = World.WWW.children.map { it.children }.flatten().groupBy { visits!!.getVisited(it) }.map { Pair(it.key,it.value.map { c-> c.area }.fold(0){ acc, i-> acc+i}) }
        val vis = VIS_country
        Log.d("VIS",vis.toString())
        val max = vis.map{it.second}.fold(0) {acc, i -> acc+i}
        vis.forEach {
            entries.add(PieEntry(it.second.toFloat().div(max.toFloat()),groups!!.getGroupFromKey(it.first)?.name?:"None"))
        }
        val dataSet = PieDataSet(entries, "GG1")

        dataSet.valueTextColor = Color.BLACK
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f


        dataSet.setDrawIcons(true)
        dataSet.colors =  vis.map{ (groups!!.getGroupFromKey(it.first)?.color?:ColorDrawable(Color.WHITE)).color }.toList()

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