package kr.tekit.lion.presentation.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions
import android.provider.Settings
import android.text.Editable
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ActivityWriteReviewBinding
import kr.tekit.lion.presentation.delegate.NetworkState
import kr.tekit.lion.presentation.ext.repeatOnStarted
import kr.tekit.lion.presentation.ext.showInfinitySnackBar
import kr.tekit.lion.presentation.ext.showSoftInput
import kr.tekit.lion.presentation.ext.toAbsolutePath
import kr.tekit.lion.presentation.home.adapter.WriteReviewImageRVAdapter
import kr.tekit.lion.presentation.home.vm.WriteReviewViewModel
import kr.tekit.lion.presentation.main.dialog.ConfirmDialog
import kr.tekit.lion.presentation.observer.ConnectivityObserver
import kr.tekit.lion.presentation.observer.NetworkConnectivityObserver
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class WriteReviewActivity : AppCompatActivity() {
    private val binding: ActivityWriteReviewBinding by lazy {
        ActivityWriteReviewBinding.inflate(layoutInflater)
    }
    private val viewModel: WriteReviewViewModel by viewModels()
    private val selectedImages: ArrayList<Uri> = ArrayList()
    private lateinit var imageRVAdapter: WriteReviewImageRVAdapter
    private val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver.getInstance(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                if (selectedImages.size < 4) {
                    selectedImages.add(uri)
                    imageRVAdapter.notifyDataSetChanged()

                    binding.writeReviewImageNumTv.text = selectedImages.size.toString()

                    val path = this.toAbsolutePath(uri)
                    viewModel.setReviewImages(path!!)
                } else {
                    Snackbar.make(binding.root, "이미지는 최대 4장까지 첨부 가능합니다", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    private val albumLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 사진 선택을 완료한 후 돌아왔다면
            if (result.resultCode == Activity.RESULT_OK) {
                // 선택한 이미지의 Uri 가져오기
                val uri = result.data?.data
                uri?.let {
                    if (selectedImages.size < 4) {
                        // 이미지를 리스트에 추가하고 어댑터에 데이터 변경을 알림
                        selectedImages.add(it)
                        imageRVAdapter.notifyDataSetChanged()

                        binding.writeReviewImageNumTv.text = selectedImages.size.toString()

                        val path = this.toAbsolutePath(uri)
                        viewModel.setReviewImages(path!!)
                    } else {
                        Snackbar.make(binding.root, "이미지는 최대 4장까지 첨부 가능합니다", Snackbar.LENGTH_SHORT).show()
                    }
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
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                permissionDialog.isCancelable = false
                permissionDialog.show(supportFragmentManager, "PermissionDialog")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val placeId = intent.getLongExtra("reviewPlaceId", -1)
        val placeName = intent.getStringExtra("reviewPlaceName") ?: "관광지"

        repeatOnStarted {
            supervisorScope {
                launch { collectWriteReviewNetworkState() }
                launch { observeConnectivity() }
            }
        }
        settingToolbar()
        settingPlaceData(placeName)
        settingImageRVAdapter()
        settingBtn(placeId)
    }

    private fun settingToolbar() {
        binding.writeReviewToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun settingPlaceData(placeName: String) {
        binding.writeReviewTitleTv.text = placeName
    }

    private fun settingImageRVAdapter() {
        imageRVAdapter = WriteReviewImageRVAdapter(selectedImages) {position ->
            viewModel.deleteImage(position)
        }
        binding.writeReviewImageRv.adapter = imageRVAdapter
        binding.writeReviewImageRv.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun settingBtn(placeId: Long) {
        binding.writeReviewImageAddBtn.setOnClickListener {
            if (isPhotoPickerAvailable()) {
                this.pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                checkPermission()
            }
        }

        binding.writeReviewDateEdit.setOnClickListener {
            setDate()
        }

        binding.writeReviewBtn.setOnClickListener {
            if (checkReviewValid()) {
                val visitDate = viewModel.placeVisitDate.value
                val reviewRating = binding.writeReviewRatingbar.rating
                val reviewText = binding.writeReviewTextWriteEdit.text.toString()

                viewModel.writePlaceReviewData(placeId, visitDate!!, reviewRating, reviewText)
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
            applicationContext,
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

    private fun setDate() {
        val constraintsBuilder = CalendarConstraints.Builder()
        val maxValidator = DateValidatorPointBackward.now()

        constraintsBuilder.setValidator(maxValidator)

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTheme(R.style.DateRangePickerTheme)
            .setTitleText("방문 기간을 설정해주세요")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.show(supportFragmentManager, "WriteReviewDate")
        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            viewModel.setPlaceVisitDate(selectedDate)
            showPickedDates()
        }
    }

    private fun formatDateValue(date: LocalDate): String {
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.KOREA)

        return dateFormat.format(date)
    }

    private fun showPickedDates() {
        val visitDate = viewModel.placeVisitDate.value
        val visitDateValue = visitDate?.let {
            formatDateValue(visitDate)
        }
        binding.writeReviewDateEdit.text = Editable.Factory.getInstance().newEditable(visitDateValue)
    }

    private fun checkReviewValid(): Boolean {
        val date = viewModel.placeVisitDate.value
        val reviewText = binding.writeReviewTextWriteEdit.text.toString()

        return if (date == null) {
            Snackbar.make(binding.root, "방문 날짜를 선택해 주세요", Snackbar.LENGTH_SHORT).show()
            false

        } else if (reviewText.isEmpty()) {
            binding.writeReviewTextWriteEdit.requestFocus()
            this.showSoftInput(binding.writeReviewTextWriteEdit)
            Snackbar.make(binding.root, "후기 내용을 입력해 주세요", Snackbar.LENGTH_SHORT).show()

            false

        } else {
            true
        }
    }

    private suspend fun collectWriteReviewNetworkState() {
        viewModel.networkState.collectLatest { state ->
            when (state) {
                is NetworkState.Loading -> {
                }

                is NetworkState.Success -> {
                    finish()
                }

                is NetworkState.Error -> {
                    this@WriteReviewActivity.showInfinitySnackBar(binding.root, state.msg)
                }
            }
        }
    }

    private suspend fun observeConnectivity() {
        with(binding) {
            connectivityObserver.getFlow().collect { connectivity ->
                when (connectivity) {
                    ConnectivityObserver.Status.Available -> {
                        writeReviewBtn.isEnabled = true
                    }

                    ConnectivityObserver.Status.Unavailable -> {}
                    ConnectivityObserver.Status.Losing,
                    ConnectivityObserver.Status.Lost -> {
                        val msg = "${getString(R.string.text_network_is_unavailable)}\n" +
                                "${getString(R.string.text_plz_check_network)} "

                        writeReviewBtn.isEnabled = false
                        this@WriteReviewActivity.showInfinitySnackBar(binding.root, msg)
                    }
                }
            }
        }
    }
}