package net.helcel.beans.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import net.helcel.beans.R
import net.helcel.beans.activity.adapter.ViewPagerAdapter
import net.helcel.beans.activity.fragment.EditPlaceFragment
import net.helcel.beans.countries.World
import net.helcel.beans.databinding.ActivityEditBinding
import net.helcel.beans.helper.Theme.createActionBar


class EditActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditBinding.inflate(layoutInflater)

        setContentView(_binding.root)
        createActionBar(this, getString(R.string.action_edit))

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle, _binding.pager)
        _binding.pager.adapter = adapter
        adapter.addFragment(null, EditPlaceFragment(World.WWW, adapter))

        TabLayoutMediator(_binding.tab, _binding.pager) { tab, position ->
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