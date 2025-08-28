package kr.techit.lion.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.techit.lion.domain.model.LoginType
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentLoginBinding
import kr.techit.lion.presentation.delegate.NetworkEvent
import kr.techit.lion.presentation.ext.repeatOnViewStarted
import kr.techit.lion.presentation.ext.showSnackbar
import kr.techit.lion.presentation.login.vm.LoginViewModel
import kr.techit.lion.presentation.main.MainActivity

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentLoginBinding.bind(view)

        initView(binding)
        repeatOnViewStarted {
            launch { collectUiEvent() }
            launch { collectNetworkEvent(binding) }
        }
    }

    private fun initView(binding: FragmentLoginBinding) = with(binding) {
        kakaoLoginButton.setOnClickListener {
            kakaoLogin(binding)
        }

        naverLoginButton.setOnClickListener {
            naverLogin(binding)
        }

        btnBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun kakaoLogin(binding: FragmentLoginBinding) {
        binding.progressbar.visibility = View.VISIBLE

        val kakao = LoginType.KAKAO.toString()
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, _ ->
            if (token != null) {
                viewModel.signIn(
                    kakao,
                    token.accessToken,
                    token.refreshToken
                )
            }
        }
        if (UserApiClient.Companion.instance.isKakaoTalkLoginAvailable(requireContext())) {
            loginWithKakaoTalk(binding, kakao, callback)
        } else {
            loginWithKakaoAccount(callback)
        }
    }

    private fun loginWithKakaoTalk(
        binding: FragmentLoginBinding,
        loginType: String,
        callback: (OAuthToken?, Throwable?) -> Unit,
    ) {
        UserApiClient.Companion.instance.loginWithKakaoTalk(requireContext()) { token, error ->
            if (error != null) {
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    requireContext().showSnackbar(
                        binding.root,
                        requireContext().getString(R.string.error_fail_to_login)
                    )
                    return@loginWithKakaoTalk
                }

                loginWithKakaoAccount(callback)
            } else if (token != null) {
                viewModel.signIn(loginType, token.accessToken, token.refreshToken)
            }
        }
    }

    private fun loginWithKakaoAccount(callback: (OAuthToken?, Throwable?) -> Unit) {
        UserApiClient.Companion.instance.loginWithKakaoAccount(
            requireContext(),
            callback = callback
        )
    }

    private fun naverLogin(binding: FragmentLoginBinding) {
        binding.progressbar.visibility = View.VISIBLE
        NaverIdLoginSDK.authenticate(requireContext(), object : OAuthLoginCallback {
            override fun onSuccess() {
                loginWithNaver()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                requireContext().showSnackbar(
                    binding.root,
                    requireContext().getString(R.string.error_fail_to_login)
                )
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
                requireContext().showSnackbar(
                    binding.root,
                    requireContext().getString(R.string.error_fail_to_login)
                )
            }
        })
    }

    private fun loginWithNaver() {
        val naver = LoginType.NAVER.toString()
        val accessToken = NaverIdLoginSDK.getAccessToken()
        val refreshToken = NaverIdLoginSDK.getRefreshToken()

        if (accessToken != null && refreshToken != null) {
            viewModel.signIn(naver, accessToken, refreshToken)
        }
    }

    private suspend fun collectUiEvent() {
        viewModel.uiEvent.collect { event ->
            when (event) {
                LoginUiEvent.NavigateToMain -> {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                }

                LoginUiEvent.NavigateToSelectConcern -> {
                    view?.findNavController()?.navigate(R.id.to_selectInterestFragment)
                }
            }
        }
    }

    private suspend fun collectNetworkEvent(binding: FragmentLoginBinding) = with(binding) {
        viewModel.networkEvent.collect { event ->
            when (event) {
                NetworkEvent.Loading -> {
                    progressbar.visibility = View.VISIBLE
                }

                is NetworkEvent.Error -> {
                    progressbar.visibility = View.GONE
                    requireContext().showSnackbar(binding.root, event.msg)
                }

                NetworkEvent.Success -> {
                    progressbar.visibility = View.GONE
                }
            }
        }
    }
}
