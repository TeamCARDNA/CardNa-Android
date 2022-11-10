package org.cardna.presentation.ui.cardpack.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.lifecycle.lifecycleScope
import com.amplitude.api.Amplitude
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.cardna.R
import org.cardna.data.remote.model.card.RequestEditCardData
import org.cardna.databinding.ActivityCardCreateCompleteBinding
import org.cardna.domain.repository.CardRepository
import org.cardna.presentation.MainActivity
import org.cardna.presentation.base.BaseViewUtil
import org.cardna.presentation.ui.alarm.view.AlarmActivity
import org.cardna.presentation.ui.login.view.SetNameFinishedActivity
import org.cardna.presentation.util.LinearGradientSpan
import javax.inject.Inject

@AndroidEntryPoint
class CardCreateCompleteActivity :
    BaseViewUtil.BaseAppCompatActivity<ActivityCardCreateCompleteBinding>(R.layout.activity_card_create_complete) {
    @Inject
    lateinit var cardRepository: CardRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun initView() {
        setCardMeOrYou()
    }

    private fun setCardMeOrYou() {

        val isCardMeOrYou = intent.getBooleanExtra(
            BaseViewUtil.IS_CARD_ME_OR_YOU,
            BaseViewUtil.CARD_ME
        ) // 안넘겨줄 경우, CARDME
        val symbolId = intent.getIntExtra(BaseViewUtil.SYMBOL_ID, -1) // symbolId가 null일 때 -1로
        val cardImg =
            Uri.parse(intent.getStringExtra(BaseViewUtil.CARD_IMG)) // uri를 string으로 변환한 값을 받아 다시 uri로
        val cardTitle = intent.getStringExtra(BaseViewUtil.CARD_TITLE)
        val isFromStore =
            intent.getBooleanExtra(BaseViewUtil.FROM_STORE_KEY, BaseViewUtil.FROM_STORE)
        val isFromAlarm =
            intent.getBooleanExtra(BaseViewUtil.FROM_ALARM_KEY, BaseViewUtil.FROM_ALARM)
        if (isCardMeOrYou == BaseViewUtil.CARD_ME) { // 카드나 작성 완료 하면 만들기
            binding.tvCardcreateComplete.text = getString(R.string.cardcreate_complete_cardme)
            binding.clCardcreateComplete.setBackgroundResource(R.drawable.bg_cardme)

            if (symbolId == -1) { // uri 값의 이미지를 띄워주면 됨.
                Glide.with(this).load(cardImg).into(binding.ivCardcreateComplete)
            } else { // symbolId가 null이 아니라면 이에 해당하는 각 심볼 이미지 띄워주면 됨 이라면,
                when (symbolId) {
                    BaseViewUtil.SYMBOL_0 -> binding.ivCardcreateComplete.setImageResource(R.drawable.ic_symbol_cardme_0)
                    BaseViewUtil.SYMBOL_1 -> binding.ivCardcreateComplete.setImageResource(R.drawable.ic_symbol_cardme_1)
                    BaseViewUtil.SYMBOL_2 -> binding.ivCardcreateComplete.setImageResource(R.drawable.ic_symbol_cardme_2)
                    BaseViewUtil.SYMBOL_3 -> binding.ivCardcreateComplete.setImageResource(R.drawable.ic_symbol_cardme_3)
                    BaseViewUtil.SYMBOL_4 -> binding.ivCardcreateComplete.setImageResource(R.drawable.ic_symbol_cardme_4)
                }
            }
        } else if (isCardMeOrYou == BaseViewUtil.CARD_YOU) { // 카드너 추가 완료 만들기
            if (isFromStore || isFromAlarm) {
                binding.tvCardcreateComplete.text =
                    getString(R.string.cardcreate_complete_cardyou_fromstore)
            } else {
                binding.tvCardcreateComplete.text = getString(R.string.cardcreate_complete_cardyou)
            }
            binding.clCardcreateComplete.setBackgroundResource(R.drawable.bg_cardyou)

            Glide.with(this).load(cardImg).into(binding.ivCardcreateComplete)
        }

        binding.tvCardcreateCompleteTitle.text = cardTitle
        setTextGradient()

        if (intent.getBooleanExtra(SetNameFinishedActivity.GO_TO_CARDCREAT_ACTIVITY_KEY, false))
            setInduceLottie(intent.getIntExtra("INDUCE_CARD_ID", -1))
        else setLottie(isCardMeOrYou, isFromStore, isFromAlarm)

    }

    private fun setLottie(isCardMeOrYou: Boolean, isFromStore: Boolean, isFromAlarm: Boolean) {
        val handler = Handler(Looper.getMainLooper())
        if (isCardMeOrYou == BaseViewUtil.CARD_ME) { // 카드나일 경우, MainActivity 로 돌아가줘야 함
            handler.postDelayed(
                {
                    var intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                },
                LOTTIE_VIEW_TIME
            ) // 이는 CardCreateActivity 가 얼마나 띄워주고 다시 main 으로 갈 건지에 대한 시간, 로티가 뜨는 시간은 아님
        } else if (isCardMeOrYou == BaseViewUtil.CARD_YOU) { // 카드너일 경우, 카드너보관함으로 돌아가줘아 함.
            if (isFromStore)
                handler.postDelayed(
                    {
                        var intent = Intent(this, CardYouStoreActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }, LOTTIE_VIEW_TIME
                )
            else if (isFromAlarm) {
                handler.postDelayed(
                    {
                        var intent = Intent(this, AlarmActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }, LOTTIE_VIEW_TIME
                )
            }
        }
    }

    private fun setInduceLottie(induceCardId: Int) {
        Handler(Looper.getMainLooper()).postDelayed({ setAni() }, LOTTIE_VIEW_TIME)
        setInduceBtnClickListener(induceCardId)
    }

    private fun setAni() {
        setAniTextGradient()
        Handler(Looper.getMainLooper()).postDelayed({ setFadeAnim("background") }, LOTTIE_BG_TIME)
        Handler(Looper.getMainLooper()).postDelayed({ setFadeAnim("text") }, LOTTIE_TEXT_TIME)
        Handler(Looper.getMainLooper()).postDelayed({ setFadeAnim("button") }, LOTTIE_BTN_TIME)
    }

    private fun setFadeAnim(type: String) {
        val fadeIn = AnimationUtils.loadAnimation(this@CardCreateCompleteActivity, R.anim.fade_in)
        val fadeOut =
            AnimationUtils.loadAnimation(this@CardCreateCompleteActivity, R.anim.fade_out)
        when (type) {
            "background" -> {
                with(binding.viewAni) {
                    visibility = View.VISIBLE
                    startAnimation(fadeOut)
                    startAnimation(fadeIn)
                }
            }
            "text" -> {
                with(binding.ctlCardCreateCompleteTextAni) {
                    visibility = View.VISIBLE
                    startAnimation(fadeOut)
                    startAnimation(fadeIn)
                }
            }
            "button" -> {
                with(binding.llSetmakemaincardBtn) {
                    visibility = View.VISIBLE
                    startAnimation(fadeOut)
                    startAnimation(fadeIn)
                }
            }
        }
    }

    private fun setInduceBtnClickListener(induceCardId: Int) {
        binding.btnSetmakemaincardNegative.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
        binding.btnSetmakemaincardPositive.setOnClickListener {
            Amplitude.getInstance().logEvent("Membership_MainCard_Yes")
            lifecycleScope.launch {
                try {
                    val putEditCard =
                        cardRepository.putEditCard(RequestEditCardData(listOf(induceCardId)))
                    if (putEditCard.success) goToMainActivity()
                } catch (e: Exception) {
                    Log.d("실패", e.message.toString())
                }
            }
        }
    }

    fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    fun setTextGradient() { // text gradation
        val text = binding.tvCardcreateComplete.text.toString()
        val green = getColor(R.color.main_green)
        val purple = getColor(R.color.main_purple)
        val spannable = text.toSpannable()
        spannable[0..text.length] = LinearGradientSpan(text, text, green, purple)
        binding.tvCardcreateComplete.text = spannable
    }

    fun setAniTextGradient() { // text gradation
        val tvAni1 = binding.tvAni1.text.toString()
        val green = getColor(R.color.main_green)
        val purple = getColor(R.color.main_purple)
        val spannable = tvAni1.toSpannable()
        spannable[0..tvAni1.length] = LinearGradientSpan(tvAni1, tvAni1, green, purple)
        binding.tvAni1.text = spannable

        val tvAni2 = binding.tvAni2.text.toString()
        val spannable2 = tvAni2.toSpannable()
        spannable2[0..tvAni2.length] = LinearGradientSpan(tvAni2, tvAni2, green, purple)
        binding.tvAni2.text = spannable2
    }

    companion object {
        const val LOTTIE_VIEW_TIME = 1670L
        const val LOTTIE_BG_TIME = 160L
        const val LOTTIE_TEXT_TIME = 1400L
        const val LOTTIE_BTN_TIME = 2400L
    }


}