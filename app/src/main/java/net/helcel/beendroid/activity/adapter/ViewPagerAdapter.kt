package net.helcel.beendroid.activity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.helcel.beendroid.activity.fragment.EditGroupFragment
import net.helcel.beendroid.activity.fragment.EditPlaceFragment

private val tabArray = arrayOf(
    "Places",
    "Groups",
)
class ViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return tabArray.size
    }

    fun getTabs() : Array<String> {
        return tabArray
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return EditPlaceFragment()
            1 -> return EditGroupFragment()
        }
        return EditPlaceFragment()
    }
}


