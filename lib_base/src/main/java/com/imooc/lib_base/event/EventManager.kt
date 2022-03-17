package com.imooc.lib_base.event

import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * FileName: EventManager
 * Profile: EventBus管理
 */
object EventManager {

    //注册
    fun register(subscriber: Objects){
        EventBus.getDefault().register(subscriber)
    }

    //解绑
    fun unRegister(subscriber: Objects){
        EventBus.getDefault().unregister(subscriber)
    }

    //发送事件类
    private fun post(event: MessageEvent){
        EventBus.getDefault().post(event)
    }

    //--------------------Public Fun-----------------

    //发送类型
    fun post(type: Int){
        post(MessageEvent(type))
    }

    //发送类型 携带String
    fun post(type: Int, string: String){
        val event = MessageEvent(type)
        event.stringValue = string
        post(event)
    }

    //发送类型 携带Int
    fun post(type: Int, int: Int){
        val event = MessageEvent(type)
        event.intValue = int
        post(event)
    }

    //发送类型 携带Boolean
    fun post(type: Int, boolean: Boolean){
        val event = MessageEvent(type)
        event.booleanValue = boolean
        post(event)
    }
}