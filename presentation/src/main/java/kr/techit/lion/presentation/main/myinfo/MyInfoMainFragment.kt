package kr.techit.lion.presentation.main.myinfo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.bookmark.BookmarkActivity
import kr.techit.lion.presentation.concerntype.ConcernTypeActivity
import kr.techit.lion.presentation.connectivity.connectivity.ConnectivityStatus
import kr.techit.lion.presentation.databinding.FragmentMyInfoMainBinding
import kr.techit.lion.presentation.delegate.NetworkEvent
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.announceForAccessibility
import kr.techit.lion.presentation.ext.isTallBackEnabled
import kr.techit.lion.presentation.ext.repeatOnViewStarted
import kr.techit.lion.presentation.ext.setAccessibilityText
import kr.techit.lion.presentation.login.LoginActivity
import kr.techit.lion.presentation.main.dialog.ConfirmDialog
import kr.techit.lion.presentation.main.myinfo.vm.MyInfoMainViewModel
import kr.techit.lion.presentation.main.myinfo.vm.model.MyInfoUiModel
import kr.techit.lion.presentation.myinfo.DeleteUserActivity
import kr.techit.lion.presentation.myinfo.MyInfoActivity
import kr.techit.lion.presentation.myreview.MyReviewActivity
import kr.techit.lion.presentation.setting.PolicyActivity
import kr.techit.lion.presentation.splash.model.LogInStatus

