package kr.tekit.lion.presentation.main.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.scheduleform.ScheduleFormActivity
import kr.tekit.lion.presentation.databinding.FragmentScheduleMainBinding
import kr.tekit.lion.presentation.ext.repeatOnStarted
import kr.tekit.lion.presentation.ext.showSnackbar
import kr.tekit.lion.presentation.login.LoginActivity
import kr.tekit.lion.presentation.main.dialog.ConfirmDialog
import kr.tekit.lion.presentation.main.vm.schedule.ScheduleMainViewModel
import kr.tekit.lion.presentation.myschedule.MyScheduleActivity
import kr.tekit.lion.presentation.splash.model.LogInState

@AndroidEntryPoint
class ScheduleMainFragment : Fragment(R.layout.fragment_schedule_main) {
    private var isUser = true

    private val viewModel: ScheduleMainViewModel by viewModels()

    private val scheduleFormLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
//            viewModel.getMyMainPlanList()
//            viewModel.getOpenPlanList()
            view?.showSnackbar("일정이 저장되었습니다", Snackbar.LENGTH_LONG)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentScheduleMainBinding.bind(view)

        initButtonClickListener(binding)

        repeatOnStarted {
            viewModel.loginState.collect { uiState ->
                when (uiState) {
                    is LogInState.Checking -> {
                        return@collect
                    }

                    is LogInState.LoggedIn -> {
                        isUser = true
                    }

                    is LogInState.LoginRequired -> {
                        isUser = false
                        binding.textViewMyScheduleMore.visibility = View.INVISIBLE
                        showAddSchedulePrompt(binding)
                    }
                }
            }
        }
    }

    private fun showAddSchedulePrompt(binding: FragmentScheduleMainBinding) {
        // 등록된 스케쥴이 없는 경우 -> '내 일정' 리사이클러뷰를 숨겨주고, 일정 추가 권유하는 cardView를 보여준다.
        binding.recyclerViewMySchedule.visibility = View.GONE
        binding.cardViewEmptySchedule.visibility = View.VISIBLE
    }

    private fun initButtonClickListener(binding: FragmentScheduleMainBinding) {
        with(binding) {
            buttonAddSchedule.setOnClickListener {
                createNewSchedule()
            }
            textViewAddSchedule.setOnClickListener {
                createNewSchedule()
            }
            textViewMyScheduleMore.setOnClickListener {
                val intent = Intent(requireActivity(), MyScheduleActivity::class.java)
                startActivity(intent)
            }
            textViewPublicScheduleMore.setOnClickListener {
                // 공개 일정 더보기
            }
        }
    }

    private fun createNewSchedule() {
        if (isUser) {
            val intent = Intent(requireActivity(), ScheduleFormActivity::class.java)
            scheduleFormLauncher.launch(intent)
        } else {
            // 비회원 -> 로그인 다이얼로그
            showLoginDialog()
        }
    }

    private fun showLoginDialog() {
        val dialog = ConfirmDialog(
            "로그인이 필요해요!",
            "여행 일정을 추가/관리하고 싶다면\n로그인을 진행해주세요",
            "로그인하기",
        ) {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        dialog.isCancelable = true
        dialog.show(activity?.supportFragmentManager!!, "ScheduleLoginDialog")
    }
}