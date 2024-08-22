package kr.tekit.lion.presentation.bookmark

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ActivityBookmarkBinding

class BookmarkActivity : AppCompatActivity() {
    private val binding: ActivityBookmarkBinding by lazy {
        ActivityBookmarkBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}