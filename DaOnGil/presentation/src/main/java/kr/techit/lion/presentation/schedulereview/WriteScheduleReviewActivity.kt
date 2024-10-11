package kr.techit.lion.presentation.schedulereview

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions
import android.provider.Settings
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.techit.lion.domain.model.schedule.NewScheduleReview
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ActivityWriteScheduleReviewBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.numberToKorean
import kr.techit.lion.presentation.ext.setImage
import kr.techit.lion.presentation.ext.showSnackbar
import kr.techit.lion.presentation.ext.toAbsolutePath
import kr.techit.lion.presentation.main.dialog.ConfirmDialog
import kr.techit.lion.presentation.schedule.ResultCode
import kr.techit.lion.presentation.schedule.ResultCode.RESULT_REVIEW_WRITE
import kr.techit.lion.presentation.schedulereview.adapter.WriteReviewImageAdapter
import kr.techit.lion.presentation.schedulereview.vm.WriteScheduleReviewViewModel
import java.util.Locale

@AndroidEntryPoint
class WriteScheduleReviewActivity : AppCompatActivity() {

    private val viewModel: WriteScheduleReviewViewModel by viewModels()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                saveImageDataAndPath(uri)
            }
        }

    private val albumLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 사진 선택을 완료한 후 돌아왔다면
            if (result.resultCode == Activity.RESULT_OK) {
                // 선택한 이미지의 Uri 가져오기
                val uri = result.data?.data
                uri?.let {
                    saveImageDataAndPath(uri)
                }
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startAlbumLauncher()
            } else {
                val permissionDialog = ConfirmDialog(
                    "권한 설정",
                    "갤러리 이용을 위해 권한 설정이 필요합니다",
                    "권한 설정",
                ) {
                    // 앱 설정 화면으로 이동
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                permissionDialog.isCancelable = false
                permissionDialog.show(supportFragmentManager, "ScheduleReviewbnPermissionDialog")
            }
        }

    private val binding: ActivityWriteScheduleReviewBinding by lazy {
        ActivityWriteScheduleReviewBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val planId = intent.getLongExtra("planId", -1)

        settingProgressBarVisibility()

        initToolbar()
        initView(planId)
        initReviewContentWatcher()
        settingImageRVAdapter()
        settingButtonClickListener(planId)
    }

    private fun settingProgressBarVisibility() {
        viewModel.resetNetworkState()

        with(binding) {
            lifecycleScope.launch {
                viewModel.networkState.collectLatest { state ->
                    when (state) {
                        is NetworkState.Loading -> {
                            progressBarWsr.visibility = View.VISIBLE
                        }

                        is NetworkState.Success -> {
                            progressBarWsr.visibility = View.GONE
                        }

                        is NetworkState.Error -> {
                            progressBarWsr.visibility = View.GONE
                            val errorMsg = state.msg.replace("\n ".toRegex(), "\n")
                            buttonWsrSubmit.showSnackbar(errorMsg)
                        }
                    }
                }
            }
        }
    }

    private fun initToolbar() {
        binding.toolbarWriteScheduleReview.setNavigationOnClickListener {
            setResult(ResultCode.RESULT_SCHEDULE_REVIEW_CANCELED)
            finish()
        }
    }

    private fun initView(planId: Long) {
        viewModel.getBriefScheduleInfo(planId)
        viewModel.briefSchedule.observe(this@WriteScheduleReviewActivity) { briefSchedule ->
            binding.apply {
                textWsrScheduleTitle.text = briefSchedule?.title
                textWsrSchedulePeriod.text = getString(
                    R.string.text_schedule_period,
                    briefSchedule?.startDate,
                    briefSchedule?.endDate
                )
                imageWsrScheduleThumbnail.context.setImage(
                    imageWsrScheduleThumbnail,
                    briefSchedule?.imageUrl
                )

                cardWsrSchedule.contentDescription = viewModel.getScheduleInfoAccessibilityText()
            }
        }

        // RatingBar 현재 선택한 별의 갯수 알려주기
        binding.ratingbarWsr.apply {
            setOnRatingChangeListener { ratingBar, rating, fromUser ->
                val formattedRating = String.format(Locale.KOREA, "%.1f", rating)

                contentDescription = getString(
                    R.string.accessibility_text_schedule_satisfaction,
                    formattedRating
                )
            }
        }
        viewModel.numOfImages.observe(this@WriteScheduleReviewActivity) { numOfImgs ->
            with(binding) {
                textWsrPhotoNum.apply {
                    text = getString(R.string.text_num_of_images, numOfImgs)

                    // 첨부 가능한 이미지 수 안내
                    val availability = 4 - numOfImgs
                    buttonWsrAddPhoto.contentDescription =
                        getString(
                            R.string.accessibility_text_photo_add,
                            numOfImgs.numberToKorean(),
                            availability.numberToKorean()
                        )
                }
            }
        }
    }

    private fun settingImageRVAdapter() {
        viewModel.imageUriList.observe(this) { imageUriList ->
            val scheduleReviewImageAdapter = WriteReviewImageAdapter(imageUriList) { position ->
                viewModel.removeReviewImageFromList(position)
            }
            binding.recyclerViewWsrPhotos.adapter = scheduleReviewImageAdapter
        }
    }

    private fun settingButtonClickListener(planId: Long) {
        binding.apply {
            buttonWsrAddPhoto.setOnClickListener {
                if (!viewModel.isMoreImageAttachable()) {
                    it.showSnackbar("사진은 최대 4개까지 첨부할 수 있습니다")
                    return@setOnClickListener
                }
                if (isPhotoPickerAvailable()) {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } else {
                    checkPermission()
                }
            }

            buttonWsrSubmit.setOnClickListener {
                val isValid = isReviewValid()
                if (!isValid) return@setOnClickListener

                val isPrivateOrPublic = radioGroupWsrPublicScope.checkedRadioButtonId
                val isPublic = when (isPrivateOrPublic) {
                    R.id.radio_button_wsr_public -> true
                    else -> false // isPrivateOrPublic == R.id.radioButtonWriteScheReviewPrivate
                }

                val reviewContent = editWsrContent.text.toString()
                val reviewRating = ratingbarWsr.rating

                val reviewDetail = NewScheduleReview(reviewRating, reviewContent, isPublic)

                viewModel.submitScheduleReview(planId, reviewDetail) { _, requestFlag ->
                    if (requestFlag) {
                        setResult(RESULT_REVIEW_WRITE)
                        finish()
                    }
                }
            }
        }
    }

    private fun saveImageDataAndPath(uri: Uri) {
        val imagePath = toAbsolutePath(uri)
        if (imagePath != null) {
            viewModel.addNewReviewImage(uri, imagePath)
        }
    }

    // 갤러리 접근 권한 확인 함수
    private fun checkPermission() {
        val permissionReadExternal = android.Manifest.permission.READ_EXTERNAL_STORAGE

        val permissionReadExternalGranted = ContextCompat.checkSelfPermission(
            this,
            permissionReadExternal
        ) == PackageManager.PERMISSION_GRANTED

        // 포토피커를 사용하지 못하는 버전만 권한 확인 (SDK 30 미만)
        if (permissionReadExternalGranted) {
            startAlbumLauncher()
        } else {
            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun isPhotoPickerAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2
        } else {
            false
        }
    }

    private fun startAlbumLauncher() {
        val albumIntent = Intent(Intent.ACTION_GET_CONTENT)
        albumIntent.type = "image/*"  // 이미지 타입만 선택하도록 설정
        albumLauncher.launch(albumIntent)
    }

    private fun isReviewValid(): Boolean {
        with(binding) {
            val isPrivateOrPublic = radioGroupWsrPublicScope.checkedRadioButtonId
            if (isPrivateOrPublic == View.NO_ID) {
                radioGroupWsrPublicScope.showSnackbar("여행 일정 공개 범주를 선택해주세요")
                return false
            }

            val reviewContent = editWsrContent.text.toString()
            if (reviewContent.isBlank()) {
                textInputWsrContent.error = getString(R.string.text_warning_review_content_empty)
                return false
            }
        }
        return true
    }

    private fun initReviewContentWatcher() {
        with(binding) {
            editWsrContent.addTextChangedListener {
                textInputWsrContent.error = null
            }
        }
    }
}