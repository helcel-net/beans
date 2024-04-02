package net.helcel.beans.activity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import net.helcel.beans.activity.fragment.EditPlaceFragment

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val viewPager: ViewPager2
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragmentList: MutableList<EditPlaceFragment> = ArrayList()

    fun addFragment(src: EditPlaceFragment?, target: EditPlaceFragment) {
        if (src != null) {
            while (fragmentList.last() != src) {
                fragmentList.removeLast()
                notifyItemRemoved(fragmentList.size)
            }
        }
        println(src.toString() + " -  " + target.toString())
        fragmentList.add(target)
        notifyItemInserted(fragmentList.size)
        viewPager.currentItem = fragmentList.size - 1
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    fun backPressed(): Boolean {
        if (viewPager.currentItem == 0) {
            return false
        }
        val target = viewPager.currentItem
        while (fragmentList.size > target) {
            fragmentList.removeLast()
            notifyItemRemoved(fragmentList.size)
        }
        return true
    }

    fun getLabel(pos: Int): String {
        return fragmentList[pos].loc.fullName
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}


