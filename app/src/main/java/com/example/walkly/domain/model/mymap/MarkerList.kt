package com.example.walkly.domain.model.mymap

import com.google.android.gms.maps.model.Marker
import java.util.*

/**
 * アクティビティの目的地となるマーカー一覧を管理する
 */
class MarkerList {
    companion object {
        const val MAX_INDEX = 5
    }
    private var list: MutableList<Marker> = ArrayList()

    /**
     * マーカーをリストに追加し、もし上限以上なら最初の1つを削除する
     *
     * @param marker
     */
    fun add(marker: Marker) {
        list.add(marker)
        if (list.size > MAX_INDEX) {
            list[0].remove()
            list = list.drop(1).toMutableList()
        }
    }

    /**
     * 同じ場所のマーカーを設置しようとしている?
     * Marker.titleで判断
     *
     * @param name
     * @return Boolean
     */
     fun checkDuplicate(name: String): Boolean {
        var flag = false
        list.forEach {
            if (it.title == name) flag = true
        }
        return flag
    }

    fun delete(marker: Marker) {
        val title = marker.title
        for (i in 0 until  list.size) {
            if (list[i].title == title) {
                list[i].remove()
                list.removeAt(i)
                break
            }
        }
    }

}