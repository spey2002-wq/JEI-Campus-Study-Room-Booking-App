package com.jei.campusbooking.ui.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jei.campusbooking.R
import com.jei.campusbooking.databinding.FragmentRoomDetailBinding
import com.jei.campusbooking.util.DateUtil
import kotlinx.coroutines.launch

/** Screen 2 - room information and the booked slots of the selected day. */
class RoomDetailFragment : Fragment() {

    private var _binding: FragmentRoomDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoomDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TimeSlotAdapter()
        binding.rvTimeSlots.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTimeSlots.adapter = adapter

        binding.btnPrevDay.setOnClickListener { viewModel.previousDay() }
        binding.btnNextDay.setOnClickListener { viewModel.nextDay() }

        binding.btnBookRoom.setOnClickListener {
            findNavController().navigate(
                R.id.action_roomDetail_to_bookingApply,
                bundleOf(
                    "roomId" to viewModel.roomId(),
                    "selectedDate" to DateUtil.dateToDb(viewModel.selectedDate.value)
                )
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.room.collect { room ->
                        if (room == null) return@collect
                        val ctx = requireContext()
                        binding.tvDetailName.text = room.roomName
                        binding.tvDetailBuilding.text = room.building
                        binding.tvDetailFloor.text = getString(R.string.floor_format, room.floor)
                        binding.tvDetailCapacity.text = getString(R.string.people_format, room.capacity)
                        binding.tvDetailEquipment.text =
                            room.equipment.split(",").joinToString(", ") { it.trim() }

                        val (label, color) = if (room.isAvailable) {
                            R.string.badge_available to R.color.badge_available
                        } else {
                            R.string.badge_closed to R.color.badge_closed
                        }
                        binding.tvDetailStatus.setText(label)
                        binding.tvDetailStatus.backgroundTintList =
                            ColorStateList.valueOf(ContextCompat.getColor(ctx, color))

                        // A closed room cannot be booked
                        binding.btnBookRoom.isEnabled = room.isAvailable
                        binding.tvClosedHint.visibility =
                            if (room.isAvailable) View.GONE else View.VISIBLE
                    }
                }
                launch {
                    viewModel.selectedDate.collect { date ->
                        binding.tvSelectedDate.text = DateUtil.prettyDate(date)
                    }
                }
                launch {
                    viewModel.bookedSlots.collect { slots ->
                        adapter.submitList(slots)
                        binding.tvNoSlots.visibility =
                            if (slots.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvTimeSlots.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
