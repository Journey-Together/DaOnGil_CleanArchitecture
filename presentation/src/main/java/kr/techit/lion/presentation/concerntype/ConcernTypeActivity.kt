package kr.techit.lion.presentation.concerntype

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.concerntype.vm.ConcernTypeViewModel

@AndroidEntryPoint
class ConcernTypeActivity : AppCompatActivity() {
    private val viewModel: ConcernTypeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_concern_type)
        val nickName = intent.getStringExtra(EXTRA_KEY_NICKNAME)
        nickName?.let { viewModel.setNickName(nickName) }
    }

    companion object {
        private const val EXTRA_KEY_NICKNAME = "nickName"

        fun newIntent(context: Context, nickName: String): Intent =
            Intent(context, ConcernTypeActivity::class.java).apply {
                putExtra(EXTRA_KEY_NICKNAME, nickName)
            }
    }
}
