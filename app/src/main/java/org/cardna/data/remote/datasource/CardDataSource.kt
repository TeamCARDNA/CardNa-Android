package org.cardna.data.remote.datasource

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.cardna.data.remote.model.card.*

interface CardDataSource {

    suspend fun getDetailCard(cardId: Int): ResponseDetailCardData

    suspend fun deleteCard(cardId: Int): ResponseDeleteCardData

    suspend fun putKeepOrAddCard(cardId: Int): ResponseKeepOrAddCardData

    suspend fun getCardAll(): ResponseCardAllData

    suspend fun getCardAllList() :ResponseCardAllListData

    suspend fun getCardMe() : ResponseCardMeData

    suspend fun getOtherCardMe(userId: Int) : ResponseCardMeData

    suspend fun getCardYou(): ResponseCardYouData

    suspend fun getOtherCardYou(cardId: Int): ResponseCardYouData

    suspend fun getMainCard(): ResponseMainCardData

    suspend fun getOtherMainCard(userId: Int): ResponseMainCardData

    suspend fun putEditCard(cards: RequestEditCardData): ResponseMainCardData

    suspend fun postCreateCardMe(body: HashMap<String, RequestBody>, image: MultipartBody.Part?) : ResponseCreateCardData

    suspend fun getCardYouStore(): ResponseCardYouStoreData
}