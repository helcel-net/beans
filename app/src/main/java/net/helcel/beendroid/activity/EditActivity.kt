package net.helcel.beendroid.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import net.helcel.beendroid.R
import net.helcel.beendroid.activity.adapter.ViewPagerAdapter
import net.helcel.beendroid.helper.createActionBar


class EditActivity : AppCompatActivity() {

    private lateinit var viewPager : ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit)
        createActionBar(this, getString(R.string.action_edit))


        viewPager = findViewById(R.id.pager)
        tabLayout = findViewById(R.id.tab)

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabs()[position]
        }.attach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }


}