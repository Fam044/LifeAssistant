package com.imooc.aivoiceapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.imooc.aivoiceapp.MainActivity
import com.imooc.aivoiceapp.R

/**
 * FileName: SplashActivity
 * Founder: LiuGuiLin
 * Profile: 启动页
 */
class SplashActivity : AppCompatActivity() {

    //倒计时
    private val mHandler by lazy { Handler() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //状态栏
//        ImmersionBar.with(this).transparentBar().init()

        mHandler.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)
    }
}