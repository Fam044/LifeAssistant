package com.imooc.lib_base.helper.func.data

import android.graphics.drawable.Drawable

/**
 * 包名，应用名称，ICON，第一启动类，是否为系统应用
 */
data class AppData(
    val packName: String,
    val appName: String,
    val appIcon: Drawable,
    val firstRunName: String,
    val isSystemApp: Boolean
)