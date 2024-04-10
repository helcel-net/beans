package net.helcel.beans.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import net.helcel.beans.R
import net.helcel.beans.activity.adapter.ViewPagerAdapter
import net.helcel.beans.activity.fragment.EditGroupAddFragment
import net.helcel.beans.activity.fragment.EditPlaceColorFragment
import net.helcel.beans.activity.fragment.EditPlaceFragment
import net.helcel.beans.countries.World
import net.helcel.beans.databinding.ActivityEditBinding
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.DialogCloser
import net.helcel.beans.helper.Settings
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (Settings.isSingleGroup(this)) {
            menuInflater.inflate(R.menu.menu_edit, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_color -> {
                Data.groups.getUniqueEntry()?.let { group ->
                    EditGroupAddFragment(group.key, {
                        (_binding.pager.adapter as ViewPagerAdapter?)?.refreshColors(group.color)
                    }, {}, false).show(supportFragmentManager, "AddColorDialogFragment")
                }
            }
            else -> finish()
        }
        return super.onOptionsItemSelected(item)
    }


}