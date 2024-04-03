package net.helcel.beans.activity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beans.activity.fragment.EditGroupAddFragment
import net.helcel.beans.databinding.ItemListGroupBinding
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.Groups
import net.helcel.beans.helper.Theme.getContrastColor

class GroupListAdapter(
    private val activity: FragmentActivity,
    private val selectDialog: DialogFragment
) : RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding =
            ItemListGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding, activity, selectDialog)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, pos: Int) {
        holder.bind(Data.groups.getGroupFromPos(pos))
    }

    override fun getItemCount(): Int {
        return Data.groups.size()
    }

    inner class GroupViewHolder(
        private val _binding: ItemListGroupBinding,
        private val activity: FragmentActivity,
        private val selectDialog: DialogFragment
    ) : RecyclerView.ViewHolder(_binding.root) {
        private lateinit var dialogFragment: EditGroupAddFragment
        fun bind(entry: Pair<Int, Groups.Group>) {
            _binding.groupColor.text = entry.second.name
            dialogFragment = EditGroupAddFragment(entry.first, {
                val newEntry = Data.groups.getGroupFromKey(entry.first)
                _binding.groupColor.text = newEntry.name
                val newEntryColor = newEntry.color.color
                val contrastNewEntryColor =
                    getContrastColor(newEntryColor)
                _binding.groupColor.setBackgroundColor(newEntryColor)
                _binding.groupColor.setTextColor(contrastNewEntryColor)
                _binding.name.setTextColor(contrastNewEntryColor)
                _binding.name.text = "0"
            }, {
                notifyItemRemoved(it)
            })

            val entryColor = entry.second.color.color
            val contrastEntryColor = getContrastColor(entryColor)
            _binding.groupColor.setBackgroundColor(entryColor)
            _binding.groupColor.setTextColor(contrastEntryColor)
            _binding.name.setTextColor(contrastEntryColor)
            _binding.name.text = Data.visits.countVisited(entry.first).toString()

            _binding.groupColor.setOnClickListener {
                Data.selected_group = entry.second
                selectDialog.dismiss()
            }
            _binding.groupColor.setOnLongClickListener {
                dialogFragment.show(
                    activity.supportFragmentManager,
                    "AddColorDialogFragment"
                )
                true
            }
        }
    }
}