package net.helcel.beans.activity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beans.countries.GeoLoc
import net.helcel.beans.countries.World
import net.helcel.beans.databinding.ItemListGroupBinding
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.Groups
import net.helcel.beans.helper.Theme.getContrastColor

class StatsListAdapter(private val stats: RecyclerView) :
    RecyclerView.Adapter<StatsListAdapter.StatsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        val binding =
            ItemListGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return StatsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatsViewHolder, pos: Int) {
        holder.bind(Data.groups.getGroupFromPos(pos))
    }

    override fun getItemCount(): Int {
        return Data.groups.size()
    }

    fun refreshMode(mode: String) {
        for (i in 0 until itemCount) {
            val viewHolder = stats.findViewHolderForAdapterPosition(i) as? StatsViewHolder
            viewHolder?.refresh(mode)
        }

    }

    inner class StatsViewHolder(
        private val _binding: ItemListGroupBinding
    ) : RecyclerView.ViewHolder(_binding.root) {

        private lateinit var data: Pair<Int, Groups.Group>
        private var countMode: Boolean = true
        private var locMode: String = "World"

        private lateinit var wwwCount: List<GeoLoc>
        private lateinit var countryCount: List<GeoLoc>
        private lateinit var stateCount: List<GeoLoc>
        fun bind(entry: Pair<Int, Groups.Group>) {
            data = entry
            _binding.groupColor.text = entry.second.name

            val entryColor = data.second.color.color
            val contrastEntryColor = getContrastColor(entryColor)
            _binding.groupColor.setBackgroundColor(entryColor)
            _binding.groupColor.setTextColor(contrastEntryColor)
            _binding.name.setTextColor(contrastEntryColor)

            _binding.groupColor.setOnClickListener {
                countMode = !countMode
                refresh(locMode)
            }
            compute()
            refresh(locMode)
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

        fun refresh(mode: String) {
            locMode = mode
            if (countMode) {
                _binding.name.text = when (locMode) {
                    "World" -> wwwCount.size
                    "Country" -> countryCount.size
                    "Region" -> stateCount.size
                    else -> "?"
                }.toString()
            } else {
                _binding.name.text = when (locMode) {
                    "World" -> wwwCount.sumOf { it.area }
                    "Country" -> countryCount.sumOf { it.area }
                    "Region" -> stateCount.sumOf { it.area }
                    else -> "?"
                }.toString()
            }

        }

    }
}