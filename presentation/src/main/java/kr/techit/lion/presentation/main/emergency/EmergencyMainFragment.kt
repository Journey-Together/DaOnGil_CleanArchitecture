package kr.techit.lion.presentation.main.emergency

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentEmergencyMainBinding
import kr.techit.lion.presentation.emergency.EmergencyMapActivity
import kr.techit.lion.presentation.emergency.PharmacyMapActivity
import kr.techit.lion.presentation.ext.repeatOnViewStarted
import kr.techit.lion.presentation.connectivity.ConnectivityObserver
import kr.techit.lion.presentation.connectivity.NetworkConnectivityObserver
import kr.techit.lion.presentation.ext.announceForAccessibility
import kr.techit.lion.presentation.ext.isTallBackEnabled

class EmergencyMainFragment : Fragment(R.layout.fragment_emergency_main) {

    private val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver(requireContext().applicationContext)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentEmergencyMainBinding.bind(view)


        binding.emrAedCard.setOnClickListener {
            val intent = Intent(requireActivity(), EmergencyMapActivity::class.java)
            startActivity(intent)
        }

        binding.pharmacyCard.setOnClickListener {
            val intent = Intent(requireActivity(), PharmacyMapActivity::class.java)
            startActivity(intent)
        }

        initializeAccessibility(binding)

        repeatOnViewStarted {
            observeConnectivity(binding)
        }
    }

    private suspend fun observeConnectivity(binding: FragmentEmergencyMainBinding) {
        with(binding) {
            connectivityObserver.getFlow().collect { connectivity ->
                when (connectivity) {
                    ConnectivityObserver.Status.Available -> {
                        emergencyMainErrorLayout.visibility = View.GONE
                        emergencyMainProgressBar.visibility = View.GONE
                        emergencyMainLayout.visibility = View.VISIBLE
                    }

                    ConnectivityObserver.Status.Unavailable,
                    ConnectivityObserver.Status.Losing,
                    ConnectivityObserver.Status.Lost -> {
                        emergencyMainErrorLayout.visibility = View.VISIBLE
                        emergencyMainProgressBar.visibility = View.GONE
                        emergencyMainLayout.visibility = View.GONE
                        val msg = "${getString(R.string.text_network_is_unavailable)}\n" +
                                "${getString(R.string.text_plz_check_network)} "
                        emergencyMainErrorMsg.text = msg
                    }
                }
            }
        }
    }

    private fun initializeAccessibility(binding: FragmentEmergencyMainBinding) {
        if (requireContext().isTallBackEnabled()) {
            setupAccessibility(binding)
        } else {
            binding.toolbarEmerMain.menu.clear()
        }
    }

    private fun setupAccessibility(binding: FragmentEmergencyMainBinding) {
        requireActivity().announceForAccessibility(
            getString(R.string.text_script_this_is_emergency) +
                    getString(R.string.text_script_read_all_text)
        )

        binding.toolbarEmerMain.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.read_script -> {
                    requireActivity().announceForAccessibility(getString(R.string.text_script_guide_for_emergency))
                    true
                }

                else -> false
            }
        }
    }
}