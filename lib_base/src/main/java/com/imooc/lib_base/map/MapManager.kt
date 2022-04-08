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

    private var mMapView : MapView? = null

    //初始化
    fun initMap(mContext: Context){
        SDKInitializer.setAgreePrivacy(mContext, true)
        SDKInitializer.initialize(mContext)

    }

    //绑定地图
    fun bindMapView(mMapView: MapView){
        this.mMapView = mMapView

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