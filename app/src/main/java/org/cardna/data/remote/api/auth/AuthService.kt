package org.cardna.data.remote.api.auth

import org.cardna.data.remote.model.auth.RequestSignUpData
import org.cardna.data.remote.model.auth.ResponseTokenIssuanceData
import org.cardna.data.remote.model.auth.ResponseSignUpData
import org.cardna.data.remote.model.auth.ResponseSocialLoginData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    @GET("auth/kakao")
    suspend fun getKakaoLogin(): ResponseSocialLoginData

    @GET("auth/naver")
    suspend fun getNaverLogin(): ResponseSocialLoginData

    @POST("auth")
    suspend fun postSignUp(
        @Body body: RequestSignUpData
    ): ResponseSignUpData

    @GET("auth/token")
    suspend fun getTokenIssuance(): ResponseTokenIssuanceData

}