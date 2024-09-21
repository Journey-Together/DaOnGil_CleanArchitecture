package kr.tekit.lion.presentation.login.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentLoginBinding
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.login.model.LoginType
import kr.tekit.lion.presentation.login.vm.LoginViewModel
import kr.tekit.lion.presentation.main.MainActivity

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentLoginBinding.bind(view)

        with(binding) {

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

        repeatOnViewStarted {
            viewModel.sigInInUiState.collectLatest {
                if (it) {
                    if (viewModel.isFirstUser.value) {
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }else{
                        Navigation.findNavController(view).navigate(R.id.to_selectInterestFragment)
                    }
                }
            }
        }
    }

    private fun kakaoLogin(binding: FragmentLoginBinding) {
        binding.progressbar.visibility = View.VISIBLE

        val kakao = LoginType.KAKAO.toString()
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (token != null) {
                viewModel.onCompleteLogIn(kakao, token.accessToken, token.refreshToken)
            }
        }
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        binding.progressbar.visibility = View.GONE
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
                } else if (token != null) {
                    viewModel.onCompleteLogIn(kakao, token.accessToken, token.refreshToken)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
        }
    }

    private fun naverLogin(binding: FragmentLoginBinding) {
        binding.progressbar.visibility = View.VISIBLE
        NaverIdLoginSDK.authenticate(requireContext(), object : OAuthLoginCallback {
            override fun onSuccess() {
                binding.progressbar.visibility = View.GONE

                val naver = LoginType.NAVER.toString()
                val accessToken = NaverIdLoginSDK.getAccessToken()
                val refreshToken = NaverIdLoginSDK.getRefreshToken()

                if (accessToken != null && refreshToken != null) {
                    viewModel.onCompleteLogIn(naver, accessToken, refreshToken)
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                // 로그인 실패
                binding.progressbar.visibility = View.GONE
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(context, "errorCode: ${errorCode}\n" +
                        "errorDescription: ${errorDescription}", Toast.LENGTH_SHORT).show()
            }

            override fun onError(errorCode: Int, message: String) {
                // 로그인 중 오류 발생
                binding.progressbar.visibility = View.GONE
                onFailure(errorCode, message)
            }
        })
    }
}