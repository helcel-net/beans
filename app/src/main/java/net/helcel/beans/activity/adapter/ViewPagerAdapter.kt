package net.helcel.beans.activity.adapter

import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import net.helcel.beans.activity.fragment.EditPlaceFragment
import kotlin.math.max

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val viewPager: ViewPager2
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragmentList: MutableList<EditPlaceFragment> = ArrayList()

    fun addFragment(src: EditPlaceFragment?, target: EditPlaceFragment) {
        val idx = fragmentList.indexOf(src)
        viewPager.currentItem = max(0, idx)
        if (src != null && idx >= 0) {
            fragmentList.subList(idx + 1, fragmentList.size).clear()
        }
        fragmentList.add(target)
        notifyItemRangeChanged(max(0, idx), fragmentList.size)
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

    fun refreshColors(colorDrawable: ColorDrawable) {
        fragmentList.forEach{ it.refreshColors(colorDrawable)}
    }
}


