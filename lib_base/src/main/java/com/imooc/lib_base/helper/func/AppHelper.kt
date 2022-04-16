package com.imooc.lib_base.helper.func

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.imooc.lib_base.R
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
    //所有商店包名
    private lateinit var mAllMarketArray: Array<String>

    //分页View
    val mAllViewList = ArrayList<View>()

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
                (resolveInfo.activityInfo.flags and ApplicationInfo.FLAG_SYSTEM) > 0
            )
            mAllAppList.add(appData)
        }

        L.e("mAllAppList$mAllAppList")

        initPageView()
        //加载商店包名
        mAllMarketArray = mContext.resources.getStringArray(R.array.AppMarketArray)
    }

    //初始化PageView
    private fun initPageView() {
        //遍历所有apk对象的数量
        // FrameLayout
        for(i in 0 until getPageSize()){
            val rootView = View.inflate(mContext, R.layout.layout_app_manager_item, null) as ViewGroup
            // 第一层 线性布局
            for (j in 0 until rootView.childCount){
                // 第二层 三个线性布局
                val childX = rootView.getChildAt(j) as ViewGroup
                // 第三层 两个线性布局
                for (k in 0 until childX.childCount){
                    // 第四层 两个View ImageView TextView
                    val child = childX.getChildAt(k) as ViewGroup
                    val iv = child.getChildAt(0) as ImageView
                    val tv = child.getChildAt(1) as TextView
                    //计算App当前下标
                    val index = i * 6 + j * 2 + k
                    if(index < mAllAppList.size){
                        //获取数据
                        val data = mAllAppList[index]
                        tv.text = data.appName
                        iv.setImageDrawable(data.appIcon)
                        //点击事件
                        child.setOnClickListener{
                            intentApp(data.packName)
                        }
                    }
                }
            }
            mAllViewList.add(rootView)
        }
    }

    //获取页面数量
    fun getPageSize(): Int{
        return mAllAppList.size / 6 + 1
    }

    //获取非系统应用
    fun getNotSystemApp(): List<AppData>{
        return mAllAppList.filter { !it.isSystemApp }
    }

    //启动App
    fun launchApp(appName: String): Boolean{
        if (mAllAppList.size > 0){
            mAllAppList.forEach {
                if (it.appName == appName){
                    intentApp(it.packName)
                    return true
                }
            }
        }
        return false
    }

    //卸载App
    fun unInstallApp(appName: String): Boolean{
        if (mAllAppList.size > 0){
            mAllAppList.forEach {
                if (it.appName == appName){
                    intentUnInstallApp(it.packName)
                    return true
                }
            }
        }
        return false
    }

    //跳转应用市场
    fun launchAppStore(appName: String):Boolean{
        mAllAppList.forEach {
            //如果你包含，说明呢安装了应用商店
            if (mAllMarketArray.contains(it.packName)) {
                if (mAllAppList.size > 0) {
                    //遍历所有应用
                    mAllAppList.forEach {data ->
                        // 应用名称 == 打开的应用的名称
                        if (data.appName == appName) {
                            intentAppStore(data.packName, it.packName)
                            return true
                        }
                    }
                }
            }
        }
        return false
    }
//    fun launchAppStore(appName: String){
//        mAllAppList.forEach {
//            //如果你包含，说明呢安装了应用商店
//            if (mAllMarketArray.contains(it.packName)) {
//                if (mAllAppList.size > 0) {
//                    mAllAppList.forEach {data ->
//                            intentAppStore(data.packName, it.packName)
//                    }
//                }
//            }
//        }
//    }
// 在该段注释的代码中，将VoiceService内otherApp方法改为不获取返回值时，调用后却出现应用商城调用失误的情况，即应用商城打开的应用并非用户所需要的应用

    //启动App
    private fun intentApp(packageName: String){
        val intent = pm.getLaunchIntentForPackage(packageName)
        intent?.let{
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(it)
        }
    }

    //启动卸载App
    private fun intentUnInstallApp(packageName: String){
        val uri = Uri.parse("package:$packageName")
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mContext.startActivity(intent)
    }

    //跳转应用商店
    private fun intentAppStore(packageName: String, marketPackageName: String){
        val uri = Uri.parse("market://details?id=$packageName")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage(marketPackageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mContext.startActivity(intent)
    }
}