package com.imooc.module_constellation

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.imooc.lib_base.base.BaseActivity
import com.imooc.lib_base.helper.ARouterHelper
import com.imooc.module_constellation.fragment.MonthFragment
import com.imooc.module_constellation.fragment.TodayFragment
import com.imooc.module_constellation.fragment.WeekFragment
import com.imooc.module_constellation.fragment.YearFragment
import kotlinx.android.synthetic.main.activity_constellation.*

@Route(path = ARouterHelper.PATH_CONSTELLATION)
class ConstellationActivity : BaseActivity() {

    private lateinit var mTodayFragment: TodayFragment
    private lateinit var mTomorrowFragment: TodayFragment
    private lateinit var mWeekFragment: WeekFragment
    private lateinit var mMonthFragment: MonthFragment
    private lateinit var mYearFragment: YearFragment

    private val mListFragment = ArrayList<Fragment>()

    override fun getLayoutId(): Int {
        return R.layout.activity_constellation
    }

    override fun getTitleText(): String {
        return getString(com.imooc.lib_base.R.string.app_title_constellation)
    }

    override fun initView() {
        val name = intent.getStringExtra("name")
        if (!TextUtils.isEmpty(name)){
            //语音进来的
            initFragment(name)
        }else{
            //主页进来的
            initFragment("射手座")
        }

        //View控制
        mTvToday.setOnClickListener {
            checkTab(true, 0)
        }
        mTvTomorrow.setOnClickListener {
            checkTab(true, 1)
        }
        mTvWeek.setOnClickListener {
            checkTab(true, 2)
        }
        mTvMonth.setOnClickListener {
            checkTab(true, 3)
        }
        mTvYear.setOnClickListener {
            checkTab(true, 4)
        }
    }

    override fun isShowBack(): Boolean {
        return true
    }

    //ViewPage + Fragment 实现滑动切换页面
    private fun initFragment(name: String){

        //设置标题
        supportActionBar?.title = name

        mTodayFragment = TodayFragment(true, name)
        mTomorrowFragment = TodayFragment(false, name)
        mWeekFragment = WeekFragment(name)
        mMonthFragment = MonthFragment(name)
        mYearFragment = YearFragment(name)

        mListFragment.add(mTodayFragment)
        mListFragment.add(mTomorrowFragment)
        mListFragment.add(mWeekFragment)
        mListFragment.add(mMonthFragment)
        mListFragment.add(mYearFragment)

        //初始化页面
        initViewPager()

    }

    private fun initViewPager(){
        mViewPager.adapter = PageFragmentAdapter(supportFragmentManager)
        mViewPager.offscreenPageLimit = mListFragment.size
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                checkTab(false, position)
            }

            override fun onPageScrollStateChanged(state: Int) {            }
        })

        //等待全部初始化之后才去做UI控制操作
        checkTab(false,0)
    }

    //适配器
    inner class PageFragmentAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm){
        override fun getCount(): Int {
            return mListFragment.size
        }

        override fun getItem(position: Int): Fragment {
            return mListFragment[position]
        }
    }

    //切换选项卡
    private fun checkTab(isClick: Boolean, index: Int){
        if (isClick){
            mViewPager.currentItem = index
        }

        mTvToday.setTextColor(if (index == 0) Color.RED else Color.BLACK)
        mTvTomorrow.setTextColor(if (index == 1) Color.RED else Color.BLACK)
        mTvWeek.setTextColor(if (index == 2) Color.RED else Color.BLACK)
        mTvMonth.setTextColor(if (index == 3) Color.RED else Color.BLACK)
        mTvYear.setTextColor(if (index == 4) Color.RED else Color.BLACK)
    }

}