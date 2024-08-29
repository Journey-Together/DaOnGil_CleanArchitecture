package kr.tekit.lion.presentation.main.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.tekit.lion.presentation.concerntype.ConcernTypeActivity
import kr.tekit.lion.presentation.DeleteUserActivity
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.bookmark.BookmarkActivity
import kr.tekit.lion.presentation.databinding.FragmentMyInfoMainBinding
import kr.tekit.lion.presentation.delegate.NetworkState
import kr.tekit.lion.presentation.ext.announceForAccessibility
import kr.tekit.lion.presentation.ext.isTallBackEnabled
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.ext.setAccessibilityText
import kr.tekit.lion.presentation.login.LoginActivity
import kr.tekit.lion.presentation.main.dialog.ConfirmDialog
import kr.tekit.lion.presentation.main.vm.myinfo.MyInfoMainViewModel
import kr.tekit.lion.presentation.myinfo.MyInfoActivity
import kr.tekit.lion.presentation.myreview.MyReviewActivity
import kr.tekit.lion.presentation.splash.model.LogInState

@AndroidEntryPoint
class MyInfoMainFragment : Fragment(R.layout.fragment_my_info_main) {
    private val viewModel: MyInfoMainViewModel by viewModels()

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == MODIFY_RESULT_CODE) {
            viewModel.onStateLoggedIn()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMyInfoMainBinding.bind(view)
        startShimmer(binding)

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
        }else{
            binding.readScriptBtn.visibility = View.GONE
            binding.readScriptBtn.setOnClickListener(null)
        }

        repeatOnViewStarted {
            supervisorScope {
                launch { handleLoginState(binding, isTalkbackEnabled, textToAnnounce) }
                launch { handleNetworkState(binding) }
                launch { handleErrorMessage(binding, isTalkbackEnabled) }
            }
        }
    }

    private suspend fun handleLoginState(
        binding: FragmentMyInfoMainBinding,
        isTalkbackEnabled: Boolean,
        talkbackText: StringBuilder
    ) {
        viewModel.loginState.collect { uiState ->
            when (uiState) {
                is LogInState.Checking -> return@collect
                is LogInState.LoggedIn -> {
                    setUiLoggedInState(binding)
                    collectMyInfo(binding, isTalkbackEnabled, talkbackText)
                }
                is LogInState.LoginRequired -> {
                    setUiLoginRequiredState(binding)
                    if (isTalkbackEnabled) {
                        talkbackText.append(getString(R.string.text_script_for_no_login_user))
                        requireContext().announceForAccessibility(talkbackText.toString())
                    }
                }
            }
        }
    }

    private suspend fun collectMyInfo(
        binding: FragmentMyInfoMainBinding,
        isTalkbackEnabled: Boolean,
        talkbackText: StringBuilder
    ) {
        viewModel.myInfo
            .filter { it.name.isNotEmpty() }
            .collect { myInfo ->
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

    private suspend fun handleNetworkState(binding: FragmentMyInfoMainBinding){
        viewModel.networkState.collect {
            if (it == NetworkState.Success){
                stopShimmer(binding)
            }
        }
    }

    private suspend fun handleErrorMessage(binding: FragmentMyInfoMainBinding, isTalkbackEnabled: Boolean){
        viewModel.errorMessage.collect {
            if (it != null) {
                showErrorPage(binding, it, isTalkbackEnabled)
            }
        }
    }

    private fun startShimmer(binding: FragmentMyInfoMainBinding) {
        with(binding) {
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.VISIBLE
        }
    }

    private fun stopShimmer(binding: FragmentMyInfoMainBinding) {
        with(binding){
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
            mainContainer.visibility = View.VISIBLE
        }
    }

    private fun showErrorPage(binding: FragmentMyInfoMainBinding, msg: String, isTalkbackEnabled: Boolean) {
        with(binding) {
            errorContainer.visibility = View.VISIBLE
            mainContainer.visibility = View.GONE
            shimmerFrameLayout.visibility = View.GONE
            shimmerFrameLayout.stopShimmer()
            textMsg.text = msg

            if (isTalkbackEnabled){
                requireActivity().announceForAccessibility(msg)
            }
        }
    }

    private fun setUiLoggedInState(binding: FragmentMyInfoMainBinding) {
        binding.mainContainer.visibility = View.VISIBLE
        navigateToMyInfo(binding)
        logoutDialog(binding)
        navigateToConcernType(binding)
        navigateBookmark(binding)
        navigateMyReview(binding)
        navigateDeleteUser(binding)
    }

    private fun setUiLoginRequiredState(binding: FragmentMyInfoMainBinding) {
        binding.mainContainer.visibility = View.VISIBLE
        with(binding) {
            userContainer.visibility = View.GONE
            tvReview.text = getString(R.string.text_NameOrLogin)
            tvReviewCnt.visibility = View.GONE
            textViewMyInfoMainRegister.visibility = View.GONE
            tvNameOrLogin.text = getString(R.string.text_myInfo_Review)
            tvNameOrLogin.contentDescription = "로그인 버튼"
            tvRegisteredData.visibility = View.GONE
            layoutProfile.setOnClickListener {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
            }
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
            intent.putExtra("nickName", viewModel.myInfo.value.name)
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
            dialog.show(requireActivity().supportFragmentManager, "MyPageDialog")
        }
    }

    private fun logout() {
        Snackbar.make(requireView(), "로그아웃", Snackbar.LENGTH_SHORT).show()
    }

    companion object{
        const val MODIFY_RESULT_CODE = 10001
    }
}