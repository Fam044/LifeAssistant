package com.imooc.module_setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.imooc.lib_base.base.BaseActivity
import com.imooc.lib_base.helper.ARouterHelper

@Route(path = ARouterHelper.PATH_SETTING)
class SettingActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }

    override fun getTitleText(): String {
        return getString(com.imooc.lib_base.R.string.app_title_system_setting)
    }

    override fun initView() {

    }

    override fun isShowBack(): Boolean {
        return true
    }
}