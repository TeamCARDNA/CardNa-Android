package org.cardna.presentation.ui.login.view

// import com.google.android.gms.tasks.OnCompleteListener

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
//import com.google.android.gms.tasks.OnCompleteListener
//import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.log.NidLog
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.AndroidEntryPoint
import org.cardna.BuildConfig
import org.cardna.R
import org.cardna.data.local.singleton.CardNaRepository
import org.cardna.databinding.ActivityLoginBinding
import org.cardna.presentation.MainActivity
import org.cardna.presentation.base.BaseViewUtil
import org.cardna.presentation.ui.login.viewmodel.LoginViewModel
import org.cardna.presentation.ui.setting.view.PrivacyPolicyActivity
import org.cardna.presentation.util.StatusBarUtil
import org.cardna.presentation.util.getErrorLog
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity :
    BaseViewUtil.BaseAppCompatActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun initView() {
        StatusBarUtil.setStatusBar(this, R.color.black)
        setClickListener()
        getDeviceToken()
//        testKakao()
    }

//    private fun getDeviceToken() {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                return@OnCompleteListener
//            }
//            // Get new FCM registration token
//            val token = task.result
//            CardNaRepository.fireBaseToken = token!!
//            Timber.d("fcm token ${CardNaRepository.fireBaseToken}")
//        })
//    }
    private fun getDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            CardNaRepository.fireBaseToken = token.toString()
            Timber.d("fcm token ${CardNaRepository.fireBaseToken}")
        })
    }

    private fun setPrivacyPolicyActivity(title: String, text: String): Intent {
        val intent = Intent(this, PrivacyPolicyActivity::class.java)
        intent.apply {
            putExtra("title", title)
            putExtra("text", text)
        }
        return intent
    }

    private fun setClickListener() {
        with(binding) {
            tvLoginPolicyUseOfTerm.setOnClickListener {
                val intent = setPrivacyPolicyActivity(
                    getString(R.string.policy_text_title),
                    getString(R.string.policy_text)
                )
                startActivity(intent)
            }
            tvLoginPolicyPrivate.setOnClickListener {
                val intent = setPrivacyPolicyActivity(
                    getString(R.string.privacy_text_title),
                    getString(R.string.privacy_text)
                )
                startActivity(intent)
            }
            btnLoginKakao.setOnClickListener {
                setKakaoBtnListener()
//                testKakao()
            }
            btnLoginNaver.setOnClickListener {
                Timber.d("naver login btn click")
                setNaverLogin()
            }

        }
    }

    private fun setNaverLogin() {


        // 1. 네이버 자체 소셜로그인을 통해 naverSocialToken 얻어와서 header token 에 끼우기
        NidLog.init()

        NaverIdLoginSDK.initialize(this,
            BuildConfig.NAVER_API_CLIENT_ID,
            BuildConfig.NAVER_API_CLIENT_SECRET,
            BuildConfig.NAVER_API_APP_NAME
        )

        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                Timber.d("naver onSuccess: ")
                loginViewModel.setNaverSocialUserToken(NaverIdLoginSDK.getAccessToken()!!)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Timber.d("naver ErrorCode : ${errorCode}")
                Timber.d("naver ErrorDescription : ${errorDescription}")
//            shortToast("errorCode:$errorCode, errorDesc:$errorDescription")
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        // 네이버 자체 소셜 로그인 호출
        NaverIdLoginSDK.authenticate(this, oauthLoginCallback)

        // 소셜로그인 API 호출을 위해 헤더토큰 naverToken으로 갈아끼움
        CardNaRepository.userToken = loginViewModel.naverSocialUserToken!!

        // 2. 소셜로그인 API 호출
        loginViewModel.getNaverLogin()

        if(loginViewModel.loginType == "signin"){ // 2 - 1. 재로그인 => MainActivity로 이동
            moveOnMain()
        }
        else if(loginViewModel.loginType == "signup"){ // 2 - 2. 회원가입
            moveOnSetName()
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun startSetNameActivity() {
        val intent = Intent(this, SetNameActivity::class.java)
        startActivity(intent)
    }

    private fun setKakaoBtnListener() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                getErrorLog(error)
            } else if (token != null) {
                //카카오 로그인 콜백
                with(CardNaRepository) {
                    kakaoAccessToken = token.accessToken
                    Timber.d("kakaoAccessToken : $kakaoAccessToken")
                }
                with(loginViewModel) {
                    getKakaoLogin()
                    isLogin.observe(this@LoginActivity) { success ->
                        if (success) {
                            startMainActivity()
                        } else {
                            startSetNameActivity()
                        }
                    }
                }
                finish()
            }
        }
        binding.btnLoginKakao.setOnClickListener {
            val context = this
            with(UserApiClient.instance) {
                if (isKakaoTalkLoginAvailable(context)) {
                    //kakaoTalk (o) ->  kakaoTalk login
                    loginWithKakaoTalk(context, callback = callback)
                } else {
                    //kakaoTalk (x) kakao account login
                    loginWithKakaoAccount(context, callback = callback)
                }
                accessTokenInfo { tokenInfo, error ->
                    if (error != null) {
                        getErrorLog(error)
                    } else if (tokenInfo != null) {
                        CardNaRepository.userUuid = tokenInfo.id.toString()
                        Timber.d("userUuid : ${tokenInfo.id}")
                        Timber.d("userUuid : ${CardNaRepository.userUuid}")
                    }
                }
            }
        }
    }

    private fun kakaoLogout() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Timber.d("logout success")
            } else {
                Timber.d("logout fail")
            }
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }

    private fun getErrorLog(error: Throwable) {
        when {
            error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                Timber.e("접근이 거부 됨(동의 취소)")
            }
            error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                Timber.e("유효하지 않은 앱")
            }
            error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                Timber.e("인증 수단이 유효하지 않아 인증할 수 없는 상태")
            }
            error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                Timber.e("요청 파라미터 오류")
            }
            error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                Timber.e("유효하지 않은 scope ID")
            }
            error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                Timber.e("설정이 올바르지 않음(android key hash)")
            }
            error.toString() == AuthErrorCause.ServerError.toString() -> {
                Timber.e("서버 내부 에러")
            }
            error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                Timber.e("앱이 요청 권한이 없음")
            }
            else -> { // Unknown
                Timber.e("기타 에러")
            }
        }
    }

    private fun moveOnMain() {
        Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }


    private fun moveOnSetName() {
        Intent(this, SetNameActivity::class.java).apply {
            startActivity(this)
        }
    }

//
//    private fun getDeviceToken() {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                return@OnCompleteListener
//            }
//            // Get new FCM registration token
//            val token = task.result
//            Timber.d("device token $token")
//        })
//    }
}