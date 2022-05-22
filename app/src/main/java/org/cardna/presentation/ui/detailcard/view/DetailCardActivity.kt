package org.cardna.presentation.ui.detailcard.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import land.sungbin.systemuicontroller.setSystemBarsColor
import org.cardna.R
import org.cardna.databinding.ActivityDetailCardBinding
import org.cardna.presentation.base.BaseViewUtil
import org.cardna.presentation.ui.cardpack.view.CardCreateCompleteActivity
import org.cardna.presentation.ui.cardpack.view.FriendCardPackActivity
import org.cardna.presentation.ui.detailcard.viewmodel.DetailCardViewModel
import org.cardna.presentation.util.*

@AndroidEntryPoint
class DetailCardActivity : BaseViewUtil.BaseAppCompatActivity<ActivityDetailCardBinding>(R.layout.activity_detail_card) {
    private val detailCardViewModel: DetailCardViewModel by viewModels()
    var cardType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.detailCardViewModel = detailCardViewModel
        binding.detailCardActivity = this
        initView()
    }

    override fun initView() {
        this.setStatusBarTransparent()
        this.setSystemBarsColor(Color.TRANSPARENT, false)
        initData()
        setObserve()
    }

    private fun initData() {
        val id = intent.getIntExtra(BaseViewUtil.CARD_ID, 0)
        detailCardViewModel.setCardId(id)
    }

    @SuppressLint("ResourceType")
    private fun setObserve() {
        detailCardViewModel.detailCard.observe(this) { detailCard ->
            cardType = detailCard.type
            setSrcWithGlide(detailCard.cardImg, binding.ivDetailcardImage)

            with(binding) {
                when (cardType) {
                    CARD_ME -> {
                        ctlDetailcardFriend.visibility = View.GONE
                        tvDetailcardTitle.setBackgroundResource(R.drawable.bg_maingreen_stroke_real_black_2dp)
                        ibtnDetailcardEdit.setImageResource(R.drawable.ic_detail_card_me_trash)
                        ibtnDetailcardEdit.setOnClickListener {
                            showEditDialog()
                        }
                    }
                    CARD_YOU -> {
                        tvDetailcardTitle.setBackgroundResource(R.drawable.bg_mainpurple_stroke_real_black_2dp)
                        ibtnDetailcardEdit.setOnClickListener {
                            showEditPopUp()
                        }
                    }
                    STORAGE -> {
                        tvDetailcardTitle.setBackgroundResource(R.drawable.bg_white_1_5_stroke_real_black_2dp)
                        ibtnDetailcardEdit.setOnClickListener {
                            showEditPopUp()
                        }
                    }
                }
            }
        }

        detailCardViewModel.isMineCard.observe(this) { isMineCard ->
            with(binding.ctvDetailcardLike) {
                if (isMineCard) {
                    isChecked = true
                    isClickable = false
                }
            }
        }
    }

    private fun showEditPopUp() {
        with(binding.ibtnDetailcardEdit) {
            when (cardType) {
                CARD_YOU -> {
                    val popup = showCustomPopUp(this, R.array.detail_cardyou_popup, baseContext)
                    popup.setOnItemClickListener { _, view, _, _ ->
                        if ((view as TextView).text == "보관") {
                            detailCardViewModel.keepOrAddCard()
                            popup.dismiss()
                            finish()
                        } else {
                            detailCardViewModel.deleteCard()
                            popup.dismiss()
                            finish()
                        }
                    }
                    popup.show()
                }
                STORAGE -> {
                    val popup = showCustomPopUp(this, R.array.detail_storage_popup, baseContext)
                    popup.setOnItemClickListener { _, view, _, _ ->
                        if ((view as TextView).text == "신고") {
                            showUerReportDialog()
                            popup.dismiss()
                        } else {
                            detailCardViewModel.deleteCard()
                            popup.dismiss()
                            finish()
                        }
                    }
                    popup.show()
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun showEditDialog() {
        val dialog = showCustomDialog(R.layout.dialog_detail_cardme)
        val deleteBtn = dialog.findViewById<Button>(R.id.tv_dialog_delete)
        val noBtn = dialog.findViewById<Button>(R.id.tv_dialog_cardme_no)
        noBtn.setOnClickListener {
            setHandler(dialog)
        }
        deleteBtn.setOnClickListener {
            detailCardViewModel.deleteCard()
            dialog.dismiss()
            finish()
        }
    }

    @SuppressLint("ResourceType")
    fun showUerReportDialog() {
        val dialog = this.showCustomDialog(R.layout.dialog_user_report)
        val reasonOneBtn = dialog.findViewById<Button>(R.id.tv_dialog_report_reason_one)
        val reasonTwoBtn = dialog.findViewById<Button>(R.id.tv_dialog_report_reason_two)
        val reasonThreeBtn = dialog.findViewById<Button>(R.id.tv_dialog_report_reason_three)
        val reasonFourBtn = dialog.findViewById<Button>(R.id.tv_dialog_report_reason_four)
        val cancelBtn = dialog.findViewById<Button>(R.id.tv_dialog_report_cancel)

        reasonOneBtn.setOnClickListener {
            detailCardViewModel.reportUser(REPORT_REASON_ONE)
            setHandler(dialog)
        }

        reasonTwoBtn.setOnClickListener {
            detailCardViewModel.reportUser(REPORT_REASON_TWO)
            setHandler(dialog)
        }

        reasonThreeBtn.setOnClickListener {
            detailCardViewModel.reportUser(REPORT_REASON_THREE)
            setHandler(dialog)
        }

        reasonFourBtn.setOnClickListener {
            detailCardViewModel.reportUser(REPORT_REASON_FOUR)
            setHandler(dialog)
        }
        cancelBtn.setOnClickListener {
            setHandler(dialog)
        }
    }

    fun setCardShareClickListener() {
        val intent = Intent(this@DetailCardActivity, CardShareActivity::class.java)
        intent.putExtra(BaseViewUtil.CARD_IMG, detailCardViewModel.cardImg.value) // 카드 이미지 uri
        intent.putExtra(BaseViewUtil.CARD_TITLE, detailCardViewModel.title.value) // 카드 title
        intent.putExtra(BaseViewUtil.IS_CARD_ME_OR_YOU, detailCardViewModel.type.value) // 카드나 인지 카드너 인지
        startActivity(intent)
    }

    fun setLikeClickListener() {
        with(binding) {
            ctvDetailcardLike.toggle()
            detailCardViewModel?.postLike() ?: return

            if (ctvDetailcardLike.isChecked) {
                showLikeLottie()
                tvDetailcardLikecount.text = (++detailCardViewModel!!.currentLikeCount).toString()
            } else {
                tvDetailcardLikecount.text = (--detailCardViewModel!!.currentLikeCount).toString()
            }
        }
    }

    fun setCardAddClickListener() {
        detailCardViewModel.keepOrAddCard()
/*        val isCardMeOrYou = intent.getBooleanExtra(BaseViewUtil.IS_CARD_ME_OR_YOU, BaseViewUtil.CARD_ME) // 안넘겨줄 경우, CARDME
        val symbolId = intent.getIntExtra(BaseViewUtil.SYMBOL_ID, -1) // symbolId가 null일 때 -1로
        val cardImg = Uri.parse(intent.getStringExtra(BaseViewUtil.CARD_IMG)) // uri를 string으로 변환한 값을 받아 다시 uri로
        val cardTitle = intent.getStringExtra(BaseViewUtil.CARD_TITLE)*/
        /*     startActivity(
                 Intent(this, FriendCardPackActivity::class.java)
                     .putExtra(BaseViewUtil.ID, mainCardViewModel.friendId.value)
                     .putExtra(BaseViewUtil.NAME, mainCardViewModel.friendName.value)
             )*/
        val intent = Intent(this@DetailCardActivity, CardCreateCompleteActivity::class.java)
            .putExtra(
                BaseViewUtil.IS_CARD_ME_OR_YOU, //카드너
                false
            ) // 현재는 카드나 작성이므로 CARD_ME를 보내줌
            /*   .putExtra(
                   BaseViewUtil.SYMBOL_ID,  //심볼
                   null
               ) */// 심볼 - symbolId값, 갤러리 - null
            .putExtra(
                BaseViewUtil.CARD_IMG, //이미지
                detailCardViewModel.cardImg.value
            ) // 심볼 - null, 갤러리 - uri 값
            .putExtra(BaseViewUtil.CARD_TITLE, detailCardViewModel.title.value)  //카드타이틀
            .putExtra(BaseViewUtil.FROM_STORE_KEY, true)  //카드타이틀
        startActivity(intent)



        finish()
    }

    private fun showLikeLottie() {
        with(binding) {
            when (cardType) {
                CARD_ME -> showLottie(laDetailcardLottie, CARD_ME, "lottie_cardme.json")
                CARD_YOU -> showLottie(laDetailcardLottie, CARD_YOU, "lottie_cardyou.json")
            }
        }
    }

    private fun goPreviousActivityWithHandling() {
        Handler(Looper.getMainLooper())
            .postDelayed({
                finish()
            }, 2000)
    }

    companion object {
        const val CARD_ME = "me"
        const val CARD_YOU = "you"
        const val STORAGE = "storage"
        const val REPORT_REASON_ONE = "욕설비하"
        const val REPORT_REASON_TWO = "허위"
        const val REPORT_REASON_THREE = "부적절"
        const val REPORT_REASON_FOUR = "성적"
    }
}
