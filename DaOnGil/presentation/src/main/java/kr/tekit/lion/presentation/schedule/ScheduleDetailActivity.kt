package kr.tekit.lion.presentation.schedule

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.domain.model.ScheduleDetail
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ActivityScheduleDetailBinding
import kr.tekit.lion.presentation.ext.repeatOnStarted
import kr.tekit.lion.presentation.ext.setImageSmall
import kr.tekit.lion.presentation.ext.showSnackbar
import kr.tekit.lion.presentation.home.DetailActivity
import kr.tekit.lion.presentation.login.LoginActivity
import kr.tekit.lion.presentation.main.dialog.ConfirmDialog
import kr.tekit.lion.presentation.report.ReportActivity
import kr.tekit.lion.presentation.schedule.ResultCode.RESULT_REVIEW_EDIT
import kr.tekit.lion.presentation.schedule.ResultCode.RESULT_REVIEW_WRITE
import kr.tekit.lion.presentation.schedule.ResultCode.RESULT_SCHEDULE_EDIT
import kr.tekit.lion.presentation.schedule.adapter.ScheduleImageViewPagerAdapter
import kr.tekit.lion.presentation.schedule.adapter.ScheduleListAdapter
import kr.tekit.lion.presentation.schedule.customview.ScheduleManageBottomSheet
import kr.tekit.lion.presentation.schedule.customview.ScheduleReviewManageBottomSheet
import kr.tekit.lion.presentation.schedule.vm.ScheduleDetailViewModel
import kr.tekit.lion.presentation.schedulereview.ModifyScheduleReviewActivity
import kr.tekit.lion.presentation.schedulereview.WriteScheduleReviewActivity
import kr.tekit.lion.presentation.splash.model.LogInState
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

@AndroidEntryPoint
class ScheduleDetailActivity : AppCompatActivity() {

    private val viewModel: ScheduleDetailViewModel by viewModels()

    private val binding: ActivityScheduleDetailBinding by lazy{
        ActivityScheduleDetailBinding.inflate(layoutInflater)
    }

