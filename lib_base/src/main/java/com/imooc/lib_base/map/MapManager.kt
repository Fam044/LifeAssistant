package com.imooc.lib_base.map

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.poi.*
import com.baidu.mapapi.search.route.*
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener

import com.baidu.mapapi.walknavi.WalkNavigateHelper
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError

import androidx.core.content.ContextCompat.startActivity
import com.baidu.mapapi.search.geocode.GeoCodeResult
import com.baidu.mapapi.search.geocode.GeoCoder
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult

import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener
import com.imooc.lib_base.helper.ARouterHelper
import com.baidu.mapapi.search.geocode.GeoCodeOption
import com.baidu.mapapi.search.route.PlanNode
import com.baidu.mapapi.search.route.WalkingRoutePlanOption
import androidx.core.content.ContextCompat.startActivity
import com.baidu.mapapi.walknavi.params.RouteNodeType
import com.baidu.mapapi.walknavi.params.WalkRouteNodeInfo
import androidx.core.content.ContextCompat.startActivity

import com.baidu.mapapi.walknavi.adapter.IWNaviCalcRouteListener
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.ContextCompat.startActivity
import com.imooc.lib_base.R
import com.imooc.lib_base.utils.L


object MapManager {

    const val TAG = "MapManager"

    //最大缩放等级  4-21
    const val MAX_ZOOM:Float = 17f

    private var mMapView : MapView? = null
    private var mBaiduMap: BaiduMap? = null
    private var mPoiSearch: PoiSearch? = null

    private lateinit var mContext: Context

    //用户所在城市
    private var locationCity: String = ""

    //定位客户端
    private lateinit var mLocationClient: LocationClient

    //步行规划
    private lateinit var mSearch: RoutePlanSearch

    //地理编码对象
    private lateinit var mCoder: GeoCoder

    //定位对外的回调
    private var mOnLocationResultListener: OnLocationResultListener? = null

    //POI对外的回调
    private var mOnPoiResultListener: OnPoiResultListener? = null

    //编码对外的回调
    private var mOnCodeResultListener: OnCodeResultListener? = null

    //导航对外的回调
    private var mOnNaviResultListener: OnNaviResultListener? = null

    //开始位置
    private var startLa: Double = 0.0
    private var startLo: Double = 0.0

    //结束位置
    private var endAddress: String = ""
    private var endCity: String = ""

