package com.imooc.lib_base.helper.func

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.imooc.lib_base.helper.func.data.AppData
import com.imooc.lib_base.utils.L


/**
 * 应用帮助类
 */
object AppHelper {
    //上下文
    private lateinit var mContext: Context
    //包管理器
    private lateinit var pm: PackageManager
    //所有应用
    private val mAllAppList = ArrayList<AppData>()

    //初始化
    fun initHelper(mContext: Context){
        this.mContext = mContext

        pm = mContext.packageManager

        loadAllApp()
    }

    //加载所有的App
    private fun loadAllApp(){
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val appInfo = pm.queryIntentActivities(intent, 0)

        appInfo.forEachIndexed { _,resolveInfo ->
            val appData = AppData(
                resolveInfo.activityInfo.packageName,
                resolveInfo.loadLabel(pm) as String,
                resolveInfo.loadIcon(pm),
                resolveInfo.activityInfo.name,
                resolveInfo.activityInfo.flags == ApplicationInfo.FLAG_SYSTEM
            )
            mAllAppList.add(appData)
        }
        L.e("mAllAppList$mAllAppList")
    }
}