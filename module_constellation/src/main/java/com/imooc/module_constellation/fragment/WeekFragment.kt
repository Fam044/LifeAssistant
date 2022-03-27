package com.imooc.module_constellation.fragment

import android.widget.Toast
import com.imooc.lib_base.base.BaseFragment
import com.imooc.lib_base.utils.L
import com.imooc.lib_network.HttpManager
import com.imooc.lib_network.bean.WeekData
import com.imooc.module_constellation.R
import kotlinx.android.synthetic.main.fragment_week.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeekFragment(val name: String): BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_week
    }

    override fun initView() {
        loadWeekData()
    }

    private fun loadWeekData() {
        HttpManager.queryWeekConsTellInfo(name, object : Callback<WeekData>{
            override fun onResponse(call: Call<WeekData>, response: Response<WeekData>) {
                val data = response.body()
                data?.let {
                    L.i("it:$it")
                    tvName.text = it.name
                    tvData.text = it.date
                    tvWeekth.text = "第${it.weekth}周"
                    tvHealth.text = it.health
                    tvJob.text = it.health
                    tvLove.text = it.love
                    tvMoney.text = it.money
                    tvWork.text = it.work
                }
            }

            override fun onFailure(call: Call<WeekData>, t: Throwable) {
                Toast.makeText(activity, "加载失败", Toast.LENGTH_SHORT).show()
            }
        })
    }
}