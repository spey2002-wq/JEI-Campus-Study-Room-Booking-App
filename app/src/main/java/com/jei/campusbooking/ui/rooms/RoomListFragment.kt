package com.jei.campusbooking.ui.rooms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jei.campusbooking.R
import com.jei.campusbooking.databinding.FragmentRoomListBinding
import kotlinx.coroutines.launch

/** Screen 1 - browse / search / filter study rooms. */
class RoomListFragment : Fragment() {

    private var _binding: FragmentRoomListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoomListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RoomAdapter { room ->
            findNavController().navigate(
                R.id.action_roomList_to_roomDetail,
                bundleOf("roomId" to room.roomId)
            )
        }
        binding.rvRooms.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRooms.adapter = adapter

        binding.etSearch.doAfterTextChanged { viewModel.setQuery(it?.toString().orEmpty()) }

        binding.chipGroupCapacity.setOnCheckedStateChangeListener { _, checkedIds ->
            val min = when (checkedIds.firstOrNull()) {
                R.id.chip_2 -> 2
                R.id.chip_4 -> 4
                R.id.chip_8 -> 8
                else -> 0
            }
            viewModel.setMinCapacity(min)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.rooms.collect { rooms ->
                    adapter.submitList(rooms.orEmpty())
                    binding.tvEmpty.visibility =
                        if (rooms != null && rooms.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvRooms.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
