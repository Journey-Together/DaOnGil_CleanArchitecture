package kr.tekit.lion.presentation.concerntype

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R

@AndroidEntryPoint
class ConcernTypeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_concern_type)
    }
}