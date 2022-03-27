package com.imooc.lib_base.helper.func

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.view.KeyEvent

/**
 * 通用设置帮助类
 */
object CommonSettingHelper {
    private lateinit var mContext: Context

    private lateinit var inst: Instrumentation

    fun initHelper(mContext: Context){
        CommonSettingHelper.mContext = mContext

        inst = Instrumentation()
    }

    fun back(){
        Thread(Runnable { inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK) }).start()
    }

    fun home(){
//        Thread(Runnable { inst.sendKeyDownUpSync(KeyEvent.KEYCODE_HOME) }).start()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        mContext.startActivity(intent)
    }

    fun setVolumeUp(){
        Thread(Runnable { inst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_UP) }).start()
    }

    fun setVolumeDown(){
        Thread(Runnable { inst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_DOWN) }).start()
    }
}