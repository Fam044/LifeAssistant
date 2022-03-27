package com.imooc.lib_base.helper.func

import android.content.Context
import com.imooc.lib_base.R

/**
 * Profile: 星座帮助类
 * 根据传递进来的星座名称反馈时间
 */
object ConsTellHelper {

    private lateinit var mNameArray: Array<String>
    private lateinit var mTimeArray: Array<String>

    fun initHelper(mContext: Context){
        mNameArray = mContext.resources.getStringArray(R.array.ConstellArray)
        mTimeArray = mContext.resources.getStringArray(R.array.ConstellTimeArray)
    }

    fun getConsTellTime(consTellName: String): String{
        mNameArray.forEachIndexed { index, s ->
            if (s == consTellName){
                return mTimeArray[index]
            }
        }
        return "查询不到结果"
    }
}