    //初始化
    fun initMap(mContext: Context){
        this.mContext = mContext

        SDKInitializer.setAgreePrivacy(mContext, true)
        SDKInitializer.initialize(mContext)

        LocationClient.setAgreePrivacy(true)
        mLocationClient = LocationClient(mContext)

        //初始化POI
        initPoi()
        //初始化定位
        initLocation()
        //初始化编码
        initCode()
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
        //初始化步行监听
        initWalkingRoute()
        //初始化监听
        initListener()

        //比例尺
        showScaleControl(true)
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
    private fun setMyLocationEnabled(isOpen: Boolean){
//        mBaiduMap?.isMyLocationEnabled = isOpen
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
                    L.i("定位失败原因：${location.locType}")
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

    private fun poi(keyword: String, city: String, size: Int){
        Log.e(TAG, "keyword: $keyword, city: $city")
        mPoiSearch?.searchInCity(
            PoiCitySearchOption()
                .city(city) //必填
                .keyword(keyword) //必填
                .pageCapacity(size)
            )
    }

    fun poiSearch(keyword: String, city: String, size: Int, mOnPoiResultListener: OnPoiResultListener?) {
        this.mOnPoiResultListener = mOnPoiResultListener
        if (!TextUtils.isEmpty(city)) {
            poi(keyword, city, size)
        }else{
            if (!TextUtils.isEmpty(locationCity)){
                poi(keyword, locationCity, size)
            } else {
                setLocationSwitch(true, object : OnLocationResultListener{
                    override fun result(
                        la: Double,
                        lo: Double,
                        city: String,
                        address: String,
                        desc: String
                    ) {
                        poi(keyword, city, size)
                    }

                    override fun fail() {

                    }
                })
            }
        }
    }

    fun searchNearby(keyword: String, la: Double, lo: Double, size: Int, mOnPoiResultListener: OnPoiResultListener?){
        this.mOnPoiResultListener = mOnPoiResultListener
        //支持多个关键字并集检索，不同关键字间以$符号分隔，最多支持10个关键字检索。如:”银行$酒店”
        mPoiSearch?.searchNearby(
            PoiNearbySearchOption()
            .location(LatLng(la, lo))
            .radius(1000)
            .keyword(keyword)
            .pageCapacity(size)
        )
    }


    //==========================路线规划========================
    //初始化步行规划
    private fun initWalkingRoute(){
        mSearch = RoutePlanSearch.newInstance()
        mSearch.setOnGetRoutePlanResultListener(object : OnGetRoutePlanResultListener{

            override fun onGetWalkingRouteResult(walkingRouteResult: WalkingRouteResult?) {
                //创建WalkingRouteOverlay实例
                val overlay = WalkingRouteOverlay(mBaiduMap)
                walkingRouteResult?.let {
                    if (it.routeLines != null){
                        if (it.routeLines.size > 0){
                            //获取路径规划数据,(以返回的第一条数据为例)
                            //为WalkingRouteOverlay实例设置路径数据
                            overlay.setData(walkingRouteResult.routeLines[0])
                            //在地图上绘制WalkingRouteOverlay
                            overlay.addToMap()
                            overlay.zoomToSpan()
                            Log.i(TAG, "suggestAddrInfo: ${walkingRouteResult.suggestAddrInfo}, " +
                                    "taxiInfo: ${walkingRouteResult.taxiInfo}, " +
                                    "error: ${walkingRouteResult.error}")

                            //3秒后自动开始导航
//                            mOnNaviResultListener?.onStartNavi(startLa, startLo, endCity, endAddress)
                        }else{
                            Log.i(TAG, "线路为0")
                        }
                    }else{
                        Log.i(TAG, "线路为空")
                        Log.i(TAG, "suggestAddrInfo: ${walkingRouteResult.suggestAddrInfo}, " +
                                "taxiInfo: ${walkingRouteResult.taxiInfo}, " +
                                "error: ${walkingRouteResult.error}")
                    }
                }
            }

            override fun onGetTransitRouteResult(p0: TransitRouteResult?) {}
            override fun onGetMassTransitRouteResult(p0: MassTransitRouteResult?) {}
            override fun onGetDrivingRouteResult(p0: DrivingRouteResult?) {}
            override fun onGetIndoorRouteResult(p0: IndoorRouteResult?) {}
            override fun onGetBikingRouteResult(p0: BikingRouteResult?) {}

        })
    }

    //以自身的位置开始去进行（步行）路线规划
    fun startLocationWalkingSearch(toAddress: String, mOnNaviResultListener: OnNaviResultListener){
        this.mOnNaviResultListener = mOnNaviResultListener

        setLocationSwitch(true, object : OnLocationResultListener{
            override fun result(
                la: Double,
                lo: Double,
                city: String,
                address: String,
                desc: String
            ) {
                Log.i(TAG, "定位成功, address: $address, desc: $desc")
                startLa = la
                startLo = lo
                endAddress = toAddress
                endCity = city

                setCenterMap(la, lo)
                startWalkingSearch(city, desc, city, toAddress)
            }

            override fun fail() {
                Log.e(TAG, "定位失败")
            }
        })
    }

    //已有数据的情况下开始步行规划
    fun startWalkingSearch(fromCity: String, fromAddress: String, toCity: String, toAddress: String){
        Log.i(
            "MapManager",
            "fromCity: $fromCity, fromAddress: $fromAddress, toCity: $toCity, toAddress: $toAddress"
        )
        val stNode = PlanNode.withCityNameAndPlaceName(fromCity, fromAddress)
        val enNode = PlanNode.withCityNameAndPlaceName(toCity, toAddress)

        //发起路线规划
        mSearch.walkingSearch(
            WalkingRoutePlanOption()
                .from(stNode)
                .to(enNode)
        )
    }

    //==========================导航=======================
    fun initNaviEngine(mActivity: Activity, startLa: Double, startLo: Double, endLa: Double, endLo: Double){
        // 获取导航控制类
        // 引擎初始化
        WalkNavigateHelper.getInstance().initNaviEngine(mActivity, object : IWEngineInitListener {
            override fun engineInitSuccess() {
                //引擎初始化成功的回调
                Log.i(TAG, "进入到initNaviEngine -> engineInitSuccess")
                routeWalkPlanWithParam(startLa,startLo, endLa, endLo)
//                routePlanWithRouteNode()
                Log.i(TAG, "导航引擎初始化成功, startLa: $startLa, startLo: $startLo, endLa: $endLa, endLo: $endLo")
            }

            override fun engineInitFail() {
                //引擎初始化失败的回调
                Log.i(TAG, "导航引擎初始化失败")
            }
        })
    }

    //室内步行导航
    private fun routePlanWithRouteNode() {
        Log.i(TAG, "调用routePlanWithRouteNode")
        //起终点位置
        val startPt = LatLng(40.056015,116.3078)// 百度大厦
        //WalkRouteNodeInfo walkStartNode = new WalkRouteNodeInfo();
        val walkStartNode = WalkRouteNodeInfo()
        walkStartNode.keyword = "百度大厦"
        walkStartNode.location = startPt
        walkStartNode.type = RouteNodeType.KEYWORD
        walkStartNode.citycode = 131

        val endPt = LatLng(40.035919,116.339863)
//        WalkRouteNodeInfo walkEndNode = new WalkRouteNodeInfo();
        val walkEndNode = WalkRouteNodeInfo()
        walkEndNode.location = endPt
        walkEndNode.type = RouteNodeType.KEYWORD
        walkEndNode.keyword = "麻辣诱惑(五彩城店)"
//        walkEndNode.buildingID = "1260176407175102463"
//        walkEndNode.floorID = "F4"
        walkEndNode.citycode = 131
        val walkParam = WalkNaviLaunchParam().startNodeInfo(walkStartNode).endNodeInfo(walkEndNode)

        Log.i(TAG, "startPt: $startPt, endPt: $endPt")
        Log.i(TAG, "walkParam: $walkParam, walkParam.startNodeInfo: ${walkParam.startNodeInfo}," +
                " walkParam.endNodeInfo: ${walkParam.endNodeInfo}, " +
                "walkParam.extraNaviMode: ${walkParam.extraNaviMode}")

        //发起路线规划
        WalkNavigateHelper.getInstance()
            .routePlanWithRouteNode(walkParam, object : IWRoutePlanListener {
                override fun onRoutePlanStart() {
                    //开始算路的回调
                    Log.i(TAG, "开始算路的回调")
                }

                override fun onRoutePlanSuccess() {
                    //算路成功
                    Log.i(TAG, "开始算路的回调成功")
                    WalkNavigateHelper.getInstance()
                        .naviCalcRoute(0, object : IWNaviCalcRouteListener {
                            override fun onNaviCalcRouteSuccess() {
                                Log.d(TAG, "WalkNavi naviCalcRoute success")
                                ARouterHelper.startActivity(ARouterHelper.PATH_MAP_NAVI)
                            }

                            override fun onNaviCalcRouteFail(error: WalkRoutePlanError) {
                                Log.d(TAG, "WalkNavi naviCalcRoute fail")
                            }
                        })
                }

                override fun onRoutePlanFail(walkRoutePlanError: WalkRoutePlanError) {
                    //算路失败的回调
                    Log.i(TAG, "开始算路的回调失败, walkRoutePlanError: $walkRoutePlanError")
                }
            })

    }

//    private fun naviCalcRoute(routeIndex: Int) {
//        WalkNavigateHelper.getInstance()
//            .naviCalcRoute(routeIndex, object : IWNaviCalcRouteListener {
//                override fun onNaviCalcRouteSuccess() {
//                    Log.d(TAG, "WalkNavi naviCalcRoute success")
//                    ARouterHelper.startActivity(ARouterHelper.PATH_MAP_NAVI)
//                }
//
//                override fun onNaviCalcRouteFail(error: WalkRoutePlanError) {
//                    Log.d(TAG, "WalkNavi naviCalcRoute fail")
//                }
//            })
//    }

    //配置导航参数
    fun routeWalkPlanWithParam(startLa: Double, startLo: Double, endLa: Double, endLo: Double) {
        Log.i(TAG, "调用routeWalkPlanWithParam")
        //起终点位置
        val startPt = LatLng(startLa,startLo)
        val endPt = LatLng(endLa, endLo)
        //构造WalkNaviLaunchParam
        val mParam = WalkNaviLaunchParam().stPt(startPt).endPt(endPt)
        Log.i(TAG, "startPt: $startPt, endPt: $endPt")
        Log.i(TAG, "mParam: $mParam, mParam.startNodeInfo: ${mParam.startNodeInfo}," +
                " mParam.endNodeInfo: ${mParam.endNodeInfo}, " +
                "mParam.extraNaviMode: ${mParam.extraNaviMode}")


        Log.i(TAG, "即将调用WalkNavigateHelper.getInstance().routePlanWithParams")
        //发起算路
        WalkNavigateHelper.getInstance().routePlanWithParams(mParam, object : IWRoutePlanListener {
            override fun onRoutePlanStart() {
                //开始算路的回调
                Log.i(TAG, "开始算路的回调")
            }

            override fun onRoutePlanSuccess() {
                Log.i(TAG, "开始算路的回调成功")
                //算路成功
                //跳转至诱导页面
                ARouterHelper.startActivity(ARouterHelper.PATH_MAP_NAVI)
            }

            override fun onRoutePlanFail(walkRoutePlanError: WalkRoutePlanError) {
                //算路失败的回调
                Log.i(TAG, "开始算路的回调失败, $walkRoutePlanError," +
                        " walkRoutePlanError.name: ${walkRoutePlanError.name}," +
                        " walkRoutePlanError.declaringClass: ${walkRoutePlanError.declaringClass}")
            }
        })

    }

    //========================事件交互======================

    //设置Logo显示的位置
    fun setLogoPosition(){
        mMapView?.logoPosition = LogoPosition.logoPostionCenterTop
    }

    //指南针
    fun isCompassEnabled(enabled: Boolean){
        //实例化UiSettings类对象
        val mUiSettings = mBaiduMap?.uiSettings
        //通过设置enable为true或false 选择是否显示指南针
        mUiSettings?.isCompassEnabled = enabled
    }

    //比例尺
    fun showScaleControl(enabled: Boolean){
        //通过设置enable为true或false 选择是否显示比例尺
        mMapView?.showScaleControl(enabled)
    }

    //缩放按钮
    fun showZoomControls(enable: Boolean){
        //通过设置enable为true或false 选择是否显示缩放按钮
        mMapView?.showZoomControls(enable)
    }

    //截图
    fun snapshot(){
        mBaiduMap?.snapshot { L.i("=====> 截图完成") }
    }

    //添加覆盖物
    fun addMarker(lat: LatLng) {
        //创建marker
        val ooA = MarkerOptions().position(lat).icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(mContext.resources, R.drawable.img_my_location)
            )
        )

        mBaiduMap?.let {
            it.clear()
            val mMarkerA = it.addOverlay(ooA)
        }
    }

