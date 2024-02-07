package net.helcel.beendroid.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.helcel.beendroid.R
import net.helcel.beendroid.databinding.FragmentLicenseBinding
import com.mikepenz.aboutlibraries.LibsBuilder

class LicenseFragment: Fragment() {
    private var _binding: FragmentLicenseBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}