package com.imooc.lib_voice.impl

/**
 * Profile: 语义结果
 */
interface OnNluResultListener {

    //==============应用管理============
    //打开App
    fun openApp(appName: String)

    //卸载App
    fun unInstallApp(appName: String)

    //其他App
    fun otherApp(appName: String)

    //==============通用设置============

    //返回
    fun back()

    //主页
    fun home()

    //音量+
    fun setVolumeUp()

    //音量-
    fun setVolumeDown()

    //==============通用设置==============

    //拨打联系人
    fun callPhoneForName(name: String)

    //拨打电话号码
    fun callPhoneForNumber(phone: String)

    //==============笑话==============

    //播放笑话
    fun playJoke()

    //笑话列表
    fun jokeList()

    //==================星座====================
    //星座时间
    fun conTellTime(name: String)

    //星座详情
    fun conTellInfo(name: String)

    //查询天气
    fun queryWeather()

    //识别语义失败
    fun nluError()
}