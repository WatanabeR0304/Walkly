package com.example.walkly.application

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.walkly.domain.model.Activity
import com.example.walkly.domain.model.GPS
import com.example.walkly.domain.model.mymap.MyMap
import com.example.walkly.domain.model.mymap.Route
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 現在地の取得やアクティビティの開始などの指示を統括している
 */

class MapApplicationService(private val activity: AppCompatActivity) {
    private lateinit var myMap: MyMap
    private lateinit var route: Route
    private lateinit var gps: GPS
    private lateinit var mapActivity: Activity // TODO: 名前
    /**
     * マップの準備ができたら現在地を取得し、GoogleMapを保管する
     *
     * @param mMap OnMapReadyCallbackインターフェースのonMapReadyが受け取る引数
     */
    fun startUp(mMap: GoogleMap) {
        gps = GPS(activity)
        gps.enableCurrentLocation(mMap)
        myMap = MyMap(mMap)
        mapActivity = Activity(mMap)
        route = Route(mMap)
    }

    /**
     * アクティビティの開始
     */
    fun handleActivityButton() {
        mapActivity.toggleIsActivity()
        val mMap = myMap.getMyMap()
        if (mapActivity.getIsActivity()) {
            CoroutineScope(Dispatchers.Main).launch {
                val location = gps.getCurrentLocation()
                val origin = LatLng(location.latitude, location.longitude)
//                val origin = LatLng(35.1681, 136.8856) // HAL

                // TODO: チェックポイントの位置を取得する
                val place: MutableList<LatLng> = ArrayList()
                place.add(LatLng(35.1709, 136.8815)) // 名古屋駅
                place.add(LatLng(35.1700, 136.8852)) // ミッドランド
                place.add(LatLng(35.1716, 136.8863)) // ユニモール

                mMap.addMarker(MarkerOptions().position(origin))
                for (j in 0 until place.size) {
                    mMap.addMarker(MarkerOptions().position(place[j]))
                }

                route.drawRoute(origin, place)
            }
        } else {
            mMap.clear()
        }
    }

    /**
     * アクティビティを実行中ならマーカーを設置して、保存する(削除やルート用)
     * そうでなければ情報ウインドウを設置する
     *
     * @param point
     */
    fun handlePointClick(point: PointOfInterest) {
        if (mapActivity.getIsActivity()) {
            myMap.addMarker(point)
        } else {
            Toast.makeText(
                activity,
                """
            ${point.name}
            緯度:${point.latLng.latitude}
            経度:${point.latLng.longitude}
            """,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun handleMarkerClick(marker: Marker) {
    }
}