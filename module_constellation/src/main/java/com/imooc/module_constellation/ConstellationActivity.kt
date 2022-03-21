package com.imooc.module_constellation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.imooc.lib_base.base.BaseActivity
import com.imooc.lib_base.helper.ARouterHelper

@Route(path = ARouterHelper.PATH_CONSTELLATION)
class ConstellationActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_constellation
    }

    override fun getTitleText(): String {
        return getString(com.imooc.lib_base.R.string.app_title_constellation)
    }

    override fun initView() {

    }

    override fun isShowBack(): Boolean {
        return true
    }

}