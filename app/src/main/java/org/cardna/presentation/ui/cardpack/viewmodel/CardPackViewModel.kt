package org.cardna.presentation.ui.cardpack.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.cardna.data.remote.model.card.ResponseCardMeData
import org.cardna.data.remote.model.card.ResponseCardYouData
import org.cardna.data.remote.model.card.ResponseCardYouStoreData
import org.cardna.domain.repository.CardRepository
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class CardPackViewModel @Inject constructor(
    private val cardRepository: CardRepository, // 이렇게 쓰는 거 맞나
) : ViewModel() { // FriendCardPackActivity 와 CardPack, CardYou, CardMeFragment 가 CardPackViewModel 사용



    // 어떤 id 의 사람의 카드팩 프래그먼트에 접근하는지
    private var _id: Int? = null
    val id: Int?
        get() = _id

    // 어떤 name 의 사람의 카드팩 프래그먼트에 접근하는지
    private var _name: String? = null
    val name: String?
        get() = _name

    // 그 사람의 카드팩의 총 카드 개수 => CardPackFragment 의 textView 에 바인딩
    private val _totalCardCnt = MutableLiveData<Int>(0)
    val totalCardCnt: LiveData<Int>
        get() = _totalCardCnt

    // 카드나 List => CardMeFragment 에서 사용
    private val _cardMeList = MutableLiveData<MutableList<ResponseCardMeData.CardList.CardMe>>(
        mutableListOf(
            ResponseCardMeData.CardList.CardMe(7, "야구 좋아해요", "https://firebasestorage.googleapis.com/v0/b/cardna-29f5b.appspot.com/o/20220115_204842_6742281400.jpg?alt=media", 6, null)
        ))
    val cardMeList: LiveData<MutableList<ResponseCardMeData.CardList.CardMe>>
        get() = _cardMeList

    private val _isCardMeEmpty = MutableLiveData<Boolean>(true)
    val isCardMeEmpty: LiveData<Boolean> = _isCardMeEmpty

    // 카드나 List => CardMeFragment 에서 사용
    private val _cardYouList = MutableLiveData<MutableList<ResponseCardYouData.CardList.CardYou>>()
    val cardYouList: LiveData<MutableList<ResponseCardYouData.CardList.CardYou>>
        get() = _cardYouList

    private val _isCardYouEmpty = MutableLiveData<Boolean>(true)
    val isCardYouEmpty: LiveData<Boolean> = _isCardYouEmpty

    private val _cardYouStoreList = MutableLiveData<MutableList<ResponseCardYouStoreData.Data>>()
    val cardYouStoreList: LiveData<MutableList<ResponseCardYouStoreData.Data>>
        get() = _cardYouStoreList

    fun setUserId(id: Int?) {
        _id = id
    } // 타인의 프래그먼트 생성시, 그 프래그먼트 코드 단에서 getArguments 로 받아온 newId를 setUserId(newId) 이런형식으로 설정 ?

    fun setUserName(name: String?) {
        _name = name
    }

    fun setTotalCardCnt() { // 본인 카드팩 접근시에만 필요
        viewModelScope.launch {
            runCatching{
                cardRepository.getCardAll().data
            }.onSuccess {
                _totalCardCnt.value = it.totalCardCnt
                Timber.e("CardPack: setTotalCnt")
            }.onFailure {
                Timber.e(it.toString())
            }
        }
    }

    fun updateCardMeList() {
        if (_id == null) { // 본인의 카드나 접근
            viewModelScope.launch {
                runCatching {
                    cardRepository.getCardMe().data
                }.onSuccess {
                    it.apply {
                        _cardMeList.value = it.cardMeList
                        _isCardMeEmpty.value = (it.totalCardCnt == 0) // 0일 때 true
                        Timber.e("CardMe: updateCardMeList")
                    }
                }.onFailure {
                    Timber.e(it.toString())
                }
            }
        } else { // 타인의 카드나 접근
            viewModelScope.launch {
                runCatching {
                    cardRepository.getOtherCardMe(_id!!).data
                }.onSuccess {
                    it.apply {
                        _cardMeList.value= it.cardMeList
                        _isCardMeEmpty.value = (it.totalCardCnt == 0)
                    }
                }.onFailure {
                    Timber.e(it.toString())
                }
            }
        }
    }


    fun updateCardYouList() {
        if (_id == null) { // 본인의 카드너 접근
            viewModelScope.launch {
                runCatching {
                    cardRepository.getCardYou().data
                }.onSuccess {
                    it.apply {
                        _cardYouList.value = it.cardYouList
                        _isCardYouEmpty.value = (it.totalCardCnt == 0)
                    }
                }.onFailure {
                    Timber.e(it.toString())
                }
            }
        } else { // 타인의 카드너 접근
            viewModelScope.launch {
                runCatching {
                    cardRepository.getOtherCardYou(_id!!).data
                }.onSuccess {
                    it.apply {
                        _cardYouList.value= it.cardYouList
                        _isCardYouEmpty.value = (it.totalCardCnt == 0)
                    }
                }.onFailure {
                    Timber.e(it.toString())
                }
            }
        }
    }

    fun getCardYouStore() {
        viewModelScope.launch {
            runCatching {
                cardRepository.getCardYouStore().data
            }.onSuccess {
                _cardYouStoreList.value = it
            }.onFailure {
                Timber.e(it.toString())
            }
        }
    }
}