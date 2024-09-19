package kr.tekit.lion.presentation.myreview.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentMyReviewModifyBinding
import kr.tekit.lion.presentation.delegate.NetworkState
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.ext.showInfinitySnackBar
import kr.tekit.lion.presentation.ext.showSnackbar
import kr.tekit.lion.presentation.ext.showSoftInput
import kr.tekit.lion.presentation.ext.toAbsolutePath
import kr.tekit.lion.presentation.main.dialog.ConfirmDialog
import kr.tekit.lion.presentation.myreview.adapter.MyReviewModifyImageRVAdapter
import kr.tekit.lion.presentation.myreview.vm.MyReviewViewModel
import kr.tekit.lion.presentation.observer.ConnectivityObserver
import kr.tekit.lion.presentation.observer.NetworkConnectivityObserver
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class MyReviewModifyFragment : Fragment(R.layout.fragment_my_review_modify) {

    private val viewModel: MyReviewViewModel by activityViewModels()
    private val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver.getInstance(requireContext())
    }
    private val selectedImages: ArrayList<Uri> = ArrayList()
    private val imageRVAdapter: MyReviewModifyImageRVAdapter by lazy {
        MyReviewModifyImageRVAdapter(selectedImages) { position ->
            viewModel.deleteImage(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedImages.add(uri)
                imageRVAdapter.notifyDataSetChanged()

                val path = requireContext().toAbsolutePath(uri)
                viewModel.addNewImage(path!!)
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

                        val path = requireContext().toAbsolutePath(uri)
                        viewModel.setReviewImages(path!!)
                    } else {
                        requireContext().showSnackbar(requireView(), "이미지는 최대 4장까지 첨부 가능합니다.")
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
                    "권한 설정",
                    "갤러리 이용을 위해 권한 설정이 필요합니다",
                    "권한 설정"
                ) {
                    openPermissionsSettings()
                }
                permissionDialog.isCancelable = false
                permissionDialog.show(requireActivity().supportFragmentManager, "PermissionDialog")
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentMyReviewModifyBinding.bind(view)

        settingToolbar(binding)
        settingReviewData(binding)
        settingImageRVAdapter(binding)
        settingButton(binding)
        settingErrorHandling(binding)

        repeatOnViewStarted {
            supervisorScope {
                launch { collectMyReviewModifyState(binding) }
                launch { observeConnectivity(binding) }
            }
        }
    }

    private suspend fun collectMyReviewModifyState(binding: FragmentMyReviewModifyBinding) {
        viewModel.networkState.collect { networkState ->
            when (networkState) {
                is NetworkState.Loading -> {}
                is NetworkState.Success -> {
                    if(viewModel.isFromDetail.value == true) {
                        requireActivity().finish()
                    }
                }
                is NetworkState.Error -> {
                    requireActivity().showInfinitySnackBar(binding.root, networkState.msg)
                }
            }
        }
    }

    private suspend fun observeConnectivity(binding: FragmentMyReviewModifyBinding) {
        with(binding) {
            connectivityObserver.getFlow().collect { connectivity ->
                when (connectivity) {
                    ConnectivityObserver.Status.Available -> {
                        buttonMyReviewModify.isEnabled = true
                    }
                    ConnectivityObserver.Status.Unavailable,
                    ConnectivityObserver.Status.Losing,
                    ConnectivityObserver.Status.Lost -> {
                        buttonMyReviewModify.isEnabled = false
                        val msg = "${getString(R.string.text_network_is_unavailable)}\n" +
                                "${getString(R.string.text_plz_check_network)} "
                        requireContext().showInfinitySnackBar(buttonMyReviewModify, msg)
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun settingReviewData(binding: FragmentMyReviewModifyBinding) {
        viewModel.reviewData.observe(viewLifecycleOwner) { review ->
            binding.textViewMyReviewModifyTitle.text = review.name
            binding.ratingbarMyReviewModify.rating = review.grade
            binding.textFieldMyReviewModifyDate.setText(review.date.toString())
            binding.textFieldMyReviewModifyWrite.setText(review.content)

            selectedImages.clear()
            selectedImages.addAll(review.images.map { Uri.parse(it) })
            imageRVAdapter.notifyDataSetChanged()

            viewModel.numOfImages.observe(viewLifecycleOwner) {
                binding.textViewMyReviewModifyPhotoNum.text = it.toString()
            }
        }
    }

    private fun settingToolbar(binding: FragmentMyReviewModifyBinding) {
        binding.toolbarMyReviewModify.setNavigationOnClickListener {
            if (viewModel.isFromDetail.value == true) {
                requireActivity().finish()
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun settingButton(binding: FragmentMyReviewModifyBinding) {
        binding.imageButtonMyReviewModify.setOnClickListener {
            if (!viewModel.isMoreImageAttachable()) {
                requireContext().showSnackbar(requireView(), "이미지는 최대 4장까지 첨부 가능합니다.")
                return@setOnClickListener
            }

            if (isPhotoPickerAvailable()) {
                this.pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                checkPermission()
            }
        }

        binding.textFieldMyReviewModifyDate.setOnClickListener {
            settingDate(binding)
        }

        binding.buttonMyReviewModify.setOnClickListener {
            if (isFormValid(binding)) {
                val grade = binding.ratingbarMyReviewModify.rating
                val date = viewModel.visitDate.value
                val content = binding.textFieldMyReviewModifyWrite.text.toString()

                viewModel.updateMyPlaceReview(viewModel.reviewData.value?.reviewId ?:0, grade, date!!, content)

                viewModel.snackbarEvent.observe(viewLifecycleOwner) { message ->
                    message?.let {
                        requireContext().showSnackbar(requireView(), message)
                        viewModel.resetSnackbarEvent()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun settingDate(binding: FragmentMyReviewModifyBinding) {
        val constraintsBuilder = CalendarConstraints.Builder()
        val maxValidator = DateValidatorPointBackward.now()

        constraintsBuilder.setValidator(maxValidator)

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTheme(R.style.DateRangePickerTheme)
            .setTitleText("방문 기간을 설정해주세요")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.show(parentFragmentManager, "WriteReviewDate")
        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            viewModel.setVisitDate(selectedDate)
            showPickedDates(binding)
        }
    }

    private fun showPickedDates(binding: FragmentMyReviewModifyBinding) {
        val visitDate = viewModel.visitDate.value
        val visitDateValue = visitDate?.let {
            formatDateValue(visitDate)
        }
        binding.textFieldMyReviewModifyDate.setText(visitDateValue)
    }

    private fun formatDateValue(date: LocalDate): String {
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.KOREA)

        return dateFormat.format(date)
    }

    private fun settingImageRVAdapter(binding: FragmentMyReviewModifyBinding) {
        binding.recyclerViewMyReviewModify.adapter = imageRVAdapter
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

    private fun checkPermission() {
        val permissionReadExternal = android.Manifest.permission.READ_EXTERNAL_STORAGE

        val permissionReadExternalGranted = ContextCompat.checkSelfPermission(
            requireContext().applicationContext,
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

    private fun isFormValid(binding: FragmentMyReviewModifyBinding): Boolean {
        val date = viewModel.visitDate.value
        val reviewContent = binding.textFieldMyReviewModifyWrite.text.toString()

        return if (date == null) {
            requireContext().showSnackbar(requireView(), "방문 날짜를 선택해 주세요.")
            false

        } else if (reviewContent.isEmpty()) {
            binding.textInputLayoutMyReviewModifyWrite.error = "후기 내용을 입력해 주세요"
            binding.textFieldMyReviewModifyWrite.requestFocus()
            requireContext().showSoftInput(binding.textFieldMyReviewModifyWrite)
            false
        } else {
            true
        }
    }

    private fun settingErrorHandling(binding: FragmentMyReviewModifyBinding) {
        binding.textFieldMyReviewModifyWrite.addTextChangedListener {
            clearErrorMessage(binding.textInputLayoutMyReviewModifyWrite)
        }
    }

    private fun clearErrorMessage(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
    }

    private fun openPermissionsSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}