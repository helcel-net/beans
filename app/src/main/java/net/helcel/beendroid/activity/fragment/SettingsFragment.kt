package net.helcel.beendroid.activity.fragment

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import net.helcel.beendroid.R


class SettingsFragment: PreferenceFragmentCompat() {
    private lateinit var themePreference: ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)

        // Select Light/Dark/System Mode
        themePreference = findPreference(getString(R.string.key_theme))!!
        themePreference.setOnPreferenceChangeListener { _, key ->
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
        fun setTheme(context: Context, key: String?): Boolean {
            when (key) {
                context.getString(R.string.system) -> {
                    // Set SYSTEM Theme
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    return true
                }

                context.getString(R.string.light) -> {
                    // Set LIGHT Theme
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    return true
                }

                context.getString(R.string.dark) -> {
                    // Set DARK Theme
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    return true
                }

                else -> {
                    return false
                }
            }
        }
    }
}