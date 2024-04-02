package net.helcel.beans.activity.fragment

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import net.helcel.beans.R


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)


        // Select Light/Dark/System Mode
        val themePreference = findPreference<Preference>(getString(R.string.key_theme))
        themePreference?.setOnPreferenceChangeListener { _, key ->
            setTheme(requireContext(), key as String)
        }

        // Open license fragment
        val licensesPreference = findPreference<Preference>(getString(R.string.licenses))
        licensesPreference?.setOnPreferenceClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_view, LicenseFragment(), getString(R.string.licenses))
                .commit()
            true
        }

        // Open about fragment
        val aboutPreference = findPreference<Preference>(getString(R.string.about))
        aboutPreference?.setOnPreferenceClickListener {
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