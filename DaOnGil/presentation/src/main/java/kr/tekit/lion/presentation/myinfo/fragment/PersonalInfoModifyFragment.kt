package kr.tekit.lion.presentation.myinfo.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentPersonalInfoModifyBinding
import kr.tekit.lion.presentation.ext.showSoftInput
import kr.tekit.lion.presentation.ext.toAbsolutePath
import kr.tekit.lion.presentation.myinfo.ConfirmDialog
import kr.tekit.lion.presentation.myinfo.vm.MyInfoViewModel

@AndroidEntryPoint
class PersonalInfoModifyFragment : Fragment(R.layout.fragment_personal_info_modify) {
    private val viewModel: MyInfoViewModel by activityViewModels()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var albumLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPersonalInfoModifyBinding.bind(view)

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { drawImage(binding.imgProfile, it) }
        }

        albumLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            // 사진 선택을 완료한 후 돌아왔다면
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let { drawImage(binding.imgProfile, uri) }
            }
        }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startAlbumLauncher()
                } else {
                    val permissionDialog = ConfirmDialog(
                        "권한 설정",
                        "갤러리 이용을 위해 권한 설정이 필요합니다",
                        "권한 설정"
                    ) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val packageName = requireContext().packageName
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri

                        startActivity(intent)
                    }
                    permissionDialog.isCancelable = false
                    permissionDialog.show(childFragmentManager, "PermissionDialog")
                }
            }


        //val myInfo = viewModel.myPersonalInfo.value
        with(binding) {
//            tvNickname.setText(myInfo.nickname)
//            tvPhone.setText(myInfo.phone)

//            Glide.with(requireContext())
//                .load(viewModel.profileImg.value)
//                .fallback(R.drawable.default_profile)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .into(imgProfile)

            btnModify.setOnClickListener {
                if (isPhotoPickerAvailable()) {
                    this@PersonalInfoModifyFragment.pickMedia.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } else {
                    checkPermission()
                }
            }

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            btnSubmit.setOnClickListener {
                if (isFormValid(binding)) {
                    //val state = viewModel.modifyState.value
                    val nickname = tvNickname.text.toString()
                    val phone = tvPhone.text.toString()

//                    when (state) {
//                        ModifyState.ImgSelected -> {
//                            viewModel.onCompleteModifyPersonalWithImg(nickname, phone)
//                        }
//
//                        ModifyState.ImgUnSelected -> {
//                            viewModel.onCompleteModifyPersonal(nickname, phone)
//                        }
//                    }

                    Snackbar.make(binding.root, "개인 정보가 수정 되었습니다.", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.text_secondary
                            )
                        )
                        .show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun checkPermission() {
        val permissionReadExternal = android.Manifest.permission.READ_EXTERNAL_STORAGE

        val permissionReadExternalGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            permissionReadExternal
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionReadExternalGranted) {
            startAlbumLauncher()
        } else {
            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun startAlbumLauncher() {
        val albumIntent = Intent(Intent.ACTION_GET_CONTENT)
        albumIntent.type = "image/*"
        albumLauncher.launch(albumIntent)
    }

    private fun isPhotoPickerAvailable(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> true
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> SdkExtensions.getExtensionVersion(
                Build.VERSION_CODES.R
            ) >= 2

            else -> false
        }
    }


    private fun drawImage(view: ImageView, imgUrl: Uri) {
        Glide.with(requireContext())
            .load(imgUrl)
            .fallback(R.drawable.default_profile)
            .into(view)
        val path = requireContext().toAbsolutePath(imgUrl)
//        viewModel.onSelectProfileImage(path)
//        viewModel.modifyStateChange()
    }


    private fun isFormValid(binding: FragmentPersonalInfoModifyBinding): Boolean {
        val phoneNumber = binding.tvPhone.text.toString()
        val phonePattern = "^010\\d{4}\\d{4}$"

        return if (binding.tvNickname.text.isNullOrBlank()) {
            binding.tvNickname.requestFocus()
            context?.showSoftInput(binding.tvNickname)
            binding.textInputLayoutUserNickname.error = "닉네임을 입력해주세요."
            false
        } else if (phoneNumber.isNotBlank() and !phoneNumber.matches(phonePattern.toRegex())) {
            binding.tvPhone.requestFocus()
            context?.showSoftInput(binding.tvPhone)
            binding.textInputLayoutUserPhoneNumber.error =
                "올바른 전화번호 형식을 입력해주세요.\n예: 01012345678"
            false
        } else if (phoneNumber.isEmpty()) {
            binding.tvPhone.requestFocus()
            context?.showSoftInput(binding.tvPhone)
            binding.textInputLayoutUserPhoneNumber.error =
                "전화번호를 입력해주세요."
            false
        } else {
            true
        }
    }
}