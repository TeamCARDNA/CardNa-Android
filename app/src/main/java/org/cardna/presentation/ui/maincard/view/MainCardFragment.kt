package org.cardna.presentation.ui.maincard.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.cardna.R
import com.example.cardna.databinding.FragmentMainCardBinding
import dagger.hilt.android.AndroidEntryPoint
import org.cardna.data.remote.model.card.ResponseMainCardData
import org.cardna.presentation.base.BaseViewUtil
import org.cardna.presentation.ui.alarm.view.AlarmActivity
import org.cardna.presentation.ui.maincard.adapter.MainCardAdapter
import org.cardna.presentation.ui.maincard.viewmodel.MainCardViewModel
import timber.log.Timber
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainCardFragment :
    BaseViewUtil.BaseFragment<FragmentMainCardBinding>(R.layout.fragment_main_card) {
    private lateinit var mainCardAdapter: MainCardAdapter
    private val mainCardViewModel: MainCardViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun initView() {
        initData()
        initAdapter()
        setClickListener()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    //onResume 에 뿌려질 데이터
    private fun initData() {
        Timber.d("init data")
        binding.mainCardViewModel = mainCardViewModel
        mainCardViewModel.getMainCardList()
        mainCardViewModel.getMyPageUser()
    }

    //adapter 관련 모음
    private fun initAdapter() {
        Timber.d("init adapter")
        mainCardAdapter = MainCardAdapter()
        mainCardViewModel.cardList.observe(viewLifecycleOwner) {
            mainCardAdapter.submitList(it)
        }
        with(binding.vpMaincardList) {
            adapter = mainCardAdapter
            viewPagerAnimation()
        }
    }

    private fun viewPagerAnimation() {
        val compositePageTransformer = getPageTransformer()
        with(binding.vpMaincardList) {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 1
            setPageTransformer(compositePageTransformer)
            setPadding(
                (56 * resources.displayMetrics.density).roundToInt(),
                0,
                (56 * resources.displayMetrics.density).roundToInt(),
                0
            )
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
    }

    private fun getPageTransformer(): ViewPager2.PageTransformer {
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer((20 * resources.displayMetrics.density).roundToInt()))

        return compositePageTransformer
    }

    //click listener
    private fun setClickListener() {
        setEditCardActivity()
        setAlarmActivity()
    }

    private fun setEditCardActivity() {
        binding.llMaincardEditLayout.setOnClickListener {
//            val intent = Intent(requireActivity(), EditCardActivity::class.java)
//            startActivity(intent)
        }
    }

    private fun setAlarmActivity() {
        binding.ibtnMaincardAlarm.setOnClickListener {
            val intent = Intent(requireActivity(), AlarmActivity::class.java)
            startActivity(intent)
        }
    }
}
