package com.imooc.lib_base.helper

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager

/**
 * 窗口帮助类
 */
object WindowHelper {
    private lateinit var mContext: Context
    private lateinit var wm: WindowManager
    private lateinit var lp: WindowManager.LayoutParams

    fun initHelper(mContext: Context){
        this.mContext = mContext

        wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        lp = createLayoutParams()
    }

    /**
     * 创建布局属性
     * height: 窗口的高
     * width: 窗口的宽
     */
    fun createLayoutParams(): WindowManager.LayoutParams{
        val lp = WindowManager.LayoutParams()
        lp.apply {
            this.width = WindowManager.LayoutParams.MATCH_PARENT
            this.height = WindowManager.LayoutParams.MATCH_PARENT
            gravity = Gravity.CENTER
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            }else{
                WindowManager.LayoutParams.TYPE_PHONE
            }
        }
        return lp
    }

    //获取试图控件
    fun getView(layoutId: Int): View {
        return View.inflate(mContext, layoutId, null)
    }

    //显示窗口
    fun show(view: View){
        if (view.parent == null){
            wm.addView(view, lp)
        }
    }

    //显示窗口，但是窗口属性自定义
    fun show(view: View, lp: WindowManager.LayoutParams){
        if (view.parent == null){
            wm.addView(view, lp)
        }
    }

    //隐藏试图
    fun hide(view: View){
        if (view.parent != null){
            wm.removeView(view)
        }
    }

    //更新试图
    fun update(view: View, lp: WindowManager.LayoutParams){
        wm.updateViewLayout(view, lp)
    }
}