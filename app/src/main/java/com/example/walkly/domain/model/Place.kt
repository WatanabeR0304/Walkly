package com.example.walkly.domain.model

import com.android.volley.Response
import com.example.walkly.BuildConfig
import com.example.walkly.R
import com.example.walkly.lib.HTTPRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONObject
import java.util.Random
import kotlin.Exception
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 周辺施設を取得する
 */
class Place(private val mMap: GoogleMap) {
    companion object {
        const val PLACE_RADIUS = 2500
        const val MAX_CHECKPOINT = 5

        const val DEBUG_MODE = true // TODO: 本番時には消す
    }

    private val places: MutableList<LatLng> = ArrayList()

    /**
     * 周辺施設の位置情報を取得する
     *
     * @param origin 現在地
     * @throws Exception APIキーが間違っているなどのエラー
     */
    suspend fun pickCheckpoint(origin: LatLng): MutableList<LatLng> {
        return suspendCoroutine { continuation ->
            // TODO: 本番時には消す & リリースビルド時にtrueになることを確認する
            if (!BuildConfig.DEBUG || DEBUG_MODE) {
                places.clear()
                val url = createURL(origin)
                val listener = Response.Listener<String> { response ->
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getString("status") != "OK") {
                        throw Exception(jsonResponse.getString("error_message"))
                    }

                    val results = jsonResponse.getJSONArray("results")
                    val targetIndex = createRandomIndex(results.length())

                    for (i in 0 until results.length()) {
                        /**
                         * ランダムに生成されたtargetIndexの中にiが含まれていたら
                         */
                        if (targetIndex.contains(i)) {
                            val item = results.getJSONObject(i)
                            val geometry = item.getJSONObject("geometry")
                            val location = geometry.getJSONObject("location")

                            val latLng =
                                LatLng(location.getDouble("lat"), location.getDouble("lng"))
                            places.add(latLng)

                            val options = MarkerOptions()
                            options.position(latLng)
                            options.title("[checkpoint]"+ item.getString("name"))
                            options.icon(
                                BitmapDescriptorFactory.defaultMarker(160F)
                            )
                            mMap.addMarker(options)
                        }
                    }
                    continuation.resume(places)
                }
                val errorListener = Response.ErrorListener {
                    continuation.resume(places)
                }
                HTTPRequest().getRequest(url, listener, errorListener)
            } else {
                // TODO: 本番時には消す
                places.clear()
                places.add(LatLng(35.1709, 136.8815)) // 名古屋駅
                places.add(LatLng(35.1700, 136.8852)) // ミッドランド
                places.add(LatLng(35.1716, 136.8863)) // ユニモール
                continuation.resume(places)
            }
        }
    }

    /**
     * Places API用のURL作成
     *
     * @param origin 現在地
     * @return String API URL
     */
    private fun createURL(origin: LatLng): String {
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?language=ja&location=${origin.latitude},${origin.longitude}&radius=${PLACE_RADIUS}&type=${createRandomType()}&key=${
            MyApplication.getContext().getString(
                R.string.google_maps_key
            )
        }"
    }

    /**
     * 周辺施設の取得対象となる施設タイプをランダムに生成
     * API用
     *
     * @return String 施設タイプ
     */
    private fun createRandomType(): String {
        val list: List<String> = listOf("restaurant", "bakery", "cafe", "museum", "park", "place_of_worship", "spa")
        val randomIndex = Random().nextInt(list.size)
        return list[randomIndex]
    }

    /**
     * ランダム習得する対象となるindexを格納した、リストの作成
     *
     * @param size
     * @return 最大${MAX_CHECKPOINT}個だけ格納されたリスト
     */
    private fun createRandomIndex(size: Int): List<Int> {
        val list: MutableList<Int> = ArrayList()
        for (i in 0 until size) {
            list.add(i)
        }
        val randomIndex = list.shuffled()
        return randomIndex.take(MAX_CHECKPOINT)
    }
}