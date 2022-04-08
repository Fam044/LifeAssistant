package com.imooc.lib_base.base

import android.app.Application
import android.content.Intent
import com.baidu.mapapi.SDKInitializer
import com.imooc.lib_base.helper.ARouterHelper
import com.imooc.lib_base.helper.NotificationHelper
import com.imooc.lib_base.map.MapManager
import com.imooc.lib_base.service.InitService
import com.imooc.lib_base.utils.SpUtils
import com.imooc.lib_voice.manager.VoiceManager

/**
 * Filename: BaseApp
 * Profile: 基类APP
 */
open class BaseApp : Application(){

    override fun onCreate() {
        super.onCreate()


        ARouterHelper.initHelper(this)
        NotificationHelper.initHelper(this)
        MapManager.initMap(this)
        startService(Intent(this, InitService::class.java))
    }
}