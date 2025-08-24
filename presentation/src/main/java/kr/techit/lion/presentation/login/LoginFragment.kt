package kr.techit.lion.presentation.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import kr.techit.lion.domain.model.LoginType
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentLoginBinding
import kr.techit.lion.presentation.ext.repeatOnViewStarted
import kr.techit.lion.presentation.login.model.UserType
import kr.techit.lion.presentation.login.vm.LoginViewModel
import kr.techit.lion.presentation.main.MainActivity

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentLoginBinding.bind(view)

        initView(binding)
        collectUserType(binding)
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
                viewModel.signIn(kakao, token.accessToken, token.refreshToken)
            }
        }
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.Companion.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.Companion.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.Companion.instance.loginWithKakaoAccount(
                        requireContext(),
                        callback = callback
                    )
                } else if (token != null) {
                    viewModel.signIn(kakao, token.accessToken, token.refreshToken)
                }
            }
        } else {
            UserApiClient.Companion.instance.loginWithKakaoAccount(requireContext(), callback = callback)
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
                    viewModel.signIn(naver, accessToken, refreshToken)
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                // 로그인 실패
                binding.progressbar.visibility = View.GONE
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Log.d("NaverLoginFailed", "$errorCode : $errorDescription")
            }

            override fun onError(errorCode: Int, message: String) {
                // 로그인 중 오류 발생
                binding.progressbar.visibility = View.GONE
                onFailure(errorCode, message)
            }
        })
    }

    private fun collectUserType(binding: FragmentLoginBinding){
        repeatOnViewStarted {
            viewModel.userType.collect { state ->
                when (state) {
                    UserType.Checking -> return@collect
                    UserType.NewUser -> {
                        binding.progressbar.visibility = View.VISIBLE
                        view?.findNavController()?.navigate(R.id.to_selectInterestFragment)
                    }

                    UserType.ExistingUser -> {
                        binding.progressbar.visibility = View.VISIBLE
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                }
            }
        }
    }
}
