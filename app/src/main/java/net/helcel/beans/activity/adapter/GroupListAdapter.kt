package net.helcel.beans.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beans.R
import net.helcel.beans.activity.fragment.EditGroupAddFragment
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.Groups
import net.helcel.beans.helper.Theme.getContrastColor

class GroupListAdapter(
    private val activity: FragmentActivity,
    private val selectDialog: DialogFragment
) : RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_group, parent, false)
        return GroupViewHolder(view, activity, selectDialog)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, pos: Int) {
        holder.bind(Data.groups.getGroupFromPos(pos))
    }

    override fun getItemCount(): Int {
        return Data.groups.size()
    }

    inner class GroupViewHolder(
        itemView: View,
        private val activity: FragmentActivity,
        private val selectDialog: DialogFragment
    ) : RecyclerView.ViewHolder(itemView) {
        private val color: Button = itemView.findViewById(R.id.group_color)
        private val entries: TextView = itemView.findViewById(R.id.name)
        private lateinit var dialogFragment: EditGroupAddFragment
        fun bind(entry: Pair<Int, Groups.Group>) {
            color.text = entry.second.name
            dialogFragment = EditGroupAddFragment(entry.first, {
                val newEntry = Data.groups.getGroupFromKey(entry.first)
                color.text = newEntry.name
                val newEntryColor = newEntry.color.color
                val contrastNewEntryColor =
                    getContrastColor(newEntryColor)
                color.setBackgroundColor(newEntryColor)
                color.setTextColor(contrastNewEntryColor)
                entries.setTextColor(contrastNewEntryColor)
                entries.text = "0"
            }, {
                notifyItemRemoved(it)
            })

            val entryColor = entry.second.color.color
            val contrastEntryColor = getContrastColor(entryColor)
            color.setBackgroundColor(entryColor)
            color.setTextColor(contrastEntryColor)
            entries.setTextColor(contrastEntryColor)
            entries.text = Data.visits.countVisited(entry.first).toString()

            color.setOnClickListener {
                Data.selected_group = entry.second
                selectDialog.dismiss()
            }
            color.setOnLongClickListener {
                dialogFragment.show(
                    activity.supportFragmentManager,
                    "AddColorDialogFragment"
                )
                true
            }
        }
    }
}