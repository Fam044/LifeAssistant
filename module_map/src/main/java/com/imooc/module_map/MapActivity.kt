package com.imooc.module_map

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.poi.PoiResult
import com.imooc.lib_base.base.BaseActivity
import com.imooc.lib_base.helper.ARouterHelper
import com.imooc.lib_base.map.MapManager
import com.imooc.lib_base.utils.L
import com.yanzhenjie.permission.Action
import kotlinx.android.synthetic.main.activity_map.*

@Route(path = ARouterHelper.PATH_MAP)
class MapActivity : BaseActivity() {

    //权限
    private val permission = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun getLayoutId(): Int {
        return R.layout.activity_map
    }

    override fun getTitleText(): String {
        return getString(com.imooc.lib_base.R.string.app_title_map)
    }

    override fun initView() {
        MapManager.bindMapView(mMapView)

        //动态权限
        if (checkPermission(permission)){
            startLocation()
        }else{
            requestPermission(permission, Action<List<String>> { startLocation() })
        }
    }

    private fun startLocation(){
        MapManager.setLocationSwitch(true, object : MapManager.OnLocationResultListener{
            override fun result(la: Double, lo: Double, city: String, address: String, desc: String) {
                MapManager.setCenterMap(la, lo)
                MapManager.searchNearby("美食", la, lo)
                L.i("定位成功: $address, desc: $desc")
            }

            override fun fail() {
                L.i("定位失败")
            }
        })

    }

    override fun isShowBack(): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()
        MapManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        MapManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        MapManager.onDestroy()
    }
}