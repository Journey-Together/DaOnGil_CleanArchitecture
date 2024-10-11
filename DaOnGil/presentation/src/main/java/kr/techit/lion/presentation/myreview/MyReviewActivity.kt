package kr.techit.lion.presentation.myreview

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.home.model.ReviewInfo
import kr.techit.lion.presentation.myreview.vm.MyReviewViewModel

@AndroidEntryPoint
class MyReviewActivity : AppCompatActivity() {

    private val viewModel: MyReviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_review)

        val isFromDetail = intent.getBooleanExtra("isModifyFromDetail", false)
        viewModel.setIsFromDetail(isFromDetail)

        if (isFromDetail) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val reviewInfo = intent.getParcelableExtra("reviewInfo", ReviewInfo::class.java)
                if (reviewInfo != null) {
                    viewModel.setDetailReviewData(reviewInfo)
                }
            } else {
                @Suppress("DEPRECATION")
                val reviewInfo = intent.getParcelableExtra<ReviewInfo>("reviewInfo")
                if (reviewInfo != null) {
                    viewModel.setDetailReviewData(reviewInfo)
                }
            }

            val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerViewMyReview) as NavHostFragment
            val navController = navHostFragment.navController

            navController.navigate(R.id.myReviewModifyFragment)
        }
    }
}