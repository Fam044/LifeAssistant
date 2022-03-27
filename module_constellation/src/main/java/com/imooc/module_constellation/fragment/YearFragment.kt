package com.imooc.module_constellation.fragment

import com.imooc.lib_base.base.BaseFragment
import com.imooc.module_constellation.R

class YearFragment(val name: String): BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_year
    }

    override fun initView() {
        loadYearData()
    }
}