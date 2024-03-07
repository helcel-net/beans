package net.helcel.beendroid.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.helcel.beendroid.R
import net.helcel.beendroid.activity.fragment.EditGroupAddFragment
import net.helcel.beendroid.helper.Groups
import net.helcel.beendroid.helper.getContrastColor
import net.helcel.beendroid.helper.groups
import net.helcel.beendroid.helper.saveData
import net.helcel.beendroid.helper.selected_group
import net.helcel.beendroid.helper.visits

class GroupListAdapter(private val activity: FragmentActivity, private val selectDialog: DialogFragment?) : RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : GroupViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_list_group, parent, false)
        return GroupViewHolder(view, activity, selectDialog)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, pos: Int) {
        holder.bind(groups!!.getGroupFromPos(pos))
    }

    override fun getItemCount(): Int {
        return groups!!.size()
    }

    inner class GroupViewHolder(itemView: View, private val activity: FragmentActivity, private val selectDialog: DialogFragment?) : RecyclerView.ViewHolder(itemView) {
        private val color: Button = itemView.findViewById(R.id.group_color)
        private val entries: TextView = itemView.findViewById(R.id.name)

        fun bind(entry: Pair<Int, Groups.Group>) {
            color.text = entry.second.name
            val entryColor = entry.second.color.color
            val contrastEntryColor = getContrastColor(entryColor)
            color.setBackgroundColor(entryColor)
            color.setTextColor(contrastEntryColor)
            entries.setTextColor(contrastEntryColor)
            entries.text = visits!!.countVisited(entry.first).toString()

            color.setOnClickListener {
                    if (selectDialog == null) {
                        val dialogFragment = EditGroupAddFragment(entry.first) {
                            val newEntry = groups!!.getGroupFromKey(entry.first)!!
                            color.text = newEntry.name
                            val newEntryColor = newEntry.color.color
                            val contrastNewEntryColor = getContrastColor(newEntryColor)
                            color.setBackgroundColor(newEntryColor)
                            color.setTextColor(contrastNewEntryColor)
                            entries.setTextColor(contrastNewEntryColor)
                            entries.text = "0"
                        }
                        dialogFragment.show(
                            activity.supportFragmentManager,
                            "AddColorDialogFragment"
                        )
                    } else {
                        selected_group = entry.second
                        selectDialog.dismiss()
                    }
            }

            color.setOnLongClickListener {
                if (selectDialog == null) {
                    MaterialAlertDialogBuilder(activity)
                        .setMessage(R.string.delete_group)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            // Remove all countries belonging to that group
                            val key = entry.first
                            visits!!.deleteVisited(key)

                            // Delete the group
                            val pos = groups!!.findGroupPos(key)
                            groups!!.deleteGroup(key)
                            saveData()
                            this@GroupListAdapter.notifyItemRemoved(pos)
                        }
                        .setNegativeButton(android.R.string.cancel) { _, _ -> }
                        .show()
                }
                true
            }
        }
    }
}