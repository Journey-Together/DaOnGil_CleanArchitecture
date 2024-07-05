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

    private fun kakaoLogin(){
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (token != null) {
                viewModel.onCompleteLogIn("KAKAO", token.accessToken)
            }
        }
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
                } else if (token != null) {
                    viewModel.onCompleteLogIn("KAKAO", token.accessToken)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
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