    private fun initListener(){
        //单击
        mBaiduMap?.setOnMapClickListener(object : BaiduMap.OnMapClickListener{
            override fun onMapClick(p0: LatLng?) {
                L.i("单击")
            }

            override fun onMapPoiClick(p0: MapPoi?) {
                L.i("POI 单击")
            }
        })

        //双击
        mBaiduMap?.setOnMapDoubleClickListener(BaiduMap.OnMapDoubleClickListener { L.i("双击") })

        //长按
        mBaiduMap?.setOnMapLongClickListener { L.i("长按") }
    }


    //========================地理编码======================

    //初始化地理编码
    private fun initCode(){
        mCoder = GeoCoder.newInstance()
        mCoder.setOnGetGeoCodeResultListener(object : OnGetGeoCoderResultListener{
            override fun onGetGeoCodeResult(geoCodeResult: GeoCodeResult?) {
                Log.i(TAG, "正编码成功")
                //正编码
                if (null != geoCodeResult && null != geoCodeResult.location) {
                    if (geoCodeResult.error !== SearchResult.ERRORNO.NO_ERROR) {
                        /**
                         * == 相当于java中的 equals 方法
                         * === 相当于java中的 ==
                         */
                        //没有检索到结果
                        return
                    } else {
                        val latitude = geoCodeResult.location.latitude
                        val longitude = geoCodeResult.location.longitude
                        Log.i(TAG, "正编码成功: $latitude, $longitude")
                        mOnCodeResultListener?.result(latitude, longitude)
                    }
                }
            }

            override fun onGetReverseGeoCodeResult(p0: ReverseGeoCodeResult?) {
                //逆编码
            }

        })
    }

    //开始正编码
    fun startCode(city: String, address: String, mOnCodeResultListener: OnCodeResultListener){
        this.mOnCodeResultListener = mOnCodeResultListener
        mCoder.geocode(
            GeoCodeOption()
                .city(city)
                .address(address)
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
        mSearch.destroy()
    }

    //===========================接口=========================
    interface OnLocationResultListener {
        fun result(la: Double, lo: Double, city: String, address: String, desc: String)
        fun fail()
    }

    interface OnPoiResultListener{
        fun result(result: PoiResult)
    }

    interface OnCodeResultListener{
        fun result(codeLa: Double, codeLo: Double)
    }

    interface OnNaviResultListener{
        fun onStartNavi(startLa: Double, startLo: Double, endCity: String, address: String)
    }
}