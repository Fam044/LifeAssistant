package com.imooc.lib_base.map

import android.content.Context
import android.content.QuickViewConstants
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MapView
import android.R
import android.location.Location
import android.text.TextUtils
import android.util.Log
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation

import com.baidu.mapapi.map.SupportMapFragment

import com.baidu.mapapi.map.MapStatusUpdate
import com.baidu.mapapi.model.LatLng
import com.baidu.location.LocationClientOption

import com.baidu.location.LocationClient
import com.baidu.mapapi.search.core.RouteNode.location

import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.core.RouteNode.location
import com.baidu.mapapi.search.core.RouteNode.location
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.poi.*
import com.baidu.mapapi.search.poi.PoiCitySearchOption





object MapManager {

    const val TAG = "MapManager"

    //最大缩放等级  4-21
    private const val MAX_ZOOM:Float = 18f

    private var mMapView : MapView? = null
    private var mBaiduMap: BaiduMap? = null
    private var mPoiSearch: PoiSearch? = null

    //用户所在城市
    private var locationCity: String = ""

    //定位客户端
    private lateinit var mLocationClient: LocationClient

    private var mOnLocationResultListener: OnLocationResultListener? = null
    private var mOnPoiResultListener: OnPoiResultListener? = null

    //初始化
    fun initMap(mContext: Context){
        SDKInitializer.setAgreePrivacy(mContext, true)
        SDKInitializer.initialize(mContext)
        LocationClient.setAgreePrivacy(true)
        mLocationClient = LocationClient(mContext)
        //初始化POI
        initPoi()
        //初始化定位
        initLocation()
    }

    //绑定地图
    fun bindMapView(mMapView: MapView){
        this.mMapView = mMapView
        mBaiduMap = mMapView.map

        //默认缩放
        zoomMap(MAX_ZOOM)
        //默认卫星地图
        //setMapType(1)
        //默认打开实时路况
        //setTrafficEnabled(true)
        //默认打开热力图
        //setBaiduHeatMapEnabled(true)

        //=================定位==============
        //开启定位
        setMyLocationEnabled(true)

    }

    fun setPoiImage(poiResult: PoiResult){
        mBaiduMap?.clear()
        //创建PoiOverlay对象
        val poiOverlay = PoiOverlay(mBaiduMap)
        //设置Poi检索数据
        poiOverlay.setData(poiResult)
        //将poiOverlay添加至地图并缩放至合适级别
        poiOverlay.addToMap()
        poiOverlay.zoomToSpan()
    }

    //========================操作方法=====================
    //缩放地图
    fun zoomMap(value: Float){
        val builder = MapStatus.Builder()
        builder.zoom(value)
        mBaiduMap?.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
    }

