package com.imooc.lib_voice.engine

import android.util.Log
import com.imooc.lib_voice.impl.OnNluResultListener
import com.imooc.lib_voice.words.NluWords
import org.json.JSONObject

/**
 * Profile:语音引擎分析
 */
object VoiceEngineAnalyze {

    private var TAG = VoiceEngineAnalyze::class.java.simpleName

    private lateinit var mOnNluResultListener: OnNluResultListener
    //分析结果
    fun analyzeNlu(nlu: JSONObject, mOnNluResultListener: OnNluResultListener){
        this.mOnNluResultListener = mOnNluResultListener
        //用户说的话
        val rawText = nlu.optString("raw_text")
        Log.i(TAG, "rawText:$rawText")

        //解析results
        val results = nlu.optJSONArray("results") ?: return

        val nluResultLength = results.length()
        when {
            nluResultLength <= 0 -> {
                return
            }
            results.length() == 1 -> {
                //单条命中
                analyzeNluSingle(results[0] as JSONObject)
            }
            else -> {
                //多条命中
            }
        }
    }

    //处理单条结果
    private fun analyzeNluSingle(result: JSONObject) {
        val domain = result.optString("domain")
        val intent = result.optString("intent")
        val slots = result.optJSONObject("slots")

        when(domain){
            NluWords.NLU_WEATHER -> {
                //获取其他类型
            }
        }
    }
}