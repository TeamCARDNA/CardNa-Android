package org.cardna.data.remote.model.friend

data class ResponseApplyOrCancleFriendData(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: Data,
) {
    data class Data(
        val id: Int,
        val status: String,
    )
}