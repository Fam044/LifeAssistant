package com.imooc.aivoiceapp.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.imooc.aivoiceapp.R
import com.imooc.aivoiceapp.adapter.ChatListAdapter
import com.imooc.aivoiceapp.data.ChatList
import com.imooc.aivoiceapp.entity.AppConstants
import com.imooc.lib_base.event.EventManager
import com.imooc.lib_base.event.MessageEvent
import com.imooc.lib_base.helper.ARouterHelper
import com.imooc.lib_base.helper.func.CommonSettingHelper
import com.imooc.lib_base.helper.NotificationHelper
import com.imooc.lib_base.helper.SoundPoolHelper
import com.imooc.lib_base.helper.WindowHelper
import com.imooc.lib_base.helper.func.AppHelper
import com.imooc.lib_base.helper.func.ConsTellHelper
import com.imooc.lib_base.helper.func.ContactHelper
import com.imooc.lib_base.map.MapManager
import com.imooc.lib_base.utils.L
import com.imooc.lib_base.utils.SpUtils
import com.imooc.lib_network.HttpManager
import com.imooc.lib_network.bean.JokeOneData
import com.imooc.lib_network.bean.RobotData
import com.imooc.lib_network.bean.WeatherData
import com.imooc.lib_voice.engine.VoiceEngineAnalyze
import com.imooc.lib_voice.error.VoiceErrorCode
import com.imooc.lib_voice.impl.OnAsrResultListener
import com.imooc.lib_voice.impl.OnNluResultListener
import com.imooc.lib_voice.manager.VoiceManager
import com.imooc.lib_voice.tts.VoiceTTS
import com.imooc.lib_voice.words.WordsTools
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoiceService: Service(), OnNluResultListener {

    private val mHandler = Handler()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        L.i("语音服务启动")
        initCoreVoiceService()
    }



    /**
     * START_STICKY: When the system memory is not enough, it kills the service, and restart the service when the memory is enough
     * START_NOT_STICKY: When the system memory is not enough, it kills the service until the startService start again
     * START_REDELIVER_INTENT: Deliver the Intent value again
     * START_STICK_COMPATIBILITY: A compatibility version of START_STICKY_COMPATIBILITY, but it can not assure the service that has been killed will restart
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bindNotification()
        return START_STICKY_COMPATIBILITY
    }

    //绑定通知栏
    private fun bindNotification() {
        startForeground(1000, NotificationHelper.bindVoiceService(getString(R.string.text_voice_run_text)))
    }

    private lateinit var mFullWindowView: View
    private lateinit var mChatListView: RecyclerView
    private lateinit var mLottieView: LottieAnimationView
    private val mList = ArrayList<ChatList>()
    private lateinit var ivCloseWindow: ImageView
    private lateinit var mChatAdapter: ChatListAdapter
    private lateinit var tvVoiceTips: TextView
    //初始化语音服务
    private fun initCoreVoiceService(){
        EventManager.register(this)
        WindowHelper.initHelper(this)
        mFullWindowView = WindowHelper.getView(R.layout.layout_window_item)
        mChatListView = mFullWindowView.findViewById<RecyclerView>(R.id.mChatListView)
        mLottieView = mFullWindowView.findViewById<LottieAnimationView>(R.id.mLottieView)
        tvVoiceTips = mFullWindowView.findViewById<TextView>(R.id.tvVoiceTips)
        ivCloseWindow = mFullWindowView.findViewById<ImageView>(R.id.ivCloseWindow)
        mChatListView.layoutManager = LinearLayoutManager(this)
        mChatAdapter = ChatListAdapter(mList)
        mChatListView.adapter = mChatAdapter

        ivCloseWindow.setOnClickListener {
            VoiceManager.ttsStop()
            hideTouchWindow()
        }

        VoiceManager.initManager(this, object : OnAsrResultListener{

            override fun wakeUpReady() {
                L.i("唤醒准备就绪")

                //发声人
                VoiceManager.setPeople(
                    resources.getStringArray(R.array.TTSPeopleIndex)[SpUtils.getInt("tts_people", 2)]
                )
                //语速
                VoiceManager.setVoiceSpeed(SpUtils.getInt("tts_speed", 5).toString())
                //音量
                VoiceManager.setVoiceVolume(SpUtils.getInt("tts_volume", 5).toString())

                val isHello = SpUtils.getBoolean("isHello", true)
                if (isHello){
                    addAiText("唤醒引擎准备就绪")
                }
            }

            override fun asrStartSpeak() {
                L.i("开始说话")
            }

            override fun asrStopSpeak() {
                L.i("结束说话")
               hideWindow()
            }

            override fun wakeUpSuccess(result: JSONObject) {
                L.i("唤醒成功:$result")
                val errorCode = result.optInt("errorCode")
                //唤醒成功
                if (errorCode == 0){
//                    val word = result.optString("word")
//                    //当唤醒词是小爱同学的时候，才开启识别
//                    if (word == getString(R.string.text_voice_wakeup_text)){
//                        wakeUpNoError()
//                    }
                    wakeUpNoError()
                }
            }

            override fun updateUserText(text: String) {
                updateTips(text)
            }

            override fun asrResult(result: JSONObject) {
                L.i("========================RESULT====================")
                L.i("result:$result")
//                val errorCode = result.optInt("sub_error")
//                if (errorCode != 0){
//                    addAiText(VoiceErrorCode.fixErrorCode(errorCode))
//                    hideWindow()
//                }
            }

            override fun nluResult(nlu: JSONObject) {
                L.i("========================NLU====================")
                L.i("nlu:$nlu")
                addMineText(nlu.optString("raw_text"))
                VoiceEngineAnalyze.analyzeNlu(nlu, this@VoiceService)
            }

            override fun asrVolume(volume: Int) {
                EventManager.post(EventManager.VOLUME_ASR, volume)
            }

            override fun voiceError(text: String) {
                L.i("发生错误:$text")
                hideWindow()
            }

        })
    }

    /**
     * 唤醒成功后的操作
     */
    private fun wakeUpNoError(){
        showWindow()
        updateTips(getString(R.string.text_voice_wakeup_tips))
        SoundPoolHelper.play(R.raw.record_start)
        //应答
        val wakeUpText = WordsTools.wakeUpWords()
        addAiText(wakeUpText, object : VoiceTTS.OnTTSResultListener{
            override fun ttsEnd() {
                //开始识别
                VoiceManager.startAsr()
            }
        })
    }

    //显示窗口
    private fun showWindow(){
        L.i("=========显示窗口==========")
        mLottieView.playAnimation()
        WindowHelper.show(mFullWindowView)
    }

    //隐藏窗口
    private fun hideWindow(){
        L.i("==========隐藏窗口==========")
        mHandler.postDelayed({
            WindowHelper.hide(mFullWindowView)
            mLottieView.pauseAnimation()
            SoundPoolHelper.play(R.raw.record_over)
        }, 2 * 1000)
    }

    //直接隐藏窗口
    private fun hideTouchWindow(){
        L.i("==========隐藏窗口==========")
        WindowHelper.hide(mFullWindowView)
        mLottieView.pauseAnimation()
        SoundPoolHelper.play(R.raw.record_over)
        VoiceManager.stopAsr()
    }

    //打开APP
    override fun openApp(appName: String) {
        if (!TextUtils.isEmpty(appName)){
            L.i("Open App $appName")
            val isOpen = AppHelper.launchApp(appName)
            if (isOpen){
                addAiText(getString(R.string.text_voice_app_open, appName))
            }else{
                addAiText(getString(R.string.text_voice_app_not_open, appName))
            }
        }
        hideWindow()
    }

    //卸载APP
    override fun unInstallApp(appName: String) {
        if (!TextUtils.isEmpty(appName)){
            L.i("Uninstall App $appName")
            val isUninstall = AppHelper.unInstallApp(appName)
            if (isUninstall){
                addAiText(getString(R.string.text_voice_app_uninstall, appName))
            }else{
                addAiText(getString(R.string.text_voice_app_not_uninstall))
            }
        }
        hideWindow()
    }

    //其他App
    override fun otherApp(appName: String) {
        //全部跳转应用市场
        if (!TextUtils.isEmpty(appName)){
            val isIntent = AppHelper.launchAppStore(appName)
            if (isIntent){
                addAiText(getString(R.string.text_voice_app_option, appName))
            }else{
                addAiText(WordsTools.noAnswerWords())
            }
//            AppHelper.launchAppStore(appName)
        }
        hideWindow()
    }

    //返回操作
    override fun back() {
        addAiText(getString(R.string.text_voice_back_text))
        CommonSettingHelper.back()
        hideWindow()
    }

    //返回主页
    override fun home() {
        addAiText(getString(R.string.text_voice_home_text))
        CommonSettingHelper.home()
        hideWindow()
    }

    //增加音量
    override fun setVolumeUp() {
        addAiText(getString(R.string.text_voice_volume_add))
        CommonSettingHelper.setVolumeUp()
        hideWindow()
    }

    //减小音量
    override fun setVolumeDown() {
        addAiText(getString(R.string.text_voice_volume_sub))
        CommonSettingHelper.setVolumeDown()
        hideWindow()
    }

    override fun quit() {
        addAiText(WordsTools.quitWords(), object : VoiceTTS.OnTTSResultListener{
            override fun ttsEnd() {
                hideTouchWindow()
            }
        })
    }

    //拨打联系人
    override fun callPhoneForName(name: String) {
        val list = ContactHelper.mContactList.filter { it.phoneName == name }
        if (list.isNotEmpty()){
            addAiText("正在为你拨打$name 的电话", object : VoiceTTS.OnTTSResultListener{
                override fun ttsEnd() {
                    ContactHelper.callPhone(list[0].phoneNumber)
                }
            })
        }else{
            addAiText(getString(R.string.text_voice_no_friend))
        }
        hideWindow()
    }

    //拨打电话号码
    override fun callPhoneForNumber(phone: String) {
        addAiText("正在为你拨打$phone", object : VoiceTTS.OnTTSResultListener{
            override fun ttsEnd() {
                ContactHelper.callPhone(phone)
            }
        })
        hideWindow()
    }

    override fun playJoke() {
        HttpManager.queryJoke(object : Callback<JokeOneData>{
            override fun onResponse(call: Call<JokeOneData>, response: Response<JokeOneData>) {
                L.i("Joke onResponse")
                if (response.isSuccessful){
                    response.body()?.let {
                        if (it.error_code == 0){
                            //根据Result随机抽取一段笑话进行播放
                            val index = WordsTools.randomInt(it.result.size)
                            L.i("index: $index")
                            if (index < it.result.size){
                                val data = it.result[index]
                                addAiText(data.content, object : VoiceTTS.OnTTSResultListener{
                                    override fun ttsEnd() {
                                        hideWindow()
                                    }
                                })
                            }
                        }else{
                            jokeError()
                        }
                    }
                }else{
                    jokeError()
                }
            }

            override fun onFailure(call: Call<JokeOneData>, t: Throwable) {
                L.i("onFailure:$t")
                jokeError()
            }
        })
    }

    override fun jokeList() {
        addAiText(getString(R.string.text_voice_query_joke))
        ARouterHelper.startActivity(ARouterHelper.PATH_JOKE)
        hideWindow()
    }

    //星座时间
    override fun conTellTime(name: String) {
        L.i("conTellTime:$name")
        val text = ConsTellHelper.getConsTellTime(name)
        addAiText(text, object : VoiceTTS.OnTTSResultListener{
            override fun ttsEnd() {
                hideWindow()
            }
        })
    }

    //星座详情
    override fun conTellInfo(name: String) {
        L.i("conTellInfo:$name")
        addAiText(getString(R.string.text_voice_query_con_tell_info, name),object : VoiceTTS.OnTTSResultListener{
            override fun ttsEnd() {
                hideWindow()
            }
        })
        ARouterHelper.startActivity(ARouterHelper.PATH_CONSTELLATION, "name", name)
    }

    override fun aiRobot(text: String) {
        //请求机器人回答
        //2022-4-28过期
        HttpManager.aiRobotChat(text, object : Callback<RobotData>{
            override fun onResponse(
                call: Call<RobotData>,
                response: Response<RobotData>
            ) {
                L.i("机器人结果" + response.body().toString())
                if (response.isSuccessful){
                    response.body()?.let {
                        if (it.intent.code == 10004){
                            //回答
                            if (it.results.isEmpty()){
                                addAiText(WordsTools.noAnswerWords())
                                hideWindow()
                            }else{
                                addAiText(it.results[0].values.text)
                            }
                        }else{
                            addAiText(WordsTools.noAnswerWords())
                            hideWindow()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<RobotData>, t: Throwable) {
                addAiText(WordsTools.noAnswerWords())
                hideWindow()
            }

        })
    }

    override fun queryWeather(city: String) {
        HttpManager.run {
            queryWeather(city, object : Callback<WeatherData>{
                override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                    if (response.isSuccessful){
                        response.body()?.let {
                            //填充数据
                            it.result.realtime.apply {
                                addWeather(city, wid, info, temperature, aqi, object : VoiceTTS.OnTTSResultListener{
                                    override fun ttsEnd() {
                                        hideWindow()
                                    }
                                })
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                    addAiText(getString(R.string.text_voice_query_weather_error, city))
                    hideWindow()
                }
            })
        }
    }

    override fun queryWeatherInfo(city: String) {
        addAiText(getString(R.string.text_voice_query_weather, city))
        ARouterHelper.startActivity(ARouterHelper.PATH_WEATHER, "city", city)
        hideWindow()
    }

    //周边搜索
    override fun nearByMap(poi: String) {
        L.i("nearByMap: $poi")
        addAiText(getString(R.string.text_voice_query_poi, poi))
        ARouterHelper.startActivity(ARouterHelper.PATH_MAP, "type", "poi", "keyword", poi)
        hideWindow()
    }

    override fun routeMap(address: String) {
        L.i("routeMap: $address")
        addAiText(getString(R.string.text_voice_query_navi, address))
        ARouterHelper.startActivity(ARouterHelper.PATH_MAP, "type", "route", "keyword", address)
        hideWindow()
    }


    //语义识别失败
    override fun nluError() {
        addAiText(WordsTools.noAnswerWords())
        hideWindow()
    }

    /**
     * 添加我的文本
     */
    private fun addMineText(text: String){
        val bean = ChatList(AppConstants.TYPE_MINE_TEXT)
        bean.text = text
        baseAddItem(bean)
    }

    /**
     * 添加AI文本
     */
    private fun addAiText(text: String){
        val bean = ChatList(AppConstants.TYPE_AI_TEXT)
        bean.text = text
        baseAddItem(bean)
        VoiceManager.ttsStart(text)
    }

    /**
     * 添加AI文本
     */
    private fun addAiText(text: String, mOnTTSResultListener: VoiceTTS.OnTTSResultListener){
        val bean = ChatList(AppConstants.TYPE_AI_TEXT)
        bean.text = text
        baseAddItem(bean)
        VoiceManager.ttsStart(text,mOnTTSResultListener)
    }

    /**
     * 添加天气文本
     */
    private fun addWeather(city: String,
                           wid: String,
                           info: String,
                           temperature: String,
                           air: String,
    mOnTTSResultListener: VoiceTTS.OnTTSResultListener){

        val bean = ChatList(AppConstants.TYPE_AI_WEATHER)
        bean.city = city
        bean.wid = wid
        bean.info = info
        bean.temperature = "$temperature°"
        bean.air = air

        baseAddItem(bean)
        val text = "$city 今天天气$info ，温度$temperature 度"
        VoiceManager.ttsStart(text, mOnTTSResultListener)
    }

    /**
     * 添加基类
     */
    private fun baseAddItem(bean: ChatList){
        mList.add(bean)
        mChatAdapter.notifyItemInserted(mList.size - 1)
        //滚到到底部
        mChatListView.scrollToPosition(mList.size - 1)
    }

    /**
     * 更新提示语
     */
    private fun updateTips(text: String){
        tvVoiceTips.text = text
    }

    /**
     * 笑话错误
     */
    private fun jokeError(){
        hideWindow()
        addAiText("很抱歉，未能搜索到笑话")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(message: MessageEvent){
        when(message.type){
            EventManager.WAKE_UP -> wakeUpNoError()
        }
    }
}