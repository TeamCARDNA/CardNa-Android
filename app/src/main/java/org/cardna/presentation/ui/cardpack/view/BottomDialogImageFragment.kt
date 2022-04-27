package org.cardna.ui.cardpack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.cardna.R
import com.example.cardna.databinding.FragmentBottomDialogImageBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.cardna.presentation.base.BaseViewUtil
import org.cardna.presentation.ui.cardpack.view.CardCreateActivity
import org.cardna.presentation.ui.cardpack.viewmodel.CardCreateViewModel

// 1. 카드나 작성
// 2. 카드너 작성
// 둘의 차이 ? 는 아직 잘 모르겠고
// 둘 분기처리는 삼항 연산자 이용해서 xml 상에서 처리해주기

class BottomDialogImageFragment(val itemClick: () -> Unit)
    : BaseViewUtil.BaseBottomDialogFragment<FragmentBottomDialogImageBinding>(R.layout.fragment_bottom_dialog_image) {
    private val cardCreateViewModel: CardCreateViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setListener() // for symbol
        accessGallery() // for gallery
    }

    override fun initView() {
        binding.cardCreateViewModel = cardCreateViewModel
    }


    // 이미지 선택하는 dialog 에서 갤러리 접근 버튼 눌렀을 때를 위해 리스너 달기
    // 버튼을 눌렀을 때, CardCreateActivity 의 checkPermission 메서드 실행
    private fun accessGallery() { // 갤러리에 접근하고 dialog 는 사라짐 => dialog 에서 완료 버튼 누를 일이 없음.
        binding.btnCardcreateGallery.setOnClickListener {
            (activity as CardCreateActivity).checkPermission()
            dialog?.dismiss()
        }
    }

    // 이미지 선택하는 dialog 에서 각 심볼 이미지 눌렀을 때를 위해 리스너 달기
    private fun setListener() {
        with(binding) {
            // 5개의 심볼, 이미지 버튼에서 하나를 선택하면 나머지 선택안되도록
            // 이미지버튼 중 하나라도 selected 되어있을 때, 완료 버튼 누를 수 있게 되고, 색깔 바뀜
            // 그리고 선택을 한 클릭일 때, 완료 버튼 선택할 수 있도록 바꾸기

            // 더 좋은 로직이 없을까
            imgBtnCardpackSymbol0.setOnClickListener {
                imgBtnCardpackSymbol0.isSelected = !imgBtnCardpackSymbol0.isSelected
                imgBtnCardpackSymbol1.isSelected = false
                imgBtnCardpackSymbol2.isSelected = false
                imgBtnCardpackSymbol3.isSelected = false
                imgBtnCardpackSymbol4.isSelected = false
                ifEnableCompleteBtn()  // 클릭될때마다 완료버튼 check

                // 카드나, 카드너 일때 분기처리 필요 => 여기서 분기처리할 필요 없고, Activity 에서 해주면 될 듯
                if (imgBtnCardpackSymbol0.isSelected) {
                    cardCreateViewModel?.setSymbolId(SYMBOL_0)
                    // imgIndex 는 카드나, 카드너 분기처리
                    if (cardCreateViewModel?.isCardMeOrYou!!)
                        cardCreateViewModel?.setImgIndex(R.drawable.ic_symbol_cardme_0)
                    else
                        cardCreateViewModel?.setImgIndex(R.drawable.ic_symbol_cardyou_0)
                } else
                    cardCreateViewModel?.setSymbolId(GALLERY)

            }

            imgBtnCardpackSymbol1.setOnClickListener {
                imgBtnCardpackSymbol0.isSelected = false
                imgBtnCardpackSymbol1.isSelected = !imgBtnCardpackSymbol1.isSelected
                imgBtnCardpackSymbol2.isSelected = false
                imgBtnCardpackSymbol3.isSelected = false
                imgBtnCardpackSymbol4.isSelected = false
                ifEnableCompleteBtn()

                if (imgBtnCardpackSymbol1.isSelected) {
                    cardCreateViewModel?.setSymbolId(SYMBOL_1)

                    // imgIndex 는 카드나, 카드너 분기처리
                    if (cardCreateViewModel?.isCardMeOrYou!!)
                        cardCreateViewModel?.setImgIndex(R.drawable.ic_symbol_cardme_1)
                    else
                        cardCreateViewModel?.setImgIndex(R.drawable.ic_symbol_cardyou_1)
                } else
                    cardCreateViewModel?.setSymbolId(GALLERY)
            }

            imgBtnCardpackSymbol2.setOnClickListener {
                imgBtnCardpackSymbol0.isSelected = false
                imgBtnCardpackSymbol1.isSelected = false
                imgBtnCardpackSymbol2.isSelected = !imgBtnCardpackSymbol2.isSelected
                imgBtnCardpackSymbol3.isSelected = false
                imgBtnCardpackSymbol4.isSelected = false
                ifEnableCompleteBtn()

                if (imgBtnCardpackSymbol2.isSelected) {
                    cardCreateViewModel?.setSymbolId(SYMBOL_2)

                    // imgIndex 는 카드나, 카드너 분기처리
                    if (cardCreateViewModel?.isCardMeOrYou!!)
                        cardCreateViewModel?.setImgIndex(R.drawable.ic_symbol_cardme_2)
                    else
                        cardCreateViewModel?.setImgIndex(R.drawable.ic_symbol_cardyou_2)
                } else
                    cardCreateViewModel?.setSymbolId(GALLERY)
            }

            imgBtnCardpackSymbol3.setOnClickListener {
                imgBtnCardpackSymbol0.isSelected = false
                imgBtnCardpackSymbol1.isSelected = false
                imgBtnCardpackSymbol2.isSelected = false
                imgBtnCardpackSymbol3.isSelected = !imgBtnCardpackSymbol3.isSelected
                imgBtnCardpackSymbol4.isSelected = false
                ifEnableCompleteBtn()

                if (imgBtnCardpackSymbol3.isSelected) {
                    cardCreateViewModel?.setSymbolId(SYMBOL_3)

                    // imgIndex 는 카드나, 카드너 분기처리
                    if (cardCreateViewModel?.isCardMeOrYou!!)
                        cardCreateViewModel?.setImgIndex(R.drawable.ic_symbol_cardme_3)
                    else
                        cardCreateViewModel?.setImgIndex(R.drawable.ic_symbol_cardyou_3)
                } else
                    cardCreateViewModel?.setSymbolId(GALLERY)
            }

            imgBtnCardpackSymbol4.setOnClickListener {
                imgBtnCardpackSymbol0.isSelected = false
                imgBtnCardpackSymbol1.isSelected = false
                imgBtnCardpackSymbol2.isSelected = false
                imgBtnCardpackSymbol3.isSelected = false
                imgBtnCardpackSymbol4.isSelected = !imgBtnCardpackSymbol4.isSelected
                ifEnableCompleteBtn()

                if (imgBtnCardpackSymbol4.isSelected) {
                    cardCreateViewModel?.setSymbolId(SYMBOL_4)

                    // imgIndex 는 카드나, 카드너 분기처리
                    if (cardCreateViewModel?.isCardMeOrYou!!)
                        cardCreateViewModel?.setImgIndex(R.drawable.ic_symbol_cardme_4)
                    else
                        cardCreateViewModel?.setImgIndex(R.drawable.ic_symbol_cardyou_4)
                } else
                    cardCreateViewModel?.setSymbolId(GALLERY)
            }

            // 완료 버튼 누르면 dialog 없어지고, CardCreateActivity 에서 dialog 를 생성할 때 넘겨준 itemClick() 메서드를 실행한다.
            // 완료 버튼 안 누르고 갤러리 접근하면 itemClick 호출 안함.
            btnCardcreateComplete.setOnClickListener {
                cardCreateViewModel?.setIfChooseImg(true) // symbol 들 중 하나로 선택했으니 IfChooseImg true 로 설정
                itemClick() // CardCreateActivity 에서 이 Fragment 를 생성할 때 넘겨준 람다를 완료버튼 누를 때 실행
                dialog?.dismiss()
            }
        }
    }

    private fun ifEnableCompleteBtn() {
        with(binding) {
            if (imgBtnCardpackSymbol0.isSelected || imgBtnCardpackSymbol1.isSelected || imgBtnCardpackSymbol2.isSelected ||
                imgBtnCardpackSymbol3.isSelected || imgBtnCardpackSymbol4.isSelected
            ) {
                btnCardcreateComplete.isEnabled = true
                btnCardcreateComplete.setTextColor(resources.getColor(R.color.white_1))
            } else {
                btnCardcreateComplete.isEnabled = false
                btnCardcreateComplete.setTextColor(resources.getColor(R.color.white_3))
            }
        }
    }

    companion object {
        const val SYMBOL_0 = 1
        const val SYMBOL_1 = 2
        const val SYMBOL_2 = 3
        const val SYMBOL_3 = 4
        const val SYMBOL_4 = 5
        const val GALLERY = 6
    }



}