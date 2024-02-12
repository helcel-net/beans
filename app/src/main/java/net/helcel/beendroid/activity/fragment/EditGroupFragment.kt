package net.helcel.beendroid.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beendroid.activity.adapter.GroupListAdapter
import net.helcel.beendroid.databinding.FragmentEditGroupsBinding
import net.helcel.beendroid.helper.groups

class EditGroupFragment : Fragment() {
        private var _binding: FragmentEditGroupsBinding? = null
        private val binding get() = _binding!!
        private lateinit var listadapt : GroupListAdapter

        override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
        ): View {
                _binding = FragmentEditGroupsBinding.inflate(inflater, container, false)

                listadapt = GroupListAdapter(requireActivity())
                binding.addGroup.setOnClickListener {
                        val dialogFragment = EditGroupAddFragment {
                                listadapt.notifyItemInserted(groups!!.findGroupPos(it))
                        }
                        dialogFragment.show(requireActivity().supportFragmentManager, "AddColorDialogFragment")

                }

                binding.list.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                binding.list.adapter = listadapt
                return binding.root
        }

        override fun onDestroyView() {
                super.onDestroyView()
                _binding = null
        }
}