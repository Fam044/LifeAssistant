package com.imooc.lib_network.impl

import com.imooc.lib_network.bean.*
import com.imooc.lib_network.http.HttpUrl

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HttpImplService {

    //=======================笑话===========================
    @GET(HttpUrl.JOKE_ONE_ACTION)
    fun queryJoke(
        @Query("key")key: String
    ):Call<JokeOneData>

    @GET(HttpUrl.JOKE_LIST_ACTION)
    fun queryJokeList(
        @Query("key")key: String,
        @Query("page") page: Int,
        @Query("pagesize") pageSize: Int
    ):Call<JokeListData>

    //=======================星座===========================
    @GET(HttpUrl.CONS_TELL_ACTION)
    fun queryTodayConsTellInfo(
        @Query("consName") consName: String,
        @Query("type") type: String,
        @Query("key") key: String
    ):Call<TodayData>

    @GET(HttpUrl.CONS_TELL_ACTION)
    fun queryWeekConsTellInfo(
        @Query("consName") consName: String,
        @Query("type") type: String,
        @Query("key") key: String
    ):Call<WeekData>

    @GET(HttpUrl.CONS_TELL_ACTION)
    fun queryMonthConsTellInfo(
        @Query("consName") consName: String,
        @Query("type") type: String,
        @Query("key") key: String
    ):Call<MonthData>

    @GET(HttpUrl.CONS_TELL_ACTION)
    fun queryYearConsTellInfo(
        @Query("consName") consName: String,
        @Query("type") type: String,
        @Query("key") key: String
    ):Call<YearData>

    //=======================天气===========================

    @GET(HttpUrl.WEATHER_ACTION)
    fun getWeather(@Query("city")city: String,
                   @Query("key")key: String
    ): Call<ResponseBody>
}