package net.helcel.beans.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import net.helcel.beans.R
import net.helcel.beans.activity.adapter.ViewPagerAdapter
import net.helcel.beans.activity.fragment.EditPlaceFragment
import net.helcel.beans.countries.World
import net.helcel.beans.helper.Theme.createActionBar


class EditActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit)
        createActionBar(this, getString(R.string.action_edit))


        viewPager = findViewById(R.id.pager)
        tabLayout = findViewById(R.id.tab)

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle, viewPager)
        viewPager.adapter = adapter
        adapter.addFragment(null, EditPlaceFragment(World.WWW, adapter))

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getLabel(position)
        }.attach()

        onBackPressedDispatcher.addCallback {
            if (!adapter.backPressed()) {
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }


}