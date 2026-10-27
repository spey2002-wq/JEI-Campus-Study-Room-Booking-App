package com.jei.campusbooking.ui.mybookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.jei.campusbooking.R
import com.jei.campusbooking.data.entity.Reservation
import com.jei.campusbooking.databinding.FragmentMyBookingsBinding
import kotlinx.coroutines.launch

/** Screen 4 - booking history, with Cancel and Check-out actions. */
class MyBookingsFragment : Fragment() {

    private var _binding: FragmentMyBookingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyBookingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BookingAdapter(
            onCancel = { item ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.cancel_title)
                    .setMessage(R.string.cancel_message)
                    .setPositiveButton(R.string.btn_yes) { _, _ -> viewModel.cancel(item.reservationId) }
                    .setNegativeButton(R.string.btn_no, null)
                    .show()
            },
            onCheckOut = { item -> viewModel.checkOut(item.reservationId) }
        )
        binding.rvBookings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBookings.adapter = adapter

        binding.chipGroupStatus.setOnCheckedStateChangeListener { _, checkedIds ->
            val status = when (checkedIds.firstOrNull()) {
                R.id.chip_status_confirmed -> Reservation.Status.CONFIRMED
                R.id.chip_status_completed -> Reservation.Status.COMPLETED
                R.id.chip_status_cancelled -> Reservation.Status.CANCELLED
                else -> null
            }
            viewModel.setStatusFilter(status)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.bookings.collect { list ->
                        adapter.submitList(list.orEmpty())
                        binding.tvEmpty.visibility =
                            if (list != null && list.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.messages.collect { resId ->
                        Snackbar.make(binding.root, resId, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvBookings.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
