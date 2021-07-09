package com.example.walkly.domain.model

import com.android.volley.Response
import com.example.walkly.R
import com.example.walkly.lib.HTTPRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONObject

/**
 * 目的地のマーカー表示
 */
class Activity(private val mMap: GoogleMap) {
    private var isActivity: Boolean = false
    private val places: MutableList<LatLng> = ArrayList()

    fun toggleIsActivity(){
        isActivity = !isActivity
    }
    fun getIsActivity(): Boolean {
        return isActivity
    }

    fun startActivity(){
        if (true) {
            val url = createURLPlacaes(LatLng(35.1681694,136.8857641), "restaurant")
            val listener = Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response) // 文字列をJSON形式に変換
                val results = jsonResponse.getJSONArray("results") // results:配列取得
                for (i in 0 until results.length()) {
                    val item = results.getJSONObject(i) // i:オブジェクト取得
                    val geometry = item.getJSONObject("geometry") // geometry:オブジェクト取得
                    val location = geometry.getJSONObject("location") // location:オブジェクト取得

                    val options = MarkerOptions()
                    options.position(LatLng(location.getDouble("lat"), location.getDouble("lng")))
                    options.title(item.getString("name"))
                    //options.snippet("補足情報を記載")
                    val marker = mMap.addMarker(options)

                    places.add(LatLng(location.getDouble("lat"), location.getDouble("lng"))) // HAL
                }
            }
            val errorListener = Response.ErrorListener {  }
            HTTPRequest().getRequest(url, listener, errorListener)
        }
    }

    /**
     * URL作成
     *
     * @param origin 現在地
     * @param types 施設種類
     */
    private fun createURLPlacaes(origin: LatLng, types: String): String{
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=35.1681694,136.8857641&radius=2500&type=restaurant&key=${MyApplication.getContext().getString(
            R.string.google_maps_key
        )}"
    }
}