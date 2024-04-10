package net.helcel.beans.activity.fragment

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.helcel.beans.R
import net.helcel.beans.countries.GeoLocImporter
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.DialogCloser
import net.helcel.beans.helper.Settings


class SettingsFragment : PreferenceFragmentCompat(), DialogCloser {
    private var savedInstanceState: Bundle? = null
    private var rootKey: String? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        this.savedInstanceState = savedInstanceState
        this.rootKey = rootKey

        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
        val ctx = requireContext()

        // Select Light/Dark/System Mode
        findPreference<Preference>(getString(R.string.key_theme))?.setOnPreferenceChangeListener { _, key ->
            setTheme(ctx, key as String)
        }

        // Select map projection
        findPreference<Preference>(getString(R.string.key_projection))?.setOnPreferenceChangeListener { _, key ->
            Settings.refreshProjection()
        }

        // Toggle groups
        findPreference<Preference>(getString(R.string.key_group))?.setOnPreferenceChangeListener { _, key ->
            if (key as String == ctx.getString(R.string.off)) {
                val fragment = EditPlaceColorFragment(this, true)
                fragment.show(
                    this.parentFragmentManager,
                    "AddColorDialogFragment"
                )
                false
            } else {
                true
            }
        }

        // Toggle regional geolocs
        findPreference<Preference>(getString(R.string.key_regional))?.setOnPreferenceChangeListener { _, key ->
            when (key as String) {
                ctx.getString(R.string.off) -> {
                    MaterialAlertDialogBuilder(requireActivity())
                        .setMessage(R.string.delete_regions)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            GeoLocImporter.clearStates()
                            PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString(
                                ctx.getString(R.string.key_regional),
                                ctx.getString(R.string.off)
                            ).apply()
                            refreshPreferences()
                        }
                        .setNegativeButton(android.R.string.cancel) { _, _ -> }
                        .show()
                    false
                }

                ctx.getString(R.string.on) -> {
                    GeoLocImporter.importStates(ctx, true)
                    true
                }

                else -> false
            }
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

    override fun onDialogDismiss(clear: Boolean) {
        // When turning groups off, select one group to keep and reassign everything
        Data.selected_group?.let { selectedGroup ->
            // Reassign all visited that are not to selectedGroup to selectedGroup
            Data.visits.reassignAllVisitedToGroup(selectedGroup.key)

            // Delete all groups that are not selectedGroup
            Data.groups.deleteAllExcept(selectedGroup.key)

            // Save and clear global variables
            Data.saveData()
            Data.selected_geoloc = null
            Data.selected_group = null

            // Actually change preference
            val ctx = requireContext()
            val sp = PreferenceManager.getDefaultSharedPreferences(ctx)
            sp.edit().putString(ctx.getString(R.string.key_group), ctx.getString(R.string.off))
                .apply()

            // Refresh entire preference fragment to reflect changes
            refreshPreferences()
        }
    }

    private fun refreshPreferences() {
        preferenceScreen.removeAll()
        onCreatePreferences(savedInstanceState, rootKey)
    }
}