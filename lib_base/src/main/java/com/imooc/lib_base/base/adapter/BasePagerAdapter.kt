package com.imooc.lib_base.base.adapter

import android.view.View
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class BasePagerAdapter(private val mList: List<View>): PagerAdapter() {
    override fun getCount(): Int {
        return mList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: View, position: Int): Any {
        (container as ViewPager).addView(mList[position])
        return mList[position]
    }

    override fun destroyItem(container: View, position: Int, `object`: Any) {
        (container as ViewPager).removeView(mList[position])
    }
}