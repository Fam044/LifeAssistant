package com.imooc.module_weather


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.text.format.Time
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.gyf.immersionbar.ImmersionBar
import com.imooc.lib_base.base.BaseActivity
import com.imooc.lib_base.helper.ARouterHelper
import com.imooc.lib_base.utils.L
import com.imooc.lib_base.utils.SpUtils
import com.imooc.lib_network.HttpManager
import com.imooc.lib_network.bean.WeatherData
import com.imooc.module_weather.tools.WeatherIconTools
import com.imooc.module_weather.ui.CitySelectActivity
import kotlinx.android.synthetic.main.activity_weather.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Route(path = ARouterHelper.PATH_WEATHER)
class WeatherActivity : AppCompatActivity() {

    //默认当前城市
    private var currentCity = "广州"

    //跳转
    private val codeSelect = 100

    //时间格式化
    private val mFormat = SimpleDateFormat("YYYY-MM-dd HH:mm:ss", Locale.CHINA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        //初始化状态栏
//        ImmersionBar.with(this).transparentBar().init()

        initView()
    }

    override fun onResume() {
        super.onResume()

        val time = Time()
        time.setToNow()
        val hour = time.hour

        L.i("当前时间: $hour")
    }

    //初始化View
    private fun initView(){
        intent.run {
            val city = getStringExtra("city")
            if (!TextUtils.isEmpty(city)) {
                loadWeatherData(city!!)
            } else {
                //非语音进入,先查询本地
                val localCity = SpUtils.getString("city")
                if (!TextUtils.isEmpty(localCity)) {
                    loadWeatherData(localCity!!)
                } else {
                    startCitySelectActivity()
                }
            }
        }

        //点击事件
        ivBack.setOnClickListener { finish() }
        ivMenu.setOnClickListener {
            //跳转到城市选择中去
            startCitySelectActivity()
        }
    }

    //加载天气数据
    private fun loadWeatherData(city: String){
        currentCity = city
        SpUtils.putString("city", currentCity)

        //标题
//        tvTitle.text = city
        tvCity.text = city

//        initChart()
        loadWeather()
    }

    //load city weather data
    private fun loadWeather() {
        //setting
        supportActionBar?.title = "天气"
            HttpManager.run {
                queryWeather(currentCity, object : Callback<WeatherData>{
                    override fun onResponse(
                        call: Call<WeatherData>,
                        response: Response<WeatherData>
                    ) {
                        L.i("onResponse")
                        if (response.isSuccessful){
                            response.body()?.let {
                                // 超过每日可查询次数
                                if (it.error_code == 10012){
                                    return
                                }

                                tvTime.text = getString(
                                    R.string.app_weather_time,
                                    mFormat.format(System.currentTimeMillis())
                                )
                                //填充数据
                                it.result.realtime.apply {
                                    //设置天气 阴
                                    mInfo.text = getString(R.string.app_weather_aqi_info, info, aqi)
                                    //设置图片
                                    //mIvWid.setImageResource(WeatherIconTools.getIcon(wid))
                                    //设置温度
                                    mTemperature.text = String.format(
                                        "%s%s",
                                        temperature,
                                        getString(R.string.app_weather_t)
                                    )
                                }
                                //设置未来五天
                                it.result.future.forEachIndexed { index, future ->
                                    when (index) {
                                        0 -> {
                                            tv1Top.text = getString(R.string.app_date_today)
                                            iv1.setImageResource(WeatherIconTools.getWeatherIcon(future.weather))
                                            tv1Bottom.text = future.temperature
                                        }
                                        1 -> {
                                            tv2Top.text = future.date.substring(
                                                future.date.length - 2,
                                                future.date.length
                                            )
                                            iv2.setImageResource(WeatherIconTools.getWeatherIcon(future.weather))
                                            tv2Bottom.text = future.temperature
                                        }
                                        2 -> {
                                            tv3Top.text = future.date.substring(
                                                future.date.length - 2,
                                                future.date.length
                                            )
                                            iv3.setImageResource(WeatherIconTools.getWeatherIcon(future.weather))
                                            tv3Bottom.text = future.temperature
                                        }
                                        3 -> {
                                            tv4Top.text = future.date.substring(
                                                future.date.length - 2,
                                                future.date.length
                                            )
                                            iv4.setImageResource(WeatherIconTools.getWeatherIcon(future.weather))
                                            tv4Bottom.text = future.temperature
                                        }
                                        4 -> {
                                            tv5Top.text = future.date.substring(
                                                future.date.length - 2,
                                                future.date.length
                                            )
                                            iv5.setImageResource(WeatherIconTools.getWeatherIcon(future.weather))
                                            tv5Bottom.text = future.temperature
                                        }
                                    }
                                }
                                val data = ArrayList<Entry>()
                                //绘制图表
                                it.result.future.forEachIndexed { index, future ->
                                    val temp = if (index % 2 == 0) {
                                        future.temperature.substring(
                                            future.temperature.length - 3,
                                            future.temperature.length - 1
                                        )
                                    } else {
                                        future.temperature.substring(0, 2)
                                    }
                                    data.add(Entry((index + 1).toFloat(), temp.toFloat()))
                                }
//                                setLineChartData(data)
                            }
                        }
                    }

                    override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                        L.i("onFailure:$t")
                        Toast.makeText(this@WeatherActivity, "Request Failed", Toast.LENGTH_SHORT).show()
                    }
                })
            }
    }

    //初始化图表
