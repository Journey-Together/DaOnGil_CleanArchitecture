package kr.techit.lion.presentation.home

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.connectivity.ConnectivityObserver
import kr.techit.lion.presentation.connectivity.NetworkConnectivityObserver
import kr.techit.lion.presentation.databinding.ActivityWriteReviewBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.copyUriToCacheFile
import kr.techit.lion.presentation.ext.repeatOnStarted
import kr.techit.lion.presentation.ext.showInfinitySnackBar
import kr.techit.lion.presentation.ext.showSoftInput
import kr.techit.lion.presentation.home.adapter.WriteReviewImageRVAdapter
import kr.techit.lion.presentation.home.vm.WriteReviewViewModel
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
        NetworkConnectivityObserver(applicationContext)
    }

    @SuppressLint("NotifyDataSetChanged")
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                if (selectedImages.size < 4) {
                    selectedImages.add(uri)
                    imageRVAdapter.notifyDataSetChanged()

                    val path = this.copyUriToCacheFile(uri)
                    viewModel.setReviewImages(path!!)

                    viewModel.numOfImages.observe(this) {
                        binding.writeReviewImageNumTv.text = it.toString()
                    }
                } else {
                    Snackbar.make(binding.root, "이미지는 최대 4장까지 첨부 가능합니다", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val placeId = intent.getLongExtra("reviewPlaceId", -1)
        val placeName = intent.getStringExtra("reviewPlaceName") ?: "관광지"

        repeatOnStarted {
            launch { collectWriteReviewNetworkState() }
            launch { observeConnectivity() }
        }
        settingToolbar()
        settingReviewData(placeName)
        settingImageRVAdapter()
        settingBtn(placeId)
    }

    private fun settingToolbar() {
        binding.writeReviewToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun settingReviewData(placeName: String) {
        binding.writeReviewTitleTv.text = placeName
        viewModel.numOfImages.observe(this) {
            binding.writeReviewImageNumTv.text = it.toString()
        }
    }

    private fun settingImageRVAdapter() {
        imageRVAdapter = WriteReviewImageRVAdapter(selectedImages) { position ->
            viewModel.deleteImage(position)
        }
        binding.writeReviewImageRv.adapter = imageRVAdapter
        binding.writeReviewImageRv.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun settingBtn(placeId: Long) {
        binding.writeReviewImageAddBtn.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
        binding.writeReviewDateEdit.text =
            Editable.Factory.getInstance().newEditable(visitDateValue)
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