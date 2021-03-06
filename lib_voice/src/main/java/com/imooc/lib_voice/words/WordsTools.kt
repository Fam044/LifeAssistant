package com.imooc.lib_voice.words

import android.content.Context
import com.imooc.lib_voice.R
import kotlin.random.Random

object WordsTools {
    //唤醒词条
    private lateinit var wakeUpArray: Array<String>
    //无法应答
    private lateinit var noAnswerArray: Array<String>
    //暂不支持功能
    private lateinit var noSupportArray: Array<String>
    //退出语音界面
    private lateinit var quitArray: Array<String>

    fun initTools(mContext: Context){
        mContext.apply {
            wakeUpArray = resources.getStringArray(R.array.WakeUpListArray)
            noAnswerArray = resources.getStringArray(R.array.NoAnswerArray)
            noSupportArray = resources.getStringArray(R.array.NoSupportArray)
        }
    }

    //唤醒
    fun wakeUpWords(): String{
        return randomArray(wakeUpArray)
    }
    //无法回答
    fun noAnswerWords(): String{
        return randomArray(noAnswerArray)
    }
    //暂不支持
    fun noSupportWords(): String{
        return randomArray(noSupportArray)
    }

    //退出语音界面
    fun quitWords(): String{
        return randomArray(quitArray)
    }

    private fun randomArray(array: Array<String>): String {
        return array[Random.nextInt(array.size)]
    }

    fun randomInt(maxSize: Int): Int{
        return Random.nextInt(maxSize)
    }
}