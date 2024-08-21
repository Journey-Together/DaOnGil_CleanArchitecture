package kr.tekit.lion.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kakao.sdk.user.UserApiClient
import kr.tekit.lion.presentation.databinding.ActivityDeleteUserBinding
import kr.tekit.lion.presentation.login.LoginActivity
import kr.tekit.lion.presentation.main.dialog.ConfirmDialog

class DeleteUserActivity : AppCompatActivity() {
    private val binding: ActivityDeleteUserBinding by lazy {
        ActivityDeleteUserBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            toolbar.setNavigationOnClickListener {
                finish()
            }

            btnDelete.setOnClickListener {
                val permissionDialog = ConfirmDialog(
                    "회원 탈퇴",
                    "정말 회원을 탈퇴 하시겠습니까?",
                    "탈퇴 하기"
                ) {
                    UserApiClient.instance.unlink {

                    }
                    startActivity(Intent(this@DeleteUserActivity, LoginActivity::class.java))
                    finish()
                }
                permissionDialog.isCancelable = false
                permissionDialog.show(supportFragmentManager, "PermissionDialog")
            }
        }
    }
}