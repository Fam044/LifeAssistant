package com.imooc.aivoiceapp.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.imooc.aivoiceapp.R
import com.imooc.aivoiceapp.adapter.ChatListAdapter
import com.imooc.aivoiceapp.data.ChatList
import com.imooc.aivoiceapp.entity.AppConstants
import com.imooc.lib_base.helper.NotificationHelper
import com.imooc.lib_base.helper.SoundPoolHelper
import com.imooc.lib_base.helper.WindowHelper
import com.imooc.lib_base.utils.L
import com.imooc.lib_voice.engine.VoiceEngineAnalyze
import com.imooc.lib_voice.impl.OnAsrResultListener
import com.imooc.lib_voice.impl.OnNluResultListener
import com.imooc.lib_voice.manager.VoiceManager
import com.imooc.lib_voice.tts.VoiceTTS
import com.imooc.lib_voice.words.WordsTools
import org.json.JSONObject
import org.w3c.dom.Text

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
        startForeground(1000, NotificationHelper.bindVoiceService("正在运行"))
    }

    private lateinit var mFullWindowView: View
    private lateinit var mChatListView: RecyclerView
    private lateinit var mLottieView: LottieAnimationView
    private val mList = ArrayList<ChatList>()
    private lateinit var mChatAdapter: ChatListAdapter
    private lateinit var tvVoiceTips: TextView
    //初始化语音服务
    private fun initCoreVoiceService(){
        WindowHelper.initHelper(this)
        mFullWindowView = WindowHelper.getView(R.layout.layout_window_item)
        mChatListView = mFullWindowView.findViewById<RecyclerView>(R.id.mChatListView)
        mLottieView = mFullWindowView.findViewById<LottieAnimationView>(R.id.mLottieView)
        tvVoiceTips = mFullWindowView.findViewById<TextView>(R.id.tvVoiceTips)
        mChatListView.layoutManager = LinearLayoutManager(this)
        mChatAdapter = ChatListAdapter(mList)
        mChatListView.adapter = mChatAdapter


        VoiceManager.initManager(this, object : OnAsrResultListener{

            override fun wakeUpReady() {
                L.i("唤醒准备就绪")
                VoiceManager.ttsStart("唤醒引擎准备就绪")
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
                //当唤醒词是小爱同学的时候，才开启识别
                val errorCode = result.optInt("errorCode")
                //唤醒成功
                if (errorCode == 0){
                    val word = result.optString("word")
                    if (word == "小爱同学"){
                        wakeUpNoError()
                    }
                }
            }

            override fun updateUserText(text: String) {
                updateTips(text)
            }

            override fun asrResult(result: JSONObject) {
                L.i("========================RESULT====================")
                L.i("result:$result")
            }

            override fun nluResult(nlu: JSONObject) {
                L.i("========================NLU====================")
                L.i("nlu:$nlu")
                addMineText(nlu.optString("raw_text"))
                addAiText(nlu.toString())
                VoiceEngineAnalyze.analyzeNlu(nlu, this@VoiceService)
            }

            override fun voiceError(text: String) {
                L.i("发生错误:$text")
                hideWindow()
            }

        })
    }

    private fun wakeUpNoError(){
        showWindow()
        updateTips("正在聆听...")
        SoundPoolHelper.play(R.raw.record_start)
        //应答
        val wakeUpText = WordsTools.wakeUpWords()
        addAiText(wakeUpText)
        VoiceManager.ttsStart(wakeUpText, object : VoiceTTS.OnTTSResultListener{
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
        }, 2 * 1000)
    }

    //查询天气
    override fun queryWeather() {

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
    }

    /**
     * 添加基类
     */
    private fun baseAddItem(bean: ChatList){
        mList.add(bean)
        mChatAdapter.notifyItemInserted(mList.size - 1)
    }

    /**
     * 更新提示语
     */
    private fun updateTips(text: String){
        tvVoiceTips.text = text
    }
}