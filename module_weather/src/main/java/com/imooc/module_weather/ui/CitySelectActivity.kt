package com.imooc.module_weather.ui

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imooc.lib_base.base.BaseActivity
import com.imooc.lib_base.base.adapter.CommonAdapter
import com.imooc.lib_base.base.adapter.CommonViewHolder
import com.imooc.lib_base.bean.Result
import com.imooc.lib_base.utils.AssetsUtils
import com.imooc.module_weather.R
import com.imooc.module_weather.data.CitySelectBean
import com.imooc.module_weather.view.CitySelectView
import kotlinx.android.synthetic.main.activity_city_select.*

class CitySelectActivity: BaseActivity() {

    /**
     * 实现城市选择列表
     * 1.加载assets数据
     * 2.实现POI
     * 3.实现列表
     * 4.自定义导航View
     * 5.双向的数据通信
     * 6.双向的联动
     */

    private val mList = ArrayList<CitySelectBean>()
    private lateinit var mCitySelectAdapter: CommonAdapter<CitySelectBean>

    private val mListTitle =  ArrayList<String>()

    //布局类型
    //标题
    private val mTypeTitle = 1000
    //热门城市
    private val mTypeHotCity = 1001
    //内容
    private val mTypeContent = 1002


    override fun getLayoutId(): Int {
        return R.layout.activity_city_select
    }

    override fun getTitleText(): String {
        return "选择城市"
    }

    override fun isShowBack(): Boolean {
        return true
    }

    override fun initView() {
        parsingData()
        initListView()
    }

    private fun initListView(){
        mCityListView.layoutManager = LinearLayoutManager(this)
        mCityListView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mCitySelectAdapter = CommonAdapter(mList, object: CommonAdapter.OnMoreBindDataListener<CitySelectBean>{
            override fun onBindViewHolder(
                model: CitySelectBean,
                viewHolder: CommonViewHolder,
                type: Int,
                position: Int
            ) {
                when(model.type){
                    mTypeTitle -> viewHolder.setText(R.id.mTvCityTitle, model.title)
                    mTypeHotCity -> {
                        val mCityHotView = viewHolder.getView(R.id.mCityHotView) as RecyclerView
                        setHotCityView(mCityHotView)
                    }
                    mTypeContent -> {
                        viewHolder.setText(R.id.mTvCityContent, model.content)
                        viewHolder.itemView.setOnClickListener {
                            finishResult(model.city)
                        }
                    }
                }
            }

            override fun getLayoutId(type: Int): Int {
                return when(type){
                    mTypeTitle -> R.layout.layout_city_title
                    mTypeHotCity -> R.layout.layout_city_hot
                    mTypeContent -> R.layout.layout_city_content
                    else -> R.layout.layout_city_title
                }
            }

            override fun getItemType(position: Int): Int {
                return mList[position].type
            }
        })
        mCityListView.adapter = mCitySelectAdapter

        mCityListView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //计算当前滚动到的是哪一个城市，通知View去刷新选中的状态
                val lm = recyclerView.layoutManager
                if (lm is LinearLayoutManager){
                    val firstPosition = lm.findFirstCompletelyVisibleItemPosition()
                    val lastPosition = lm.findFirstCompletelyVisibleItemPosition()
                    //取中间值
                    val centerIndex = (lastPosition - firstPosition) / 2 + firstPosition
                    val province = mList[centerIndex].province
                    val itemIndex = mListTitle.indexOf(province)
                    if (itemIndex >= 0 && itemIndex < mListTitle.size){
                        mCitySelectView.setCheckIndex(itemIndex)
                    }
                }
            }
        })
    }

    //设置热门城市
    private fun setHotCityView(mCityHotView: RecyclerView) {
        val cityList = resources.getStringArray(R.array.HotCityArray)

        mCityHotView.layoutManager = GridLayoutManager(this, 3)
        mCityHotView.adapter = CommonAdapter<String>(cityList.toList(), object: CommonAdapter.OnBindDataListener<String>{
            override fun onBindViewHolder(
                model: String,
                viewHolder: CommonViewHolder,
                type: Int,
                position: Int
            ) {
                viewHolder.setText(R.id.mTvHotCity, model)

                viewHolder.itemView.setOnClickListener {
                    finishResult(model)
                }
            }

            override fun getLayoutId(type: Int): Int {
                return R.layout.layout_hot_city_item
            }

        })
    }

    //解析数据
    private fun parsingData() {
        val city = AssetsUtils.getCity()
        addTitle("热门城市")
        addHot()
        //其他操作 标题+内容
        city.result.forEach {

                if (!mListTitle.contains(it.province)){
                    addTitle(it.province)
                }
                //添加内容
                addContent("${it.city}市${it.district}", it.district, it.province)
        }

        mCitySelectView.setCity(mListTitle)

        mCitySelectView.setOnViewResultListener(object : CitySelectView.OnViewResultListener{
            override fun uiChange(uiShow: Boolean) {
                mShowCity.visibility = if (uiShow) View.VISIBLE else View.GONE
            }

            override fun valueInput(text: String) {
                mShowCity.text = text

                //计算值
                findTextIndex(text)
            }

        })
    }

    //根据城市寻找下标
    private fun findTextIndex(text: String) {
        if (mList.size > 0){
            mList.forEachIndexed{ index, citySelectBean ->
                if (text == citySelectBean.title){
                    mCityListView.scrollToPosition(index)
                    return@forEachIndexed
                }

            }
        }
    }

    private fun addTitle(title: String){
        val data = CitySelectBean(mTypeTitle, title, "", "", title)
        mList.add(data)
        mListTitle.add(title)
    }

    private fun addContent(content: String, city: String, province: String){
        val data = CitySelectBean(mTypeContent, "", content, city, province)
        mList.add(data)
    }

    private fun addHot(){
        val data = CitySelectBean(mTypeHotCity, "", "", "", "热门城市")
        mList.add(data)
    }

    private fun finishResult(city: String){
        val intent = Intent()
        intent.putExtra("city", city)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}