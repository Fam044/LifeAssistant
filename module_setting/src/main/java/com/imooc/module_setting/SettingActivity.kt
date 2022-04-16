package com.imooc.module_setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.text.style.UnderlineSpan
import com.alibaba.android.arouter.facade.annotation.Route
import com.imooc.lib_base.base.BaseActivity
import com.imooc.lib_base.helper.ARouterHelper
import com.imooc.lib_base.utils.SpUtils
import kotlinx.android.synthetic.main.activity_setting.*

@Route(path = ARouterHelper.PATH_SETTING)
class SettingActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }

    override fun getTitleText(): String {
        return getString(com.imooc.lib_base.R.string.app_title_system_setting)
    }

    override fun initView() {
        //欢迎词
        val isHello = SpUtils.getBoolean("isHello", true)
        mSwHello.isChecked = isHello

        mSwHello.setOnClickListener {
            !mSwHello.isChecked
            SpUtils.putBoolean("isHello", mSwHello.isChecked)
        }

        //版本
        val version = packageManager.getPackageInfo(packageName, 0).versionName
        tvVersion.text = getString(R.string.text_version_ui, version)

        //去除下划线
        if (tvClassLink.text is Spannable) {
            val spannable = tvClassLink.text as Spannable
            spannable.setSpan(NoUnderlineSpan(), 0, spannable.length, Spanned.SPAN_MARK_MARK)
        }
    }

    class NoUnderlineSpan : UnderlineSpan() {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = ds.linkColor
            ds.isUnderlineText = false
        }
    }

    override fun isShowBack(): Boolean {
        return true
    }
}