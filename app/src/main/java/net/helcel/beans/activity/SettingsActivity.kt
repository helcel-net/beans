package net.helcel.beans.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import net.helcel.beans.R
import net.helcel.beans.activity.fragment.AboutFragment
import net.helcel.beans.activity.fragment.LicenseFragment
import net.helcel.beans.activity.fragment.SettingsFragment
import net.helcel.beans.helper.Theme.createActionBar

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
        createActionBar(this, getString(R.string.action_settings))

        // Populate activity with settings fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_view, SettingsFragment(), getString(R.string.action_settings))
            .commit()

        // Change title in action bar according to current fragment
        supportFragmentManager.addFragmentOnAttachListener { _, _ ->
            supportActionBar?.title =
                supportFragmentManager.findFragmentById(R.id.fragment_view).let {
                    when (it) {
                        is LicenseFragment -> getString(R.string.licenses)
                        is AboutFragment -> getString(R.string.about)
                        else -> getString(R.string.action_settings)
                    }
                }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Configure on back pressed
        supportFragmentManager.findFragmentById(R.id.fragment_view).let {
            when (it) {
                is LicenseFragment, is AboutFragment -> {
                    supportFragmentManager.beginTransaction()
                        .remove(it)
                        .commit()
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragment_view,
                            SettingsFragment(),
                            getString(R.string.action_settings)
                        )
                        .commit()
                    supportActionBar?.title = getString(R.string.action_settings)
                }

                else -> {
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}