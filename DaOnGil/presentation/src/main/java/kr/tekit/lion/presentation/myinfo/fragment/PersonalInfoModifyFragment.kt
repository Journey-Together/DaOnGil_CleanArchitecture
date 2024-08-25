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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentPersonalInfoModifyBinding
import kr.tekit.lion.presentation.ext.announceForAccessibility
import kr.tekit.lion.presentation.ext.formatBirthday
import kr.tekit.lion.presentation.ext.isScreenReaderEnabled
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.ext.setAccessibilityText
import kr.tekit.lion.presentation.ext.showSoftInput
import kr.tekit.lion.presentation.ext.toAbsolutePath
import kr.tekit.lion.presentation.main.dialog.ConfirmDialog
import kr.tekit.lion.presentation.myinfo.model.ModifyState
import kr.tekit.lion.presentation.myinfo.vm.MyInfoViewModel

@AndroidEntryPoint
class PersonalInfoModifyFragment : Fragment(R.layout.fragment_personal_info_modify) {
    private val viewModel: MyInfoViewModel by activityViewModels()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var albumLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private val myInfoAnnounce = StringBuilder()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPersonalInfoModifyBinding.bind(view)

        repeatOnViewStarted {
            viewModel.errorMessage.collect{
                if (it != null){
                    if (requireContext().isScreenReaderEnabled()){
                        requireActivity().announceForAccessibility(it)
                    }
                }
            }
        }

        if (requireContext().isScreenReaderEnabled()) {
            setupAccessibility(binding)
        } else {
            binding.toolbar.menu.clear()
        }

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri == null && requireContext().isScreenReaderEnabled()){
                requireActivity().announceForAccessibility(getString(R.string.text_modify_profile_img_unselected))
            }
            uri?.let {
                drawImage(binding.imgProfile, it)
                if (requireContext().isScreenReaderEnabled()){
                    requireActivity().announceForAccessibility(getString(R.string.text_modify_profile_img_selected))
                }
            }
        }

        albumLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri == null && requireContext().isScreenReaderEnabled()){
                    requireActivity().announceForAccessibility(getString(R.string.text_modify_profile_img_unselected))
                }
                uri?.let {
                    drawImage(binding.imgProfile, uri)
                    if (requireContext().isScreenReaderEnabled()){
                        requireActivity().announceForAccessibility(getString(R.string.text_modify_profile_img_selected))
                    }
                }
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
                        "권한 설정하기"
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


        val myInfo = viewModel.myPersonalInfo.value
        with(binding) {
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            tvNickname.setText(myInfo.nickname)
            tvPhone.setText(myInfo.phone)

            myInfoAnnounce.append(getString(R.string.text_nickname))
            myInfoAnnounce.append(myInfo.nickname)
            myInfoAnnounce.append(getString(R.string.text_phone))
            myInfoAnnounce.append(myInfo.phone)

            tvNickname.setAccessibilityText(
                if (myInfo.nickname.isEmpty()) getString(R.string.text_plz_enter_nickname)
                else myInfo.nickname
            )
            tvPhone.setAccessibilityText(
                if (myInfo.phone.isEmpty()) getString(R.string.text_plz_enter_phone)
                else myInfo.phone
            )

            tvNickname.doAfterTextChanged {
                if (it != null) tvNickname.setAccessibilityText(it)
                else tvNickname.setAccessibilityText(getString(R.string.text_plz_enter_nickname))
            }

            Glide.with(requireContext())
                .load(viewModel.profileImg.value.imagePath)
                .fallback(R.drawable.default_profile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imgProfile)

            btnModify.setOnClickListener {
                if (isPhotoPickerAvailable()) {
                    this@PersonalInfoModifyFragment.pickMedia.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } else {
                    checkPermission()
                }
            }

            btnSubmit.setOnClickListener {
                if (isFormValid(binding)) {
                    val state = viewModel.modifyState.value
                    val nickname = tvNickname.text.toString()
                    val phone = tvPhone.text.toString()

                    when (state) {
                        ModifyState.ImgSelected -> {
                            viewModel.onCompleteModifyPersonalWithImg(nickname, phone)
                        }

                        ModifyState.ImgUnSelected -> {
                            viewModel.onCompleteModifyPersonal(nickname, phone)
                        }
                    }

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

    private fun setupAccessibility(binding: FragmentPersonalInfoModifyBinding) {
        requireActivity().announceForAccessibility(
            getString(R.string.text_script_this_is_my_info_modify_page) +
            getString(R.string.text_script_read_all_text)
        )
        myInfoAnnounce.append(getString(R.string.text_personal_info))

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.read_script -> {
                    requireActivity().announceForAccessibility(getString(R.string.text_script_my_info_modify))
                    true
                }
                R.id.read_info -> {
                    requireActivity().announceForAccessibility(myInfoAnnounce.toString())
                    true
                }
                else -> false
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
        viewModel.onSelectProfileImage(path)
        viewModel.modifyStateChange()
    }


    private fun isFormValid(binding: FragmentPersonalInfoModifyBinding): Boolean {
        val phoneNumber = binding.tvPhone.text.toString()
        val phonePattern = "^010\\d{4}\\d{4}$"

        return if (binding.tvNickname.text.isNullOrBlank()) {
            val errorMessage = getString(R.string.text_plz_enter_nickname)

            binding.textInputLayoutUserNickname.error = errorMessage
            binding.tvNickname.requestFocus()
            context?.showSoftInput(binding.tvNickname)
            if (requireContext().isScreenReaderEnabled()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(2500)
                    requireActivity().announceForAccessibility(errorMessage)
                }
            }
            false
        } else if (phoneNumber.isNotBlank() and !phoneNumber.matches(phonePattern.toRegex())) {
            val errorMessage = getString(R.string.text_plz_enter_collect_phone_type) + "\n" +
                    getString(R.string.text_contact_ex)

            binding.textInputLayoutUserPhoneNumber.error = errorMessage

            binding.tvPhone.requestFocus()
            context?.showSoftInput(binding.tvPhone)
            if (requireContext().isScreenReaderEnabled()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(2500)
                    requireActivity().announceForAccessibility(errorMessage)
                }
            }


            false
        } else if (phoneNumber.isEmpty()) {
            val errorMessage = getString(R.string.text_plz_enter_phone)
            binding.textInputLayoutUserPhoneNumber.error = errorMessage

            binding.tvPhone.requestFocus()
            context?.showSoftInput(binding.tvPhone)

            if (requireContext().isScreenReaderEnabled()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(2500)
                    requireActivity().announceForAccessibility(errorMessage)
                }
            }
            false
        } else {
            true
        }
    }
}