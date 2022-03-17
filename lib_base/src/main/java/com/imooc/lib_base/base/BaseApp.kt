package com.imooc.lib_base.base

import android.app.Application
import com.imooc.lib_base.helper.ARouterHelper

/**
 * Filename: BaseApp
 * Profile: 基类APP
 */
class BaseApp : Application(){

    override fun onCreate() {
        super.onCreate()

        ARouterHelper.initHelper(this)
    }
}