package com.jei.campusbooking.ui.booking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.jei.campusbooking.R
import com.jei.campusbooking.databinding.FragmentBookingApplyBinding
import com.jei.campusbooking.util.DateUtil
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

/** Screen 3 - choose date / time / purpose and submit the reservation. */
class BookingApplyFragment : Fragment() {

    private var _binding: FragmentBookingApplyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingApplyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etDate.setOnClickListener { pickDate() }
        binding.etStartTime.setOnClickListener {
            pickTime(viewModel.startTime.value ?: LocalTime.of(10, 0)) { viewModel.startTime.value = it }
        }
        binding.etEndTime.setOnClickListener {
            pickTime(viewModel.endTime.value ?: LocalTime.of(12, 0)) { viewModel.endTime.value = it }
        }

        binding.btnCancel.setOnClickListener { findNavController().navigateUp() }
        binding.btnSubmit.setOnClickListener {
            hideError()
            viewModel.submit(selectedPurpose())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.room.collect { room ->
                        if (room != null) {
                            binding.tvRoomSummary.text =
                                getString(R.string.room_summary_format, room.roomName, room.building)
                        }
                    }
                }
                launch {
                    viewModel.date.collect { binding.etDate.setText(DateUtil.prettyDate(it)) }
                }
                launch {
                    viewModel.startTime.collect {
                        binding.etStartTime.setText(it?.let(DateUtil::timeToDb).orEmpty())
                    }
                }
                launch {
                    viewModel.endTime.collect {
                        binding.etEndTime.setText(it?.let(DateUtil::timeToDb).orEmpty())
                    }
                }
                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            is BookingEvent.Error -> showError(getString(event.messageRes))
                            BookingEvent.Saved -> {
                                Snackbar.make(
                                    requireActivity().findViewById(android.R.id.content),
                                    R.string.booking_saved,
                                    Snackbar.LENGTH_LONG
                                ).show()
                                // Booking form + room detail are popped (see nav_graph popUpTo)
                                findNavController().navigate(R.id.action_bookingApply_to_myBookings)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun selectedPurpose(): String = when (binding.rgPurpose.checkedRadioButtonId) {
        R.id.rb_presentation -> getString(R.string.purpose_presentation)
        R.id.rb_self_study -> getString(R.string.purpose_self_study)
        R.id.rb_other -> getString(R.string.purpose_other)
        else -> getString(R.string.purpose_group_study)
    }

    private fun pickDate() {
        val current = viewModel.date.value
        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day -> viewModel.date.value = LocalDate.of(year, month + 1, day) },
            current.year, current.monthValue - 1, current.dayOfMonth
        )
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()
    }

    private fun pickTime(initial: LocalTime, onPicked: (LocalTime) -> Unit) {
        TimePickerDialog(
            requireContext(),
            { _, hour, minute -> onPicked(LocalTime.of(hour, minute)) },
            initial.hour, initial.minute, true
        ).show()
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.tvError.visibility = View.GONE
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
