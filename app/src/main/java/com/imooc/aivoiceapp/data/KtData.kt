package com.imooc.aivoiceapp.data

/**
 * Profile: Kotlin Data 数据
 */
//data class MainListData( val title: String,  val icon: Int,  val color: Int)
data class MainListData(val title: String, val desc: String, val icon: Int, val color: Int)

/**
 * 对话文本
 */
data class ChatList(val type: Int){
    lateinit var text: String
    //天气
    lateinit var wid: String
    lateinit var info: String
    lateinit var city: String
    lateinit var temperature: String
    lateinit var air: String
}