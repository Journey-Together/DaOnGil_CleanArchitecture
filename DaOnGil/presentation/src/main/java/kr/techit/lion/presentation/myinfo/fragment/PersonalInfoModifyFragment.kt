package kr.techit.lion.presentation.myinfo.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.techit.lion.domain.model.PersonalInfo
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentPersonalInfoModifyBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.announceForAccessibility
import kr.techit.lion.presentation.ext.formatPhoneNumber
import kr.techit.lion.presentation.ext.isPhoneNumberValid
import kr.techit.lion.presentation.ext.isTallBackEnabled
import kr.techit.lion.presentation.ext.repeatOnViewStarted
import kr.techit.lion.presentation.ext.setAccessibilityText
import kr.techit.lion.presentation.ext.showInfinitySnackBar
import kr.techit.lion.presentation.ext.showSnackbar
import kr.techit.lion.presentation.ext.showSoftInput
import kr.techit.lion.presentation.ext.toAbsolutePath
import kr.techit.lion.presentation.main.dialog.ConfirmDialog
import kr.techit.lion.presentation.myinfo.intent.MyInfoIntent
import kr.techit.lion.presentation.myinfo.model.ImgModifyState
import kr.techit.lion.presentation.myinfo.vm.MyInfoViewModel
import kr.techit.lion.presentation.observer.ConnectivityObserver
import kr.techit.lion.presentation.observer.NetworkConnectivityObserver

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

        initLauncher(binding)
        initializeUiData(binding)
        initializeListener(binding)
        initializeTallBack(binding)

    }

    private fun initializeListener(binding: FragmentPersonalInfoModifyBinding) {
        with(binding) {
            btnSubmit.setOnClickListener {
                if (isFormValid(binding)) {
                    repeatOnViewStarted { collectUiState(binding) }

                    val name = tvName.text.toString()
                    val nickname = tvNickname.text.toString()
                    val phone = tvPhone.text.toString()
                    viewModel.onChangeUiEvent(
                        MyInfoIntent.OnUiEventModifyPersonalInfo(PersonalInfo(name, nickname, phone))
                    )
                }
            }

            btnImgModify.setOnClickListener {
                if (isPhotoPickerAvailable()) {
                    this@PersonalInfoModifyFragment
                        .pickMedia
                        .launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } else {
                    checkPermission()
                }
            }
        }
    }

    private fun initializeTallBack(binding: FragmentPersonalInfoModifyBinding) {
        if (requireContext().isTallBackEnabled()) setupAccessibility(binding)
        else binding.toolbar.menu.clear()
    }

    private suspend fun collectUiState(binding: FragmentPersonalInfoModifyBinding) {
        viewModel.state.collect { state ->
            onNetworkStateChanged(binding, state.personalModifyNetworkState)
        }
    }

    private fun onNetworkStateChanged(
        binding: FragmentPersonalInfoModifyBinding,
        state: NetworkState
    ) {
        when (state) {
            is NetworkState.Loading -> binding.progressBar.visibility = View.VISIBLE
            is NetworkState.Success -> {
                binding.progressBar.visibility = View.GONE
                Snackbar.make(binding.root, "개인 정보가 수정 되었습니다.", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.text_secondary)).show()
                findNavController().popBackStack()
            }

            is NetworkState.Error -> {
                binding.progressBar.visibility = View.GONE
                requireContext().showSnackbar(binding.root, state.msg)
            }
        }
    }

    private fun initializeUiData(binding: FragmentPersonalInfoModifyBinding) {
        val myInfo = viewModel.state.value.personalInfo

        with(binding) {
            backButton.setOnClickListener { findNavController().popBackStack() }

            val name = myInfo.userName
            val nickname = myInfo.nickname
            val phone = myInfo.phone

            tvName.setText(name)
            tvNickname.setText(nickname)
            tvPhone.setText(myInfo.phone)

            Glide.with(requireContext())
                .load(viewModel.state.value.profileImg)
                .fallback(R.drawable.default_profile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imgProfile)

            if (requireContext().isTallBackEnabled()) setTallBack(binding, phone, nickname)
        }
    }

    private fun setTallBack(
        binding: FragmentPersonalInfoModifyBinding,
        phone: String,
        nickname: String
    ) {
        with(binding) {
            tvPhoneTitle.setAccessibilityText(
                if (phone.isEmpty()) "${tvPhoneTitle.text} ${getString(R.string.text_plz_enter_phone)}"
                else "${tvPhoneTitle.text} ${phone.formatPhoneNumber()}"
            )
            tvNicknameTitle.setAccessibilityText(
                if (nickname.isEmpty()) "${tvNicknameTitle.text} ${getString(R.string.text_plz_enter_nickname)}"
                else "${tvNicknameTitle.text} $nickname"
            )

            tvNickname.setAccessibilityText(
                nickname.ifEmpty { getString(R.string.text_plz_enter_nickname) }
            )
            tvPhone.setAccessibilityText(
                phone.ifEmpty { getString(R.string.text_plz_enter_phone) }
            )

            tvNickname.doAfterTextChanged {
                if (it != null) tvNickname.setAccessibilityText(it)
                else tvNickname.setAccessibilityText(getString(R.string.text_plz_enter_nickname))
            }

            tvPhone.doAfterTextChanged {
                if (it != null) tvPhone.setAccessibilityText(it)
                else tvPhone.setAccessibilityText(getString(R.string.text_plz_enter_phone))
            }

            myInfoAnnounce.append("${getString(R.string.text_name)} $nickname")
            myInfoAnnounce.append("${getString(R.string.text_phone)} $phone")
        }
    }

    private fun initLauncher(binding: FragmentPersonalInfoModifyBinding) {
        setPickMediaLauncher(binding)
        setAlbumLauncher(binding)
        setPermissionLauncher()
    }

    private fun setPickMediaLauncher(binding: FragmentPersonalInfoModifyBinding) {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri == null && requireContext().isTallBackEnabled()) {
                requireActivity().announceForAccessibility(getString(R.string.text_modify_profile_img_unselected))
            }
            uri?.let {
                drawImage(binding.imgProfile, it)
                if (requireContext().isTallBackEnabled()) {
                    requireActivity().announceForAccessibility(getString(R.string.text_modify_profile_img_selected))
                }
            }
        }
    }

    private fun setAlbumLauncher(binding: FragmentPersonalInfoModifyBinding) {
        albumLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri == null && requireContext().isTallBackEnabled()) {
                    requireActivity().announceForAccessibility(getString(R.string.text_modify_profile_img_unselected))
                }
                uri?.let {
                    drawImage(binding.imgProfile, uri)
                    if (requireContext().isTallBackEnabled()) {
                        requireActivity().announceForAccessibility(getString(R.string.text_modify_profile_img_selected))
                    }
                }
            }
        }
    }

    private fun setPermissionLauncher() {
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
    }

    private fun setupAccessibility(binding: FragmentPersonalInfoModifyBinding) {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(3000)
            requireActivity().announceForAccessibility(
                getString(R.string.text_script_this_is_my_info_modify_page) +
                        getString(R.string.text_script_read_all_text)
            )
        }
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
        viewModel.onChangeUiEvent(MyInfoIntent.OnUiEventSelectProfileImage(path))
    }

    private fun isFormValid(binding: FragmentPersonalInfoModifyBinding): Boolean {
        with(binding) {
            if (tvName.text.isNullOrBlank()) {
                showErrorAndAnnounce(
                    textInputLayoutUserName,
                    tvName,
                    getString(R.string.text_plz_enter_name)
                )
                return false
            }
            if (tvNickname.text.isNullOrBlank()) {
                showErrorAndAnnounce(
                    textInputLayoutUserNickname,
                    tvNickname,
                    getString(R.string.text_plz_enter_nickname)
                )
                return false
            }

            val phoneNumber = tvPhone.text.toString()
            if (phoneNumber.isNotBlank() && !phoneNumber.isPhoneNumberValid()) {
                val errorMessage = "${getString(R.string.text_plz_enter_collect_phone_type)}\n${
                    getString(R.string.text_contact_ex)
                }"
                showErrorAndAnnounce(textInputLayoutUserPhoneNumber, tvPhone, errorMessage)
                return false
            }

            if (phoneNumber.isEmpty()) {
                showErrorAndAnnounce(
                    textInputLayoutUserPhoneNumber,
                    tvPhone,
                    getString(R.string.text_plz_enter_phone)
                )
                return false
            }
        }
        return true
    }

    private fun showErrorAndAnnounce(
        textInputLayout: TextInputLayout,
        textView: TextView,
        errorMessage: String
    ) {
        textInputLayout.error = errorMessage
        textView.requestFocus()
        context?.showSoftInput(textView)
        if (requireContext().isTallBackEnabled()) {
            viewLifecycleOwner.lifecycleScope.launch {
                delay(2500)
                requireActivity().announceForAccessibility(errorMessage)
            }
        }
    }
}