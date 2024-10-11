package kr.techit.lion.presentation.myinfo

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kr.techit.lion.presentation.databinding.ActivityDeleteUserBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.repeatOnStarted
import kr.techit.lion.presentation.ext.showInfinitySnackBar
import kr.techit.lion.presentation.login.LoginActivity
import kr.techit.lion.presentation.main.dialog.ConfirmDialog
import kr.techit.lion.presentation.myinfo.vm.DeleteUserViewModel

@AndroidEntryPoint
class DeleteUserActivity : AppCompatActivity() {
    private val binding: ActivityDeleteUserBinding by lazy {
        ActivityDeleteUserBinding.inflate(layoutInflater)
    }
    private val viewModel: DeleteUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            toolbar.setNavigationOnClickListener {
                finish()
            }

            btnDelete.setOnClickListener {
                val dialog = ConfirmDialog(
                    "회원 탈퇴",
                    "정말 회원을 탈퇴 하시겠습니까?",
                    "탈퇴 하기"
                ) {
                    viewModel.withdrawal {
                        startActivity(Intent(this@DeleteUserActivity, LoginActivity::class.java))
                        finish()
                    }
                }
                dialog.isCancelable = false
                dialog.show(supportFragmentManager, "dialog")
            }
        }
        repeatOnStarted {
            viewModel.networkState.collect { state ->
                when (state) {
                    is NetworkState.Loading -> return@collect
                    is NetworkState.Success -> {
                        startActivity(Intent(this@DeleteUserActivity, LoginActivity::class.java))
                        finish()
                    }
                    is NetworkState.Error -> {
                        showInfinitySnackBar(binding.root, state.msg)
                    }
                }
            }
        }
    }
}