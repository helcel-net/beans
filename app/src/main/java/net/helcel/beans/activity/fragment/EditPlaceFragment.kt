package net.helcel.beans.activity.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beans.activity.adapter.GeolocListAdapter
import net.helcel.beans.activity.adapter.GeolocListAdapter.FoldingListViewHolder
import net.helcel.beans.activity.adapter.ViewPagerAdapter
import net.helcel.beans.countries.GeoLoc
import net.helcel.beans.databinding.FragmentEditPlacesBinding

class EditPlaceFragment(val loc: GeoLoc, private val pager: ViewPagerAdapter, private val holder: FoldingListViewHolder? = null) : Fragment() {
    private lateinit var _binding: FragmentEditPlacesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPlacesBinding.inflate(inflater, container, false)

        _binding.list.setItemViewCacheSize(5)
        _binding.list.setHasFixedSize(true)
        _binding.list.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        _binding.list.adapter = GeolocListAdapter(this, loc, pager, holder)
        return _binding.root
    }

    fun refreshColors(colorDrawable: ColorDrawable) {
        (_binding.list.adapter as GeolocListAdapter?)?.refreshColors(colorDrawable)
    }
}