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
import kr.techit.lion.domain.model.schedule.ReviewImage
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ActivityModifyScheduleReviewBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.numberToKorean
import kr.techit.lion.presentation.ext.setImage
import kr.techit.lion.presentation.ext.showSnackbar
import kr.techit.lion.presentation.ext.toAbsolutePath
import kr.techit.lion.presentation.main.dialog.ConfirmDialog
import kr.techit.lion.presentation.schedule.ResultCode
import kr.techit.lion.presentation.schedulereview.adapter.ModifyReviewImageAdapter
import kr.techit.lion.presentation.schedulereview.model.OriginalScheduleReviewInfo
import kr.techit.lion.presentation.schedulereview.vm.ModifyScheduleReviewViewModel
import java.net.URI
import java.util.Locale

@AndroidEntryPoint
class ModifyScheduleReviewActivity : AppCompatActivity() {
    private val viewModel: ModifyScheduleReviewViewModel by viewModels()

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
                    "권한 설정", "갤러리 이용을 위해 권한 설정이 필요합니다", "권한 설정"
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

    private val binding: ActivityModifyScheduleReviewBinding by lazy {
        ActivityModifyScheduleReviewBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        settingProgressBarVisibility()

        initToolbar()
        setScheduleReviewInfo()

        initReviewContentWatcher()
        settingImageRVAdapter()
        settingButtonClickListner()
    }

    private fun settingProgressBarVisibility() {
        viewModel.resetNetworkState()

        with(binding) {
            lifecycleScope.launch {
                viewModel.networkState.collectLatest { state ->
                    when (state) {
                        is NetworkState.Loading -> {
                            progressBarMsr.visibility = View.VISIBLE
                        }

                        is NetworkState.Success -> {
                            progressBarMsr.visibility = View.GONE
                        }

                        is NetworkState.Error -> {
                            progressBarMsr.visibility = View.GONE
                            val errorMsg = state.msg.replace("\n ".toRegex(), "\n")
                            buttonMsrSubmit.showSnackbar(errorMsg)
                        }
                    }
                }
            }
        }
    }

    private fun initToolbar() {
        binding.toolbarModifyScheduleReview.setNavigationOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    @Suppress("DEPRECATION")
    private fun setScheduleReviewInfo() {
        val originalReviewInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                "reviewInfo",
                OriginalScheduleReviewInfo::class.java
            )
        } else {
            intent.getParcelableExtra("reviewInfo") as? OriginalScheduleReviewInfo
        }

        if(originalReviewInfo != null){
            viewModel.initOriginalScheduleReviewInto(originalReviewInfo)
        }

        initView()
    }

    private fun initView() {
        viewModel.originalReview.observe(this@ModifyScheduleReviewActivity) { scheduleReviewInfo ->
            with(binding) {
                textMsrScheduleTitle.text = scheduleReviewInfo.title
                textMsrSchedulePeriod.text = getString(
                    R.string.text_schedule_period,
                    scheduleReviewInfo?.startDate,
                    scheduleReviewInfo?.endDate
                )
                val isImageAvailable = scheduleReviewInfo.imageUrl.isNotEmpty()
                if (isImageAvailable) {
                    this@ModifyScheduleReviewActivity.setImage(
                        imageMsrScheduleThumbnail,
                        scheduleReviewInfo.imageUrl
                    )
                }
                cardMsrSchedule.contentDescription = viewModel.getScheduleInfoAccessibilityText()

                ratingbarMsr.rating = scheduleReviewInfo.grade
                editMsrContent.setText(scheduleReviewInfo.content)
            }
        }

        // RatingBar 현재 선택한 별의 갯수 알려주기
        binding.ratingbarMsr.apply {
            setOnRatingChangeListener { ratingBar, rating, fromUser ->
                val formattedRating = String.format(Locale.KOREA, "%.1f", rating)

                contentDescription = getString(
                    R.string.accessibility_text_schedule_satisfaction,
                    formattedRating
                )
            }
        }

        viewModel.numOfImages.observe(this@ModifyScheduleReviewActivity) { numOfImages ->
            with(binding) {
                textMsrPhotoNum.apply {
                    text = getString(R.string.text_num_of_images, numOfImages)

                    // 첨부 가능한 이미지 수
                    val availability = 4 - numOfImages
                    buttonMsrAddPhoto.contentDescription =
                        getString(
                            R.string.accessibility_text_photo_add,
                            numOfImages.numberToKorean(),
                            availability.numberToKorean()
                        )
                }
            }
        }
    }

    private fun settingImageRVAdapter() {
        viewModel.imageList.observe(this) { imageList ->
            val modifyReviewImageAdapter = ModifyReviewImageAdapter(imageList) { position ->
                viewModel.removeReviewImageFromList(position)
            }
            binding.recyclerViewMsrPhotos.adapter = modifyReviewImageAdapter
        }
    }

    private fun settingButtonClickListner() {
        binding.apply {
            buttonMsrAddPhoto.setOnClickListener {
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

            buttonMsrSubmit.setOnClickListener {
                val isValid = isReviewValid()
                if (!isValid) return@setOnClickListener

                submitScheduleReviewUpdate()
            }
        }
    }

    private fun submitScheduleReviewUpdate() {
        binding.apply {
            val reviewContent = editMsrContent.text.toString()
            val reviewGrade = ratingbarMsr.rating

            viewModel.updateScheduleReview(reviewGrade, reviewContent) { _, requestFlag ->
                if (requestFlag) {
                    setResult(ResultCode.RESULT_SCHEDULE_EDIT)
                    finish()
                }
            }
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

    private fun startAlbumLauncher() {
        val albumIntent = Intent(Intent.ACTION_GET_CONTENT)
        albumIntent.type = "image/*"  // 이미지 타입만 선택하도록 설정
        albumLauncher.launch(albumIntent)
    }

    private fun saveImageDataAndPath(uri: Uri) {
        val imagePath = toAbsolutePath(uri)
        if (imagePath != null) {
            try {
                val newImage = ReviewImage(imageUri = URI(uri.toString()), imagePath = imagePath)
                viewModel.addNewReviewImage(newImage)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun isReviewValid(): Boolean {
        with(binding) {
            val reviewContent = editMsrContent.text.toString()
            if (reviewContent.isBlank()) {
                textInputMsrContent.error =
                    getString(R.string.text_warning_review_content_empty)
                return false
            }
        }
        return true
    }

    private fun initReviewContentWatcher() {
        with(binding) {
            editMsrContent.addTextChangedListener {
                textInputMsrContent.error = null
            }
        }
    }
}