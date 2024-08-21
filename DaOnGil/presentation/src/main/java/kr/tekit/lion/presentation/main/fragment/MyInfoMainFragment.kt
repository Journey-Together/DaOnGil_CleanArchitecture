package kr.tekit.lion.presentation.main.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.tekit.lion.presentation.ConcernTypeActivity
import kr.tekit.lion.presentation.DeleteUserActivity
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.bookmark.BookmarkActivity
import kr.tekit.lion.presentation.databinding.FragmentMyInfoMainBinding
import kr.tekit.lion.presentation.delegate.NetworkState
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
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
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onStateLoggedIn()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMyInfoMainBinding.bind(view)
        startShimmer(binding)

        repeatOnViewStarted {
            supervisorScope {
                launch {
                    viewModel.loginState.collect { uiState ->
                        when (uiState) {
                            is LogInState.Checking -> {
                                return@collect
                            }
                            is LogInState.LoggedIn -> {
                                setUiLoggedInState(binding)

                                viewModel.myInfo.collect { myInfo ->
                                    binding.tvNameOrLogin.text = myInfo.name
                                    binding.tvReviewCnt.text = myInfo.reviewNum.toString()
                                    binding.tvRegisteredData.visibility = View.VISIBLE
                                    binding.tvRegisteredData.text = "${myInfo.date + 1}일째"

                                    Glide.with(binding.imgProfile.context)
                                        .load(myInfo.profileImg)
                                        .fallback(R.drawable.default_profile)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(binding.imgProfile)
                                }
                            }
                            is LogInState.LoginRequired -> {
                                setUiLoginRequiredState(binding)
                            }
                        }
                    }
                }

                launch {
                    viewModel.networkState.collect {
                        if (it == NetworkState.Success) stopShimmer(binding)
                    }
                }

                launch {
                    viewModel.errorMessage.collect {
                        if (it != null) {
                            showErrorPage(binding, it)
                        }
                    }
                }
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

    private fun showErrorPage(binding: FragmentMyInfoMainBinding, msg: String) {
        with(binding) {
            errorContainer.visibility = View.VISIBLE
            mainContainer.visibility = View.GONE
            shimmerFrameLayout.visibility = View.GONE
            shimmerFrameLayout.stopShimmer()
            textMsg.text = msg
        }
    }

    private fun setUiLoggedInState(binding: FragmentMyInfoMainBinding) {
        binding.mainContainer.visibility = View.VISIBLE
        moveMyInfo(binding)
        logoutDialog(binding)
        moveConcernType(binding)
        moveBookmark(binding)
        moveMyReview(binding)
        moveDeleteUser(binding)
      }

    private fun setUiLoginRequiredState(binding: FragmentMyInfoMainBinding) {
        binding.mainContainer.visibility = View.VISIBLE
        with(binding) {
            userContainer.visibility = View.GONE
            tvReview.text = getString(R.string.text_NameOrLogin)
            tvReviewCnt.visibility = View.GONE
            tvUserNameTitle.visibility = View.GONE
            textViewMyInfoMainRegister.visibility = View.GONE
            tvNameOrLogin.text = getString(R.string.text_myInfo_Review)
            tvRegisteredData.visibility = View.GONE
            layoutProfile.setOnClickListener {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun moveMyInfo(binding: FragmentMyInfoMainBinding) {
        binding.btnLoginOrUpdate.setOnClickListener {
            val intent = Intent(requireActivity(), MyInfoActivity::class.java)
            activityResultLauncher.launch(intent)
        }
    }

    private fun moveConcernType(binding: FragmentMyInfoMainBinding) {
        binding.layoutConcernType.setOnClickListener {
            startActivity(Intent(requireActivity(), ConcernTypeActivity::class.java))
        }
    }

    private fun moveBookmark(binding: FragmentMyInfoMainBinding) {
        binding.layoutBookmark.setOnClickListener {
            startActivity(Intent(requireActivity(), BookmarkActivity::class.java))
        }
    }

    private fun moveMyReview(binding: FragmentMyInfoMainBinding) {
        binding.layoutMyReview.setOnClickListener {
            val intent = Intent(requireActivity(), MyReviewActivity::class.java)
            activityResultLauncher.launch(intent)
        }
    }

    private fun moveDeleteUser(binding: FragmentMyInfoMainBinding) {
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
}