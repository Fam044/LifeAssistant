package com.imooc.lib_base.base

import android.app.Application
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import com.baidu.mapapi.SDKInitializer
import com.imooc.lib_base.helper.ARouterHelper
import com.imooc.lib_base.helper.NotificationHelper
import com.imooc.lib_base.map.MapManager
import com.imooc.lib_base.service.InitService
import com.imooc.lib_base.utils.CommonUtils
import com.imooc.lib_base.utils.SpUtils
import com.imooc.lib_voice.manager.VoiceManager

/**
 * Filename: BaseApp
 * Profile: 基类APP
 */
open class BaseApp : Application(){

    override fun onCreate() {
        super.onCreate()

        //只有主进程才能初始化
        val processName = CommonUtils.getProcessName(this)
        if (!TextUtils.isEmpty(processName)){
            if (processName == packageName){
                initApp()
            }
        }
    }

    private fun initApp(){
        ARouterHelper.initHelper(this)
        NotificationHelper.initHelper(this)
        MapManager.initMap(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, InitService::class.java))
        } else {
            startService(Intent(this, InitService::class.java))
        }
    }
}