package net.helcel.beendroid.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beendroid.activity.adapter.GeolocListAdapter
import net.helcel.beendroid.countries.World
import net.helcel.beendroid.databinding.FragmentEditPlacesBinding

class EditPlaceFragment : Fragment() {
    private var _binding: FragmentEditPlacesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPlacesBinding.inflate(inflater, container, false)

        binding.list.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.list.adapter = GeolocListAdapter(requireActivity(), World.WWW.children)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}