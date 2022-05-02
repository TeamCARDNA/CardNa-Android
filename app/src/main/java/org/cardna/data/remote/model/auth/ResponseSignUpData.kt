package org.cardna.data.remote.model.auth


data class ResponseSignUpData(
    val message: String,
    val status: Int,
    val success: Boolean,
    val data: Data,
) {
    data class Data(
        val accessToken: String,
        val code: String,
        val name: String,
        val refreshToken: String
    )
}