package kr.tekit.lion.presentation.login.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentLoginBinding
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.login.vm.LoginViewModel
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentLoginBinding.bind(view)

        with(binding) {

            kakaoLoginButton.setOnClickListener {
                kakaoLogin()
            }

            naverLoginButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val result = runCatching { naverLogin() }
                    result.onSuccess { accessToken ->
                        viewModel.onCompleteLogIn("NAVER", accessToken)
                    }.onFailure { error ->
                        error.printStackTrace()
                    }
                }
            }
        }

        repeatOnViewStarted {
            viewModel.sigInInUiState.collectLatest {
                if (it) Navigation.findNavController(view).navigate(
                    R.id.to_selectInterestFragment
                )
            }
        }
    }

    private fun kakaoLogin() = repeatOnViewStarted {
        UserApiClient.login(requireContext())
            .onSuccess {
                viewModel.onCompleteLogIn("KAKAO", it.accessToken)
            }
    }

    private suspend fun UserApiClient.Companion.login(context: Context): Result<OAuthToken> = runCatching {
        if (instance.isKakaoTalkLoginAvailable(context)) {
            try {
                UserApiClient.loginWithKakaoTalk(context)
            } catch (error: Throwable) {
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    throw error
                } else {
                    UserApiClient.loginWithKakaoAccount(context)
                }
            }
        } else {
            UserApiClient.loginWithKakaoAccount(context)
        }
    }

    // 카카오톡으로 로그인 시도
    private suspend fun UserApiClient.Companion.loginWithKakaoTalk(context: Context): OAuthToken =
        suspendCoroutine { continuation ->
            instance.loginWithKakaoTalk(context) { token, error ->
                continuation.resumeTokenOrException(token, error)
            }
        }

    // 카카오 계정으로 로그인 시도
    private suspend fun UserApiClient.Companion.loginWithKakaoAccount(context: Context): OAuthToken =
        suspendCoroutine { continuation ->
            instance.loginWithKakaoAccount(context) { token, error ->
                continuation.resumeTokenOrException(token, error)
            }
        }

    private fun Continuation<OAuthToken>.resumeTokenOrException(
        token: OAuthToken?,
        error: Throwable?
    ) {
        if (error != null) {
            resumeWithException(error)
        } else if (token != null) {
            resume(token)
        } else {
            resumeWithException(RuntimeException("Can't Receive Kakao Access Token"))
        }
    }

    private suspend fun naverLogin(): String = suspendCoroutine { continuation->

        NaverIdLoginSDK.authenticate(requireContext(), object : OAuthLoginCallback {
            override fun onSuccess() {
                // 로그인 성공
                val accessToken = NaverIdLoginSDK.getAccessToken()
                if (accessToken != null) {
                    continuation.resume(accessToken)
                } else {
                    continuation.resumeWithException(RuntimeException("Can't Receive Naver Access Token"))
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                // 로그인 실패
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                continuation.resumeWithException(Exception(
                    "Naver Login Failed: $errorDescription (Error Code: $errorCode)"
                ))
            }

            override fun onError(errorCode: Int, message: String) {
                // 로그인 중 오류 발생
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                // 오류 처리
                continuation.resumeWithException(Exception(
                    "Naver Login Failed: $errorDescription (Error Code: $errorCode)"
                ))
            }
        })
    }
}