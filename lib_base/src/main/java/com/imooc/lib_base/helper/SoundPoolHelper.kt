package com.imooc.lib_base.helper

import android.content.Context
import android.media.SoundPool

/**
 * 播放铃声
 */
object SoundPoolHelper {
    private lateinit var mContext: Context
    private lateinit var mSoundPool: SoundPool

    fun init(mContext: Context){
        this.mContext = mContext
        mSoundPool = SoundPool.Builder().setMaxStreams(1).build()
    }

    fun play(resId: Int){
        val poolId = mSoundPool.load(mContext, resId, 1)
        mSoundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                /**
                 * Params:
                 * soundID – a soundID returned by the load() function
                 * leftVolume – left volume value (range = 0.0 to 1.0)
                 * rightVolume – right volume value (range = 0.0 to 1.0)
                 * priority – stream priority (0 = lowest priority)
                 * loop – loop mode (0 = no loop, -1 = loop forever)
                 * rate – playback rate (1.0 = normal playback, range 0.5 to 2.0)
                 */
                mSoundPool.play(poolId, 1f, 1f, 1, 0, 1f)
            }
        }
    }
}