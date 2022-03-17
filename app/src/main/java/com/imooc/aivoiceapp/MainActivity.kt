package com.imooc.aivoiceapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.imooc.lib_base.event.EventManager
import com.imooc.lib_base.event.MessageEvent
import com.imooc.lib_base.helper.ARouterHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn1.setOnClickListener {
            ARouterHelper.startActivity(ARouterHelper.PATH_APP_MANAGER)
        }
        btn2.setOnClickListener {
            ARouterHelper.startActivity(ARouterHelper.PATH_MAP)
        }

//        EventManager.register(this)
//
//        btn1.setOnClickListener {
//            EventManager.post(1111)
//        }
//        btn2.setOnClickListener {
//            EventManager.post(2222,"Hello EventBus")
//        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        EventManager.unRegister(this)
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMessageEvent(event: MessageEvent){
//        when(event.type){
//            1111 -> Log.i("TestApp", "1111")
//            2222 -> Log.i("TestApp", event.stringValue)
//        }
//    }
}