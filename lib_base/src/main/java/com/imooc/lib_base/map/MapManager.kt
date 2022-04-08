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

object MapManager {

    //最大缩放等级  4-21
    private const val MAX_ZOOM:Float = 18f

    private var mMapView : MapView? = null

    private lateinit var mBaiduMap: BaiduMap

    //初始化
    fun initMap(mContext: Context){
        SDKInitializer.setAgreePrivacy(mContext, true)
        SDKInitializer.initialize(mContext)
    }

    //绑定地图
    fun bindMapView(mMapView: MapView){
        this.mMapView = mMapView
        mBaiduMap = mMapView.map

        //默认缩放
        zoomMap(MAX_ZOOM)
        //默认卫星地图
        setMapType(1)
        //默认打开实时路况
        setTrafficEnabled(true)
        //默认打开热力图
        setBaiduHeatMapEnabled(true)
    }

    //========================操作方法=====================
    //缩放地图
    fun zoomMap(value: Float){
        val builder = MapStatus.Builder()
        builder.zoom(value)
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
    }

    //设置默认中心点
    fun setCenterMap(la: Double, lo: Double){
        val latLng = LatLng(la, lo)
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng))
    }

    /**
     * 0: MAP_TYPE_NORMAL 普通地图（含3D地图）
     * 1: MAP_TYPE_SATELLITE 卫星图
     */
    fun setMapType(index: Int){
        mBaiduMap.mapType = if (index == 0) BaiduMap.MAP_TYPE_NORMAL else BaiduMap.MAP_TYPE_SATELLITE
    }

    //设置实时路况
    fun setTrafficEnabled(isOpen: Boolean){
        mBaiduMap.isTrafficEnabled = isOpen
    }

    //设置百度热力图
    fun setBaiduHeatMapEnabled(isOpen: Boolean){
        mBaiduMap.isBaiduHeatMapEnabled = isOpen
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

    }
}