    //设置默认中心点
    fun setCenterMap(la: Double, lo: Double){
        val latLng = LatLng(la, lo)
        mBaiduMap?.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng))
    }

    /**
     * 0: MAP_TYPE_NORMAL 普通地图（含3D地图）
     * 1: MAP_TYPE_SATELLITE 卫星图
     */
    fun setMapType(index: Int){
        mBaiduMap?.mapType = if (index == 0) BaiduMap.MAP_TYPE_NORMAL else BaiduMap.MAP_TYPE_SATELLITE
    }

    //设置实时路况
    fun setTrafficEnabled(isOpen: Boolean){
        mBaiduMap?.isTrafficEnabled = isOpen
    }

    //设置百度热力图
    fun setBaiduHeatMapEnabled(isOpen: Boolean){
        mBaiduMap?.isBaiduHeatMapEnabled = isOpen
    }

    //设置定位开关
    fun setMyLocationEnabled(isOpen: Boolean){
        mBaiduMap?.isMyLocationEnabled = isOpen
    }

    //定位初始化
    fun initLocation(){
        val option = LocationClientOption()
        option.isOpenGps = true //打开GPS
        option.setCoorType("bd09ll") //地图标准
        option.setScanSpan(1000)
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true)
        //可选，设置是否需要地址描述
        option.setIsNeedLocationDescribe(true)
        //可选，设置是否需要设备方向结果
        option.setNeedDeviceDirect(true)
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.isLocationNotify = true
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(true)
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true)
        mLocationClient.locOption = option
        mLocationClient.registerLocationListener(object : BDAbstractLocationListener(){
            override fun onReceiveLocation(location: BDLocation?) {
                if (location == null || mMapView == null) {
                    return
                }

//                Log.e(TAG, "location: $location")
//                Log.e(TAG, "address: $addr")
//                Log.e(TAG, "errorCode: ${location.locType}")

                if (location.locType == 61 || location.locType == 161){
                    //设置默认中心点
                    //setCenterMap(location.latitude, location.longitude)
                    locationCity = location.city
                    mOnLocationResultListener?.result(
                        location.latitude,
                        location.longitude,
                        location.city,
                        location.addrStr,
                        location.locationDescribe
                    )
                }else{
                    mOnLocationResultListener?.fail()
                }
                //停止定位
                setLocationSwitch(false, null)

            }

        })

    }

    fun setLocationSwitch(isOpen: Boolean, mOnLocationResultListener: OnLocationResultListener?){
        if (isOpen){
            this.mOnLocationResultListener = mOnLocationResultListener
            mLocationClient.start()
        } else {
            mLocationClient.stop()
        }
    }

    //=========================POI=======================
    private fun initPoi(){
        mPoiSearch = PoiSearch.newInstance()
        mPoiSearch?.setOnGetPoiSearchResultListener(object : OnGetPoiSearchResultListener{
            override fun onGetPoiResult(result: PoiResult?) {
                result?.let {
                    if (it.error == SearchResult.ERRORNO.NO_ERROR){
                        mOnPoiResultListener?.result(it)
                        setPoiImage(result)
                    }
                }
            }

            //废弃
            override fun onGetPoiDetailResult(result: PoiDetailResult?) {            }

            override fun onGetPoiDetailResult(result: PoiDetailSearchResult?) {
                Log.i(TAG, "=====> onGetPoiDetailResult")
            }

            override fun onGetPoiIndoorResult(result: PoiIndoorResult?) {
                Log.i(TAG, "=====> onGetPoiIndoorResult")
            }

        })
    }

    private fun poi(keyword: String, city: String){
        Log.e(TAG, "keyword: $keyword, city: $city")
        mPoiSearch?.searchInCity(
            PoiCitySearchOption()
                .city(city) //必填
                .keyword(keyword) //必填
                .pageNum(10)
            )
    }

    fun poiSearch(keyword: String, city: String, mOnPoiResultListener: OnPoiResultListener?) {
        this.mOnPoiResultListener = mOnPoiResultListener
        if (!TextUtils.isEmpty(city)) {
            poi(keyword, city)
        }else{
            if (!TextUtils.isEmpty(locationCity)){
                poi(keyword, locationCity)
            } else {
                setLocationSwitch(true, object : OnLocationResultListener{
                    override fun result(
                        la: Double,
                        lo: Double,
                        city: String,
                        address: String,
                        desc: String
                    ) {
                        poi(keyword, city)
                    }

                    override fun fail() {

                    }
                })
            }
        }
    }

    fun searchNearby(keyword: String, la: Double, lo: Double){
        //支持多个关键字并集检索，不同关键字间以$符号分隔，最多支持10个关键字检索。如:”银行$酒店”
        mPoiSearch?.searchNearby(
            PoiNearbySearchOption()
            .location(LatLng(la, lo))
            .radius(1000)
            .keyword(keyword)
            .pageNum(10)
        )
    }


    //=========================生命周期=====================

    fun onResume(){
        mMapView?.onResume()
    }

    fun onPause(){
        mMapView?.onPause()
    }

    fun onDestroy(){
        mMapView?.onDestroy()
        mMapView = null
        mLocationClient.stop()
        mBaiduMap?.isMyLocationEnabled = false
        mPoiSearch?.destroy()
    }


    interface OnLocationResultListener {
        fun result(la: Double, lo: Double, city: String, address: String, desc: String)
        fun fail()
    }

    interface OnPoiResultListener{
        fun result(result: PoiResult)
    }
}