@AndroidEntryPoint
class MyInfoMainFragment : Fragment(R.layout.fragment_my_info_main) {
    private val viewModel: MyInfoMainViewModel by viewModels()

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == MODIFY_RESULT_CODE) {
            repeatOnViewStarted {
                viewModel.onStateLoggedIn()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMyInfoMainBinding.bind(view)
        viewModel.checkLoginState()
        val isTalkbackEnabled = requireContext().isTallBackEnabled()
        val textToAnnounce = StringBuilder()

        if (isTalkbackEnabled) {
            binding.readScriptBtn.visibility = View.VISIBLE
            viewLifecycleOwner.lifecycleScope.launch {
                delay(2500)
                binding.readScriptBtn.setOnClickListener {
                    requireContext().announceForAccessibility(
                        resources.getString(R.string.text_script_for_my_info_main)
                    )
                }
            }
        } else {
            binding.readScriptBtn.visibility = View.GONE
            binding.readScriptBtn.setOnClickListener(null)
        }

        repeatOnViewStarted {
            launch { handleConnectivityAndLoginState(binding, isTalkbackEnabled, textToAnnounce) }
            launch { handleNetworkState(binding) }
        }
    }

    private suspend fun handleConnectivityAndLoginState(
        binding: FragmentMyInfoMainBinding,
        isTalkbackEnabled: Boolean,
        talkbackText: StringBuilder
    ) {
        combine(
            viewModel.connectivityStatus,
            viewModel.loggedIn
        ) { connectivityStatus, loginStatus ->
            connectivityStatus to loginStatus
        }.collect { (connectivityStatus, loginStatus) ->
            when (connectivityStatus) {
                ConnectivityStatus.Loading -> Unit
                ConnectivityStatus.Available -> {
                    binding.errorContainer.visibility = View.GONE
                    binding.mainContainer.visibility = View.VISIBLE
                    handleLoginState(binding, isTalkbackEnabled, talkbackText, loginStatus)
                }
                is ConnectivityStatus.OnLost -> {
                    showErrorPage(binding, getString(R.string.can_not_access_network))
                }
            }
        }
    }

    private suspend fun handleLoginState(
        binding: FragmentMyInfoMainBinding,
        isTalkbackEnabled: Boolean,
        talkbackText: StringBuilder,
        loginStatus: LogInStatus
    ) {
        when (loginStatus) {
            is LogInStatus.Checking -> Unit
            is LogInStatus.LoggedIn -> {
                viewModel.onStateLoggedIn()
                collectMyInfo(binding, isTalkbackEnabled, talkbackText)
            }

            is LogInStatus.LoginRequired -> {
                setUiLoginRequiredState(binding, isTalkbackEnabled, talkbackText)
            }
        }
    }

    private suspend fun collectMyInfo(
        binding: FragmentMyInfoMainBinding,
        isTalkbackEnabled: Boolean,
        talkbackText: StringBuilder
    ) {
        viewModel.uiState.collect {
            setUiLoggedInState(binding)
            setUpMyInfo(binding, isTalkbackEnabled, talkbackText, it.myInfo)
        }
    }

    private fun setUpMyInfo(
        binding: FragmentMyInfoMainBinding,
        isTalkbackEnabled: Boolean,
        talkbackText: StringBuilder,
        myInfo: MyInfoUiModel
    ) {
        with(binding) {
            val name = myInfo.name
            val review = myInfo.reviewNum
            val registeredData = myInfo.date

            tvNameOrLogin.text = name
            tvReviewCnt.text = myInfo.reviewNum
            tvRegisteredData.visibility = View.VISIBLE
            tvRegisteredData.text = registeredData

            Glide.with(imgProfile.context)
                .load(myInfo.profileImg)
                .fallback(R.drawable.default_profile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imgProfile)

            if (isTalkbackEnabled) {
                talkbackText
                    .append(name)
                    .append(review)
                    .append("${textViewMyInfoMainRegister.text} $registeredData")
                    .append(getString(R.string.text_script_read_all_text))

                requireContext().announceForAccessibility(talkbackText.toString())

                tvNameOrLogin.setAccessibilityText(name)
                tvReview.setAccessibilityText(review)
                textViewMyInfoMainRegister.setAccessibilityText(
                    "${textViewMyInfoMainRegister.text} $registeredData"
                )
            }
        }
    }

    private suspend fun handleNetworkState(binding: FragmentMyInfoMainBinding) {
        with(binding) {
            viewModel.networkEvent.collect { event ->
                when (event) {
                    NetworkEvent.Loading -> progressBar.visibility = View.VISIBLE
                    NetworkEvent.Success -> {
                        mainContainer.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        errorContainer.visibility = View.GONE
                    }
                    is NetworkEvent.Error -> {
                        mainContainer.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        showErrorPage(binding, event.msg)
                    }
                }
            }
        }
    }

    private fun showErrorPage(binding: FragmentMyInfoMainBinding, msg: String) {
        with(binding) {
            errorContainer.visibility = View.VISIBLE
            mainContainer.visibility = View.GONE
            progressBar.visibility = View.GONE
            textMsg.text = msg
            if (requireContext().isTallBackEnabled()) requireActivity().announceForAccessibility(msg)
        }
    }

    private fun setUiLoggedInState(binding: FragmentMyInfoMainBinding) {
        navigateToMyInfo(binding)
        logoutDialog(binding)
        navigateToConcernType(binding)
        navigateBookmark(binding)
        navigateMyReview(binding)
        navigateDeleteUser(binding)
        navigateToPolicy(binding)
    }

    private fun setUiLoginRequiredState(
        binding: FragmentMyInfoMainBinding,
        isTalkbackEnabled: Boolean,
        talkbackText: StringBuilder
    ) {
        with(binding) {
            progressBar.visibility = View.GONE
            userContainer.visibility = View.GONE
            tvReviewCnt.visibility = View.GONE
            textViewMyInfoMainRegister.visibility = View.GONE
            tvRegisteredData.visibility = View.GONE
            readScriptBtn.visibility = View.GONE
            tvReview.text = getString(R.string.text_NameOrLogin)
            tvNameOrLogin.text = getString(R.string.text_myInfo_Review)
            tvNameOrLogin.contentDescription = requireContext().getString(R.string.text_login_button)
            layoutProfile.setOnClickListener {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
            }
        }

        if (isTalkbackEnabled) {
            talkbackText.append(getString(R.string.text_script_for_no_login_user))
            requireContext().announceForAccessibility(talkbackText.toString())
        }
    }

    private fun navigateToMyInfo(binding: FragmentMyInfoMainBinding) {
        binding.btnLoginOrUpdate.setOnClickListener {
            val intent = Intent(requireActivity(), MyInfoActivity::class.java)
            activityResultLauncher.launch(intent)
        }
    }

    private fun navigateToConcernType(binding: FragmentMyInfoMainBinding) {
        binding.layoutConcernType.setOnClickListener {
            val nickname = binding.tvNameOrLogin.text.toString()
            val intent = ConcernTypeActivity.newIntent(requireContext(), nickname)
            startActivity(intent)
        }
    }

    private fun navigateBookmark(binding: FragmentMyInfoMainBinding) {
        binding.layoutBookmark.setOnClickListener {
            startActivity(Intent(requireActivity(), BookmarkActivity::class.java))
        }
    }

    private fun navigateMyReview(binding: FragmentMyInfoMainBinding) {
        binding.layoutMyReview.setOnClickListener {
            val intent = Intent(requireActivity(), MyReviewActivity::class.java)
            activityResultLauncher.launch(intent)
        }
    }

    private fun navigateDeleteUser(binding: FragmentMyInfoMainBinding) {
        binding.layoutDelete.setOnClickListener {
            val intent = Intent(requireActivity(), DeleteUserActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToPolicy(binding: FragmentMyInfoMainBinding) {
        binding.layoutPolicy.setOnClickListener {
            val intent = Intent(requireActivity(), PolicyActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logoutDialog(binding: FragmentMyInfoMainBinding) {
        binding.layoutLogout.setOnClickListener {
            val dialog = ConfirmDialog(
                requireContext().getString(R.string.text_logout),
                requireContext().getString(R.string.text_logout_to_device),
                requireContext().getString(R.string.text_logout)
            ) {
                logout()
            }
            dialog.isCancelable = false
            dialog.show(childFragmentManager, "MyPageDialog")
        }
    }

    private fun logout() {
        viewModel.logout {
            val intent = Intent(requireActivity(), LoginActivity::class.java).apply {
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }

    companion object {
        const val MODIFY_RESULT_CODE = 10001
    }
}