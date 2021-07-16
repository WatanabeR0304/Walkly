package com.example.walkly.domain.model

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import com.example.walkly.lib.Permission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await

/**
 * GPS利用の許可を求めたり、現在地レイヤーを表示したりする
 */

class GPS(appActivity: AppCompatActivity) {
    private val activity: AppCompatActivity = appActivity
    private val permission: Permission = Permission(activity)
    private lateinit var lastLocation: Location
    private  val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)

    /**
     * 現在地レイヤーの表示
     *
     * @param mMap GoogleMap SDK のメインクラス
     */
    fun enableCurrentLocation(mMap: GoogleMap) {
        mMap.isMyLocationEnabled = permission.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        fusedLocationClient.lastLocation.addOnSuccessListener(activity){location ->
            if(location != null){
                lastLocation = location
                val  currentLatLong = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 18f))
            }
        }
    }

    /**
     * 現在地を取得する
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location {
            return fusedLocationClient.lastLocation.await()
    }

}