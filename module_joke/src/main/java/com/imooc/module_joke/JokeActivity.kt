package com.imooc.module_joke

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.imooc.lib_base.base.BaseActivity
import com.imooc.lib_base.helper.ARouterHelper

@Route(path = ARouterHelper.PATH_JOKE)
class JokeActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_joke
    }

    override fun getTitleText(): String {
        return getString(com.imooc.lib_base.R.string.app_title_joke)
    }

    override fun initView() {

    }

    override fun isShowBack(): Boolean {
        return true
    }
}