    private val scheduleReviewLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val planId = intent.getLongExtra("planId", -1)
        viewModel.getScheduleDetailInfo(planId)
        when(result.resultCode){
            RESULT_REVIEW_WRITE -> {
                binding.root.showSnackbar("후기가 저장되었습니다")
            }
            RESULT_SCHEDULE_EDIT -> {
                binding.root.showSnackbar("일정이 수정되었습니다")
            }
            RESULT_REVIEW_EDIT -> {
                binding.root.showSnackbar("리뷰가 수정되었습니다")
            }
        }
    }

    private val reportLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when(result.resultCode){
            RESULT_OK -> {
                binding.root.showSnackbar("신고가 완료되었습니다")
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        checkLogin()


    }

    private fun initView(isUser: Boolean, planId: Long) {
        with(binding) {

            viewModel.scheduleDetail.observe(this@ScheduleDetailActivity){ scheduleDetail ->

                initToolbarMenu(isUser, scheduleDetail.isWriter, scheduleDetail.isPublic, scheduleDetail.isBookmark, planId)

                settingScheduleAdapter(scheduleDetail)
                schedulePublic.visibility = View.VISIBLE

                textViewReview.visibility = View.VISIBLE

                scheduleDetail.remainDate?.let {
                    scheduleDday.text = it
                    scheduleDday.visibility = View.VISIBLE

                    cardViewScheduleEmptyReview.visibility = View.VISIBLE

                    // 지나가지 않은 일정 + 내가 작성자
                    if(scheduleDetail.isWriter){
                        this.scheduleEmptyReviewTitle.text = getString(R.string.text_schedule_info_writer_not_leave_title)
                        this.scheduleEmptyReviewContent.text = getString(R.string.text_schedule_info_writer_not_leave_content)
                    }
                    // 지나가지 않은 일정 + 내가 작성자가 아님
                    else{
                        this.scheduleEmptyReviewTitle.text = getString(R.string.text_schedule_info_not_leave_title)
                        this.scheduleEmptyReviewContent.text = getString(R.string.text_schedule_info_not_leave_content)
                    }
                } ?: run {
                    // 지나간 일정
                    scheduleDday.visibility = View.GONE

                    // 리뷰가 있을때
                    if(scheduleDetail.hasReview){
                        val planId = intent.getLongExtra("planId", -1)
                        scheduleDetail.reviewId?.let {
                            initReviewMenu(scheduleDetail.isWriter, isUser, planId,
                                it
                            )
                        }

                        cardViewScheduleReview.visibility = View.VISIBLE
                        cardViewScheduleEmptyReview.visibility = View.GONE
                        this@ScheduleDetailActivity.setImageSmall(ivProfileImage, scheduleDetail.profileUrl)
                        textNickname.text = scheduleDetail.nickname
                        ratingBarScheduleSatisfaction.rating = scheduleDetail.grade?.toFloat() ?: 0F
                        textViewScheduleReviewContent.text = scheduleDetail.content

                        scheduleDetail.reviewImages?.let { reviewImages ->
                            when (reviewImages.size) {
                                1 -> {
                                    scheduleReviewImg1.visibility = View.VISIBLE
                                    this@ScheduleDetailActivity.setImageSmall(scheduleReviewImg1, reviewImages[0])
                                }
                                2 -> {
                                    scheduleReviewImg1.visibility = View.VISIBLE
                                    scheduleReviewImg2.visibility = View.VISIBLE
                                    this@ScheduleDetailActivity.setImageSmall(scheduleReviewImg1, reviewImages[0])
                                    this@ScheduleDetailActivity.setImageSmall(scheduleReviewImg2, reviewImages[1])
                                }
                                3 -> {
                                    scheduleReviewImg1.visibility = View.VISIBLE
                                    scheduleReviewImg2.visibility = View.VISIBLE
                                    scheduleReviewImg3.visibility = View.VISIBLE
                                    this@ScheduleDetailActivity.setImageSmall(scheduleReviewImg1, reviewImages[0])
                                    this@ScheduleDetailActivity.setImageSmall(scheduleReviewImg2, reviewImages[1])
                                    this@ScheduleDetailActivity.setImageSmall(scheduleReviewImg3, reviewImages[2])
                                }
                                4 -> {
                                    scheduleReviewImg1.visibility = View.VISIBLE
                                    scheduleReviewImg2.visibility = View.VISIBLE
                                    scheduleReviewImg3.visibility = View.VISIBLE
                                    scheduleReviewImg4.visibility = View.VISIBLE
                                    this@ScheduleDetailActivity.setImageSmall(scheduleReviewImg1, reviewImages[0])
                                    this@ScheduleDetailActivity.setImageSmall(scheduleReviewImg2, reviewImages[1])
                                    this@ScheduleDetailActivity.setImageSmall(scheduleReviewImg3, reviewImages[2])
                                    this@ScheduleDetailActivity.setImageSmall(scheduleReviewImg4, reviewImages[3])
                                }
                                5 -> {
                                    scheduleReviewImg1.visibility = View.VISIBLE
                                    scheduleReviewImg2.visibility = View.VISIBLE
                                    scheduleReviewImg3.visibility = View.VISIBLE
                                    scheduleReviewImg4.visibility = View.VISIBLE
                                    scheduleReviewImg5.visibility = View.VISIBLE
                                    this@ScheduleDetailActivity.setImageSmall(scheduleReviewImg1, reviewImages[0])
                                    this@ScheduleDetailActivity.setImageSmall(scheduleReviewImg2, reviewImages[1])
                                    this@ScheduleDetailActivity.setImageSmall(scheduleReviewImg3, reviewImages[2])
                                    this@ScheduleDetailActivity.setImageSmall(scheduleReviewImg4, reviewImages[3])
                                    this@ScheduleDetailActivity.setImageSmall(scheduleReviewImg5, reviewImages[4])
                                }
                            }
                        }


                    }
                    // 리뷰가 없을 때
                    else {
                        if (scheduleDetail.isWriter) {
                            cardViewScheduleReview.visibility = View.GONE
                            cardViewScheduleEmptyReview.visibility = View.VISIBLE
                            this.scheduleEmptyReviewTitle.text = getString(R.string.text_my_review)
                            this.scheduleEmptyReviewContent.text =
                                getString(R.string.text_leave_schedule_review)

                            cardViewScheduleEmptyReview.setOnClickListener {
                                val newIntent =
                                    Intent(
                                        this@ScheduleDetailActivity,
                                        WriteScheduleReviewActivity::class.java
                                    )
                                val planId = intent.getLongExtra("planId", -1)
                                newIntent.putExtra("planId", planId)
                                scheduleReviewLauncher.launch(newIntent)
                            }
                        } else {
                            cardViewScheduleEmptyReview.visibility = View.VISIBLE
                            this.scheduleEmptyReviewTitle.text =
                                getString(R.string.text_schedule_info_leave_title)
                            this.scheduleEmptyReviewContent.text =
                                getString(R.string.text_schedule_info_leave_content)
                        }
                    }

                }

                if (scheduleDetail.isPublic) {
                    schedulePublic.text = getString(R.string.text_schedule_public)
                } else {
                    schedulePublic.text = getString(R.string.text_schedule_private)
                }

                textViewScheduleName.text = scheduleDetail.title
                textViewSchedulePeriod.text = getString(
                    R.string.text_schedule_period,
                    scheduleDetail.startDate,
                    scheduleDetail.endDate
                )

                scheduleDetail.images?.let {
                    initImageViewPager(this@ScheduleDetailActivity, it)
                }
            }
        }
    }

    private fun checkLogin(){
        val planId = intent.getLongExtra("planId", -1)

        repeatOnStarted {
            viewModel.loginState.collect { uiState ->
                when (uiState) {
                    is LogInState.Checking -> {
                        return@collect
                    }

                    is LogInState.LoggedIn -> {
                        viewModel.getScheduleDetailInfo(planId)
                        initView(true, planId)
                    }

                    is LogInState.LoginRequired -> {
                        viewModel.getScheduleDetailInfoGuest(planId)
                        initView(false, planId)
                    }
                }
            }
        }
    }

    private fun initImageViewPager(context: Context, images: List<String>) {
        val imageList: List<String> = if (images.isEmpty()) {
            val drawableId = R.drawable.empty_view
            val uri = Uri.parse("android.resource://${context.packageName}/$drawableId").toString()
            listOf(uri)
        } else {
            images
        }

        binding.viewPagerScheduleImages.apply {
            val scheduleAdpater = ScheduleImageViewPagerAdapter(imageList)
            adapter = scheduleAdpater
            startAutoSlide(scheduleAdpater)
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

    }

    private fun startAutoSlide(adpater: ScheduleImageViewPagerAdapter) {
        val timer = Timer()
        val handler = Handler(Looper.getMainLooper())

        // 일정 간격으로 슬라이드 변경 (4초마다)
        timer.scheduleAtFixedRate(3000, 4000) {
            handler.post {
                val nextItem = binding.viewPagerScheduleImages.currentItem + 1
                if (nextItem < adpater.itemCount) {
                    binding.viewPagerScheduleImages.currentItem = nextItem
                } else {
                    binding.viewPagerScheduleImages.currentItem = 0 // 마지막 페이지에서 첫 페이지로 순환
                }
            }
        }
    }

    private fun initToolbarMenu(isUser: Boolean, isWriter: Boolean, isPublic: Boolean, isBookmark: Boolean, planId: Long) {

        binding.toolbarViewSchedule.apply {
            menu.clear()
            setNavigationOnClickListener {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            if (isWriter) { // 로그인한 사용자의 일정인 경우
                inflateMenu(R.menu.menu_schedule_private)
                binding.textViewScheduleType.text = getString(R.string.text_my_schedule)
                setOnMenuItemClickListener {
                    showScheduleManageBottomSheet(isPublic)
                    true
                }

            } else { // 로그인 여부와 상관없이 타인의 일정인 경우
                binding.textViewScheduleType.text = getString(R.string.text_public_schedule)
                inflateMenu(R.menu.menu_schedule_public)
                val menuItemBookmark =
                    binding.toolbarViewSchedule.menu.findItem(R.id.menuSchedulPublicBookmark)
                setBookmarkIcon(menuItemBookmark, isBookmark)
                setOnMenuItemClickListener {
                    if (isUser) { // 로그인한 사용자
                        viewModel.updateScheduleDetailBookmark(planId)
                        if(isBookmark){
                            showSnackbar("북마크가 취소되었습니다")
                        } else {
                            showSnackbar("북마크 되었습니다")
                        }
                    } else {
                        displayLoginDialog("여행 일정을 북마크하고 싶다면\n로그인을 진행해주세요")
                    }
                    true
                }
            }
        }
    }

    private fun setBookmarkIcon(menuItem: MenuItem, isBookmark: Boolean) {
        if (isBookmark) {
            menuItem.setIcon(R.drawable.bookmark_fill_scheduledetail_icon)
        } else {
            menuItem.setIcon(R.drawable.bookmark_schedule_detail_icon)
        }
    }

    private fun initReviewMenu(isWriter: Boolean, isUser: Boolean, planId: Long, reviewId: Long) {
        if (isWriter) {
            with(binding.imageButtonScheduleManageReview) {
                visibility = View.VISIBLE
                setOnClickListener {
                    showScheduleReviewManageBottomSheet(
                        planId = planId,
                        reviewId = reviewId
                    )
                }
            }
        } else {
            with(binding.buttonScheduleReportReview) {
                visibility = View.VISIBLE
                setOnClickListener {
                    if (isUser) {
                        val newIntent = Intent(
                            this@ScheduleDetailActivity,
                            ReportActivity::class.java
                        )
                        reportLauncher.launch(newIntent)
                    } else {
                        displayLoginDialog("여행 후기를 신고하고 싶다면\n로그인을 진행해주세요")
                    }
                }
            }
        }
    }

    private fun displayLoginDialog(subtitle: String) {
        val dialog = ConfirmDialog(
            "로그인이 필요해요!",
            subtitle,
            "로그인하기",
        ){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        dialog.isCancelable = true
        dialog.show(supportFragmentManager, "ScheduleLoginDialog")
    }

    private fun settingScheduleAdapter(scheduleDetail: ScheduleDetail) {
        binding.rvScheduleFullList.adapter = ScheduleListAdapter(scheduleDetail.dailyPlans,
            scheduleListListener = { schedulePosition, placePosition ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("detailPlaceId", scheduleDetail.dailyPlans[schedulePosition].schedulePlaces[placePosition].placeId)
                startActivity(intent)
            })
    }

    private fun showScheduleManageBottomSheet(isPublic: Boolean) {

        val planId = intent.getLongExtra("planId", -1)

        ScheduleManageBottomSheet(
            isPublic = isPublic,
            onScheduleStateToggleListener = {
                // 공개/비공개 상태 Toggle Listener
                // 공개 -> 비공개

                viewModel.updateMyPlanPublic(planId)
                if (isPublic) {
                    binding.cardViewScheduleReview.showSnackbar(getString(R.string.text_schedule_changed_to_private))
                }
                // 비공개 -> 공개
                else {
                    binding.cardViewScheduleReview.showSnackbar(getString(R.string.text_schedule_changed_to_public))
                }
            },
            onScheduleDeleteClickListener = {
                viewModel.deleteMyPlanSchedule(planId)
                setResult(RESULT_OK)
                finish()
            },
            onScheduleEditClickListener = {
                /*val newIntent =
                    Intent(this@ScheduleDetailActivity, ModifyScheduleFormActivity::class.java)
                newIntent.putExtra("planId", planId)
                scheduleReviewLauncher.launch(newIntent)*/
            }).show(supportFragmentManager, "ScheduleManageBottomSheet")
    }

    private fun showScheduleReviewManageBottomSheet(planId: Long, reviewId: Long) {
        ScheduleReviewManageBottomSheet(
            onReviewDeleteClickListener = {
                viewModel.deleteMyPlanReview(
                    reviewId = reviewId,
                    planId = planId
                )
                binding.imageButtonScheduleManageReview.showSnackbar(getString(R.string.text_schedule_review_deleted))
            },
            onReviewEditClickListener = {
                val newIntent =
                    Intent(this@ScheduleDetailActivity, ModifyScheduleReviewActivity::class.java)
                newIntent.putExtra("planId", planId)
                scheduleReviewLauncher.launch(newIntent)
            }).show(supportFragmentManager, "ScheduleReviewManageBottomSheet")
    }
}