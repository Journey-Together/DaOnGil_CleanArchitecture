package kr.techit.lion.presentation.main.fragment

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.techit.lion.presentation.concerntype.ConcernTypeActivity
import kr.techit.lion.presentation.myinfo.DeleteUserActivity
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.bookmark.BookmarkActivity
import kr.techit.lion.presentation.databinding.FragmentMyInfoMainBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.announceForAccessibility
import kr.techit.lion.presentation.ext.isNetworkConnected
import kr.techit.lion.presentation.ext.isTallBackEnabled
import kr.techit.lion.presentation.ext.repeatOnViewStarted
import kr.techit.lion.presentation.ext.setAccessibilityText
import kr.techit.lion.presentation.login.LoginActivity
import kr.techit.lion.presentation.main.dialog.ConfirmDialog
import kr.techit.lion.presentation.main.vm.myinfo.MyInfoMainViewModel
import kr.techit.lion.presentation.myinfo.MyInfoActivity
import kr.techit.lion.presentation.myreview.MyReviewActivity
import kr.techit.lion.presentation.setting.PolicyActivity
import kr.techit.lion.presentation.splash.model.LogInState

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
            supervisorScope {
                launch { handleLoginState(binding, isTalkbackEnabled, textToAnnounce) }
                launch { handleNetworkState(binding) }
                launch { collectMyInfo(binding, isTalkbackEnabled, textToAnnounce) }
            }
        }
    }

    private suspend fun handleLoginState(
        binding: FragmentMyInfoMainBinding,
        isTalkbackEnabled: Boolean,
        talkbackText: StringBuilder
    ) {
        viewModel.loginState.collect { loginState ->
            when (loginState) {
                is LogInState.Checking -> return@collect
                is LogInState.LoggedIn -> {
                    viewModel.onStateLoggedIn()
                    setUiLoggedInState(binding)
                }
                is LogInState.LoginRequired -> {
                    setUiLoginRequiredState(binding, isTalkbackEnabled, talkbackText)
                }
            }
        }
    }

    private suspend fun collectMyInfo(
        binding: FragmentMyInfoMainBinding,
        isTalkbackEnabled: Boolean,
        talkbackText: StringBuilder
    ) {
        viewModel.myInfo.collect { myInfo ->
            with(binding) {
                val name = "${myInfo.name}님"
                val review = "${tvReview.text} ${myInfo.reviewNum}개"
                val registeredData = "${myInfo.date + 1}일째"

                tvNameOrLogin.text = name
                tvReviewCnt.text = myInfo.reviewNum.toString()
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
    }

    private suspend fun handleNetworkState(binding: FragmentMyInfoMainBinding) {
        with(binding) {
            viewModel.networkState.collect {
                when (it) {
                    NetworkState.Loading -> progressBar.visibility = View.VISIBLE
                    NetworkState.Success -> {
                        mainContainer.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        errorContainer.visibility = View.GONE
                    }
                    is NetworkState.Error -> {
                        mainContainer.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        showErrorPage(binding, it.msg)
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

            if (requireContext().isTallBackEnabled()) {
                requireActivity().announceForAccessibility(msg)
            }
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
            userContainer.visibility = View.GONE
            tvReviewCnt.visibility = View.GONE
            textViewMyInfoMainRegister.visibility = View.GONE
            tvRegisteredData.visibility = View.GONE
            readScriptBtn.visibility = View.GONE
            tvReview.text = getString(R.string.text_NameOrLogin)
            tvNameOrLogin.text = getString(R.string.text_myInfo_Review)
            tvNameOrLogin.contentDescription = "로그인 버튼"
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
            val intent = Intent(requireActivity(), ConcernTypeActivity::class.java)
            intent.putExtra("nickName", binding.tvNameOrLogin.text.toString())
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

    private fun navigateToPolicy(binding: FragmentMyInfoMainBinding){
        binding.layoutPolicy.setOnClickListener {
            val intent = Intent(requireActivity(), PolicyActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logoutDialog(binding: FragmentMyInfoMainBinding) {
        binding.layoutLogout.setOnClickListener {
            val dialog = ConfirmDialog(
                "로그아웃",
                "해당 기기에서 로그아웃 됩니다.",
                "로그아웃"
            ) {
                logout()
            }
            dialog.isCancelable = false
            dialog.show(childFragmentManager, "MyPageDialog")
        }
    }

    private fun logout() {
        viewModel.logout {
            startActivity(Intent(requireActivity(), LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (requireContext().isNetworkConnected()
            && viewModel.networkState.value is NetworkState.Error
            && viewModel.loginState.value is LogInState.LoggedIn
        ) {
            repeatOnViewStarted {
                viewModel.onStateLoggedIn()
            }
        }
    }

    companion object {
        const val MODIFY_RESULT_CODE = 10001
    }
}