package com.imooc.lib_base.service

import android.app.IntentService
import android.content.Intent
import com.imooc.lib_base.helper.func.CommonSettingHelper
import com.imooc.lib_base.helper.SoundPoolHelper
import com.imooc.lib_base.helper.func.AppHelper
import com.imooc.lib_base.helper.func.ConsTellHelper
import com.imooc.lib_base.utils.L
import com.imooc.lib_base.utils.SpUtils
import com.imooc.lib_voice.words.WordsTools

/**
 * Profile: InitService
 */
class InitService : IntentService(InitService::class.simpleName){
    override fun onCreate() {
        super.onCreate()
        L.i("初始化开始")
    }

    override fun onHandleIntent(intent: Intent?) {
        L.i("执行初始化操作")

        SpUtils.initUtils(this)
        WordsTools.initTools(this)
        SoundPoolHelper.init(this)

        AppHelper.initHelper(this)
        CommonSettingHelper.initHelper(this)
        ConsTellHelper.initHelper(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        L.i("初始化完成")
    }
}