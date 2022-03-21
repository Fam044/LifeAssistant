package com.imooc.module_developer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.imooc.lib_base.base.BaseActivity
import com.imooc.lib_base.base.adapter.CommonAdapter
import com.imooc.lib_base.base.adapter.CommonViewHolder
import com.imooc.lib_base.helper.ARouterHelper
import com.imooc.lib_voice.manager.VoiceManager
import com.imooc.module_developer.data.DeveloperListData
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_developer.*

/**
 *
 */
@Route(path = ARouterHelper.PATH_DEVELOPER)
class DeveloperActivity : BaseActivity() {

    //Title
    private val mTypeTitle = 0
    //Content
    private val mTypeContent = 1

    private val mList = ArrayList<DeveloperListData>()

    override fun getLayoutId(): Int {
        return R.layout.activity_developer
    }

    override fun getTitleText(): String {
        return getString(com.imooc.lib_base.R.string.app_title_developer)
    }

    override fun initView() {
        AndPermission.with(this)
            .runtime()
            .permission(Permission.RECORD_AUDIO)
            .start()
        initData()
        initListView()
    }

    //initData
    private fun initListView() {
        //LayoutManager
        rvDeveloperView.layoutManager = LinearLayoutManager(this)
        //Divider
        rvDeveloperView.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        //Adapter
        rvDeveloperView.adapter = CommonAdapter(mList, object : CommonAdapter.OnMoreBindDataListener<DeveloperListData>{
            override fun onBindViewHolder(
                model: DeveloperListData,
                viewHolder: CommonViewHolder,
                type: Int,
                position: Int
            ) {
                when(model.type){
                    mTypeTitle -> {
                        viewHolder.setText(R.id.mTvDeveloperTitle, model.text)
                    }
                    mTypeContent -> {
                        viewHolder.setText(R.id.mTvDeveloperContent, "${position}.${model.text}")
                        viewHolder.itemView.setOnClickListener {
                            itemClickFun(position)
                        }
                    }
                }
            }

            override fun getLayoutId(type: Int): Int {
                return if (type == mTypeTitle){
                    R.layout.layout_developer_title
                }else{
                    R.layout.layout_developer_content
                }
            }

            override fun getItemViewType(position: Int): Int {
                return mList[position].type
            }
        })
    }

    private fun initData() {
        val dataArray = resources.getStringArray(com.imooc.lib_base.R.array.DeveloperListArray)
        dataArray.forEach {
            //subtitle
            if (it.contains("[")){
                addItemData(mTypeTitle,it.replace("[","").replace("]",""))
            }else{
                addItemData(mTypeContent,it)
            }
        }
    }

    //addItemData
    private fun addItemData(type: Int, text: String){
        mList.add(DeveloperListData(type, text))
    }

    override fun isShowBack(): Boolean {
        return true
    }

    //ClickEvent
    private fun itemClickFun(position: Int){
        when(position){
            1 -> ARouterHelper.startActivity(ARouterHelper.PATH_APP_MANAGER)
            2 -> ARouterHelper.startActivity(ARouterHelper.PATH_CONSTELLATION)
            3 -> ARouterHelper.startActivity(ARouterHelper.PATH_JOKE)
            4 -> ARouterHelper.startActivity(ARouterHelper.PATH_MAP)
            5 -> ARouterHelper.startActivity(ARouterHelper.PATH_SETTING)
            6 -> ARouterHelper.startActivity(ARouterHelper.PATH_VOICE_SETTING)
            7 -> ARouterHelper.startActivity(ARouterHelper.PATH_WEATHER)

            9 -> VoiceManager.startAsr()
            10 -> VoiceManager.stopAsr()
            11 -> VoiceManager.cancelAsr()
            12 -> VoiceManager.releaseAsr()

            14 -> VoiceManager.startWakeUp()
            15 -> VoiceManager.stopWakeUp()

            20 -> VoiceManager.ttsStart("你好,我是乌阴哥")
            21 -> VoiceManager.ttsPause()
            22 -> VoiceManager.ttsResume()
            23 -> VoiceManager.ttsStop()
            24 -> VoiceManager.ttsRelease()
        }
    }
}