//    private fun initChart(){
//        //================基本设置====================
//
//        //背景网格
//        mLineChart.setDrawGridBackground(true)
//        //开启描述文本
//        mLineChart.description.isEnabled = true
//        mLineChart.description.text = "该数据由聚合数据提供"
//        //触摸手势
//        mLineChart.setTouchEnabled(true)
//        //支持缩放
//        mLineChart.setScaleEnabled(true)
//        //支持拖拽
//        mLineChart.isDragEnabled = true
//        //扩展缩放
//        mLineChart.setPinchZoom(true)
//
//        //设置背景颜色
//        mLineChart.setBackgroundColor(resources.getColor(R.color.white))
//        mLineChart.setGridBackgroundColor(resources.getColor(R.color.white))
//
//
//        //================轴配置====================
//        //平均线
//        val xLimitLine = LimitLine(10f, "")
//        xLimitLine.lineWidth = 4f
//        xLimitLine.enableDashedLine(10f, 10f, 0f)
//        xLimitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
//        xLimitLine.textSize = 10f
//
//        val xAxis = mLineChart.xAxis
//        xAxis.textColor = Color.BLACK
//        xAxis.enableAxisLineDashedLine(10f, 10f, 0f)
//        //最大值
//        xAxis.mAxisMaximum = 5f
//        //最小值
//        xAxis.axisMinimum = 1f
//
//        val axisLeft = mLineChart.axisLeft
//        axisLeft.enableAxisLineDashedLine(10f, 10f, 0f)
//        //最大值
//        axisLeft.mAxisMaximum = 40f
//        //最小值
//        axisLeft.axisMinimum = 20f
//
//        //禁止右边的Y轴
//        mLineChart.axisRight.isEnabled = false
//    }
//
//    private fun setLineChartData(values: java.util.ArrayList<Entry>) {
//        if (mLineChart.data != null && mLineChart.data.dataSetCount > 0){
//            //获取数据容器
//            val set = mLineChart.data.getDataSetByIndex(0) as LineDataSet
//            set.values = values
//            mLineChart.data.notifyDataChanged()
//            mLineChart.notifyDataSetChanged()
//        }else{
//            val set = LineDataSet(values, "${currentCity}未来五天温度")
//
//            //====================UI配置======================
//            set.enableDashedLine(20f, 5f, 0f)
//            set.setCircleColor(Color.BLACK)
//            set.lineWidth = 1f
//            set.circleRadius = 3f
//            set.valueTypeface = Typeface.DEFAULT_BOLD
//            set.setDrawCircleHole(false)
//            set.valueTextSize = 10f
//            set.formLineWidth = 1f
//            set.setDrawFilled(true)
//            set.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
//            set.formSize = 15f
//
//            set.fillColor = Color.YELLOW
//
//            //设置数据
//            val dataSet = ArrayList<LineDataSet>()
//            dataSet.add(set)
//            val data = LineData(dataSet as List<ILineDataSet>?)
//            mLineChart.data = data
//        }
//
//        //===================UI配置=================
//        //X轴动画
//        mLineChart.animateX(1000)
//        //刷新
//        mLineChart.invalidate()
//        //页眉
//        val legend = mLineChart.legend
//        legend.form = Legend.LegendForm.LINE
//    }

    private fun startCitySelectActivity() {
        val intent = Intent(this, CitySelectActivity::class.java)
        startActivityForResult(intent, codeSelect)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == codeSelect){
                data?.let {
                    val city = it.getStringExtra("city")
                    if (!TextUtils.isEmpty(city)){
                        loadWeatherData(city!!)
                    }
                }
            }
        }
    }
}