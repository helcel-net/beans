package net.helcel.beans.activity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beans.countries.World
import net.helcel.beans.databinding.ItemListGroupBinding
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.Groups
import net.helcel.beans.helper.Theme.getContrastColor

class StatsListAdapter : RecyclerView.Adapter<StatsListAdapter.StatsViewHolder>() {

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

    inner class StatsViewHolder(
        private val _binding: ItemListGroupBinding
    ) : RecyclerView.ViewHolder(_binding.root) {

        private lateinit var data: Pair<Int, Groups.Group>
        private var countMode: Boolean = true
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
                refresh()
            }
            refresh()
        }

        private fun refresh() {
            val visited = Data.visits.getVisitedByValue(data.first)
            val wwwCount = World.WWW.children.filter { it.code in visited }
            val countryCount =
                World.WWW.children.map { it.children.filter { itt -> itt.code in visited } }
                    .flatten()
            val stateCount =
                World.WWW.children.map { it.children.map { itt -> itt.children.filter { ittt -> ittt.code in visited } } }
                    .flatten().flatten()
            if (countMode) {
                _binding.name.text = "${wwwCount.size} | ${countryCount.size} | ${stateCount.size}"
            } else {
                _binding.name.text =
                    "${wwwCount.sumOf { it.area }} | ${countryCount.sumOf { it.area }} | ${stateCount.sumOf { it.area }}"

            }

        }

    }
}