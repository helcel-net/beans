package net.helcel.beans.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import net.helcel.beans.R
import net.helcel.beans.countries.GeoLoc
import net.helcel.beans.countries.GeoLoc.LocType
import net.helcel.beans.countries.World
import net.helcel.beans.databinding.ItemListGroupBinding
import net.helcel.beans.helper.AUTO_GROUP
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.Groups
import net.helcel.beans.helper.Settings
import net.helcel.beans.helper.Theme.getContrastColor

class StatsListAdapter(private val stats: RecyclerView, private val total: MaterialTextView) :
    RecyclerView.Adapter<StatsListAdapter.StatsViewHolder>() {
    private val unit = "km²"

    private var locMode = LocType.WORLD
    private lateinit var ctx: Context
    private var countMode: Boolean = true
    private var initialSum: Int = 0

    private val wwwTotal: List<GeoLoc> = World.WWW.children.toList()
    private val countryTotal: List<GeoLoc> = World.WWW.children.flatMap { it.children }
    private val stateTotal: List<GeoLoc> =
        World.WWW.children.flatMap { it.children.flatMap { itt -> itt.children } }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        ctx = parent.context
        val binding =
            ItemListGroupBinding.inflate(LayoutInflater.from(ctx), parent, false)

        return StatsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatsViewHolder, pos: Int) {
        initialSum += if (pos == itemCount - 1) {
            holder.bind(
                Pair(
                    AUTO_GROUP,
                    Groups.Group(AUTO_GROUP, ctx.getString(R.string.uncategorized))
                )
            )
        } else {
            holder.bind(Data.groups.getGroupFromPos(pos))
        }
        val unitNow = if (!countMode) unit else ""
        total.text = Settings.getStats(ctx, initialSum, getTotal(), unitNow)
    }

    override fun getItemCount(): Int {
        return Data.groups.size() + 1
    }

    private fun getTotal(): Int {
        return if (countMode) {
            when (locMode) {
                LocType.WORLD -> wwwTotal.size
                LocType.COUNTRY -> countryTotal.size
                LocType.STATE -> stateTotal.size
                else -> 0
            }
        } else {
            when (locMode) {
                LocType.WORLD -> wwwTotal.sumOf { it.area }
                LocType.COUNTRY -> countryTotal.sumOf { it.area }
                LocType.STATE -> stateTotal.sumOf { it.area }
                else -> 0
            }
        }
    }

    fun refreshMode(mode: LocType) {
        val sum = (0 until itemCount).map {
            val viewHolder = stats.findViewHolderForAdapterPosition(it) as? StatsViewHolder
            viewHolder?.refresh(mode)
        }.reduce { acc, i -> acc?.plus((i ?: 0)) }
        val unitNow = if (!countMode) unit else ""
        total.text = Settings.getStats(ctx, sum, getTotal(), unitNow)
    }

    fun invertCountMode() {
        countMode = !countMode
        refreshMode(locMode)
    }

    inner class StatsViewHolder(
        private val _binding: ItemListGroupBinding
    ) : RecyclerView.ViewHolder(_binding.root) {

        private lateinit var data: Pair<Int, Groups.Group>

        private lateinit var wwwCount: List<GeoLoc>
        private lateinit var countryCount: List<GeoLoc>
        private lateinit var stateCount: List<GeoLoc>

        fun bind(entry: Pair<Int, Groups.Group>): Int {
            data = entry
            _binding.groupColor.text = entry.second.name

            val entryColor = data.second.color.color
            val contrastEntryColor = getContrastColor(entryColor)
            _binding.groupColor.setBackgroundColor(entryColor)
            _binding.groupColor.setTextColor(contrastEntryColor)
            _binding.name.setTextColor(contrastEntryColor)

            _binding.groupColor.setOnClickListener { invertCountMode() }
            compute()
            return refresh(locMode)
        }

        private fun compute() {
            val visited = Data.visits.getVisitedByValue(data.first)
            wwwCount = World.WWW.children.filter { it.code in visited }
            countryCount =
                World.WWW.children.map { it.children.filter { itt -> itt.code in visited } }
                    .flatten()
            stateCount =
                World.WWW.children.map { it.children.map { itt -> itt.children.filter { ittt -> ittt.code in visited } } }
                    .flatten().flatten()
        }

        fun refresh(mode: LocType): Int {
            locMode = mode
            return if (countMode) {
                val count = when (locMode) {
                    LocType.WORLD -> wwwCount.size
                    LocType.COUNTRY -> countryCount.size
                    LocType.STATE -> stateCount.size
                    else -> -1
                }
                _binding.name.text = count.toString()
                count
            } else {
                val area = when (locMode) {
                    LocType.WORLD -> wwwCount.sumOf { it.area }
                    LocType.COUNTRY -> countryCount.sumOf { it.area }
                    LocType.STATE -> stateCount.sumOf { it.area }
                    else -> -1
                }
                _binding.name.text = ctx.getString(R.string.number_with_unit, area, unit)
                area
            }
        }

    }
}