package net.helcel.beans.activity.fragment

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import net.helcel.beans.R
import net.helcel.beans.countries.GeoLocImporter


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
        val ctx = requireContext()
        findPreference<Preference>(getString(R.string.key_regional))?.setOnPreferenceChangeListener { _, key ->
            when (key as String) {
                ctx.getString(R.string.off) -> GeoLocImporter.clearStates()
                ctx.getString(R.string.on) -> GeoLocImporter.importStates(ctx, true)
            }
            true
        }

        // Select Light/Dark/System Mode
        findPreference<Preference>(getString(R.string.key_theme))?.setOnPreferenceChangeListener { _, key ->
            setTheme(ctx, key as String)
        }

        // Open license fragment
        findPreference<Preference>(getString(R.string.licenses))?.setOnPreferenceClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_view, LicenseFragment(), getString(R.string.licenses))
                .commit()
            true
        }

        // Open about fragment
        findPreference<Preference>(getString(R.string.about))?.setOnPreferenceClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_view, AboutFragment(), getString(R.string.about))
                .commit()
            true
        }

    }

    companion object {
        fun setTheme(ctx: Context, key: String?): Boolean {
            AppCompatDelegate.setDefaultNightMode(
                when (key) {
                    ctx.getString(R.string.system) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    ctx.getString(R.string.light) -> AppCompatDelegate.MODE_NIGHT_NO
                    ctx.getString(R.string.dark) -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            )
            return true
        }
    }
}