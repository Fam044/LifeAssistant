package com.imooc.lib_network

import com.imooc.lib_network.bean.*
import com.imooc.lib_network.http.HttpKey
import com.imooc.lib_network.http.HttpUrl
import com.imooc.lib_network.impl.HttpImplService
import com.imooc.lib_network.interceptor.HttpInterceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Profile: 对外的网络管理类
 */
object HttpManager {

    const val PAGE_SIZE = 20

    private fun getClient():OkHttpClient{
        return OkHttpClient.Builder().addInterceptor(HttpInterceptor()).build()
    }

    //=========================笑话================================

    //笑话对象
    private val retrofitJoke by lazy {
        Retrofit.Builder()
            .client(getClient())
            .baseUrl(HttpUrl.JOKE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //笑话接口对象
    private val apiJoke by lazy {
        retrofitJoke.create(HttpImplService::class.java)
    }

    //查询笑话
    fun queryJoke(callback: Callback<JokeOneData>){
        apiJoke.queryJoke(HttpKey.JOKE_KEY).enqueue(callback)
    }

    //查询笑话列表
    fun queryJokeList(page: Int, callback: Callback<JokeListData>){
        apiJoke.queryJokeList(HttpKey.JOKE_KEY, page, PAGE_SIZE).enqueue(callback)
    }

    //=========================星座================================

    //星座对象
    private val retrofitConsTell by lazy {
        Retrofit.Builder()
            .client(getClient())
            .baseUrl(HttpUrl.CONS_TELL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //星座接口对象
    private val apiConsTell by lazy {
        retrofitConsTell.create(HttpImplService::class.java)
    }

    //查询今天星座
    fun queryTodayConsTellInfo(name: String, callback: Callback<TodayData>){
        apiConsTell.queryTodayConsTellInfo(name, "today", HttpKey.CONS_TELL_KEY).enqueue(callback)
    }

    //查询明天星座
    fun queryTomorrowConsTellInfo(name: String, callback: Callback<TodayData>){
        apiConsTell.queryTodayConsTellInfo(name, "tomorrow", HttpKey.CONS_TELL_KEY).enqueue(callback)
    }

    //查询本周星座
    fun queryWeekConsTellInfo(name: String, callback: Callback<WeekData>){
        apiConsTell.queryWeekConsTellInfo(name, "week", HttpKey.CONS_TELL_KEY).enqueue(callback)
    }

    //查询本月星座
    fun queryMonthConsTellInfo(name: String, callback: Callback<MonthData>){
        apiConsTell.queryMonthConsTellInfo(name, "month", HttpKey.CONS_TELL_KEY).enqueue(callback)
    }

    //查询本年星座
    fun queryYearConsTellInfo(name: String, callback: Callback<YearData>){
        apiConsTell.queryYearConsTellInfo(name, "year", HttpKey.CONS_TELL_KEY).enqueue(callback)
    }


    //=========================天气================================

    //天气对象
    private val retrofitWeather by lazy {
        Retrofit.Builder()
            .client(getClient())
            .baseUrl(HttpUrl.WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //天气接口对象
    private val apiWeather by lazy{
        retrofitWeather.create(HttpImplService::class.java)
    }

    //查询天气
    fun queryWeather(city: String): Call<ResponseBody> {
        return apiWeather.getWeather(city, HttpKey.WEATHER_KEY)
    }
}