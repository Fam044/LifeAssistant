package com.imooc.module_constellation.fragment

import android.widget.Toast
import com.imooc.lib_base.base.BaseFragment
import com.imooc.lib_base.utils.L
import com.imooc.lib_network.HttpManager
import com.imooc.lib_network.bean.TodayData
import com.imooc.module_constellation.R
import kotlinx.android.synthetic.main.fragment_today.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodayFragment(private val isToday: Boolean, val name: String): BaseFragment(), Callback<TodayData> {
    override fun getLayoutId(): Int {
        return R.layout.fragment_today
    }

    override fun initView() {
        if (isToday){
            loadToday()
        }else{
            loadTomorrow()
        }
    }

    //加载今天的数据
    private fun loadToday() {
        HttpManager.queryTodayConsTellInfo(name, this)
    }

    //加载明天的数据
    private fun loadTomorrow() {
        HttpManager.queryTomorrowConsTellInfo(name, this)
    }

    override fun onResponse(call: Call<TodayData>, response: Response<TodayData>) {
        val data = response.body()
        data?.let {
            L.i("it:$it")

            tvName.text = "星座名称：${it.name}"
            tvTime.text = "当前时间：${it.datetime}"
            tvNumber.text = "幸运数字： ${it.number}"
            tvFriend.text = "速配星座：${it.QFriend}"
            tvColor.text = "幸运颜色：${it.color}"
            tvSummary.text = "今日概述：${it.summary}"

            pbAll.progress = it.all.substring(0, it.all.length - 1).toInt()
            pbHealth.progress = it.health.substring(0, it.health.length - 1).toInt()
            pbLove.progress = it.love.substring(0, it.love.length - 1).toInt()
            pbMoney.progress = it.money.substring(0, it.money.length - 1).toInt()
            pbWork.progress = it.work.substring(0, it.work.length - 1).toInt()
        }
    }

    override fun onFailure(call: Call<TodayData>, t: Throwable) {
        Toast.makeText(activity, "加载失败", Toast.LENGTH_SHORT).show()
    }
}