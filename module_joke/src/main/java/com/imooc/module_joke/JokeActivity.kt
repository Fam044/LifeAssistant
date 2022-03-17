package com.imooc.module_joke

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.imooc.lib_base.base.BaseActivity

class JokeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joke)
    }
}