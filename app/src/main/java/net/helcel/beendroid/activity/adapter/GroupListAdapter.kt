package net.helcel.beendroid.activity.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beendroid.R
import net.helcel.beendroid.activity.fragment.EditGroupAddFragment
import net.helcel.beendroid.helper.getContrastColor
import net.helcel.beendroid.helper.groups

class GroupListAdapter(val activity: FragmentActivity) : RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : GroupViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_list_group, parent, false)
        return GroupViewHolder(view, activity)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, pos: Int) {
        holder.bind(groups!!.getGroupFromPos(pos))
    }

    override fun getItemCount(): Int {
        return groups!!.size()
    }

    class GroupViewHolder(itemView: View, val activity: FragmentActivity) : RecyclerView.ViewHolder(itemView) {
        private val color: Button = itemView.findViewById(R.id.group_color)

        fun bind(entry: Pair<Int, Pair<String, ColorDrawable>>) {
            color.text = entry.second.first
            color.setBackgroundColor(entry.second.second.color)
            color.setTextColor(getContrastColor(entry.second.second.color))
            color.setOnClickListener {
                    val dialogFragment = EditGroupAddFragment(entry.first) {
                        val newEntry = groups!!.getGroupFromKey(entry.first)!!
                        color.text = newEntry.first
                        color.setBackgroundColor(newEntry.second.color)
                        color.setTextColor(getContrastColor(newEntry.second.color))
                    }
                    dialogFragment.show(activity.supportFragmentManager, "AddColorDialogFragment")
            }
        }
    }
}