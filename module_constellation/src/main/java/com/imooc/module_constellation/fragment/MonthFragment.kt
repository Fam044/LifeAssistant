package com.imooc.module_constellation.fragment

import android.widget.Toast
import com.imooc.lib_base.base.BaseFragment
import com.imooc.lib_base.utils.L
import com.imooc.lib_network.HttpManager
import com.imooc.lib_network.bean.MonthData
import com.imooc.module_constellation.R
import kotlinx.android.synthetic.main.fragment_month.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonthFragment(val name: String): BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_month
    }

    override fun initView() {
        loadMonthData()
    }

    private fun loadMonthData() {
        HttpManager.queryMonthConsTellInfo(name, object : Callback<MonthData>{
            override fun onResponse(call: Call<MonthData>, response: Response<MonthData>) {
                val data = response.body()
                data?.let{
                    L.i("it:$it")

                    tvName.text = it.name
                    tvDate.text = it.date
                    tvAll.text = it.all
                    tvHappy.text = it.happyMagic
                    tvHealth.text = it.health
                    tvLove.text = it.love
                    tvMoney.text = it.money
                    tvMonth.text = "第${it.month}月"
                    tvWork.text = it.work
                }
            }

            override fun onFailure(call: Call<MonthData>, t: Throwable) {
                Toast.makeText(activity, "加载失败", Toast.LENGTH_SHORT).show()
            }
        })
    }
}