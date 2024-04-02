package net.helcel.beans.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mikepenz.aboutlibraries.LibsBuilder
import net.helcel.beans.R
import net.helcel.beans.databinding.FragmentLicenseBinding

class LicenseFragment : Fragment() {
    private lateinit var _binding: FragmentLicenseBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLicenseBinding.inflate(inflater, container, false)

        val librariesFragment = LibsBuilder()
            .withLicenseShown(true)
            .supportFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.license_fragment_view, librariesFragment)
            .commit()

        return _binding.root
    }
}