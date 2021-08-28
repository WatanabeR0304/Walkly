
package com.example.walkly

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.walkly.application.MapApplicationService
import com.example.walkly.domain.model.Pedometer
import com.example.walkly.ui.MapCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapsActivity : AppCompatActivity() {

    private val mapApplication: MapApplicationService = MapApplicationService(this)
    private val mapCallback: MapCallback = MapCallback(mapApplication)

    //画面に仮表示するための仕組み用
    val handler = Handler(Looper.getMainLooper())
    private val run = object: Runnable {
        override fun run() {
            //歩数を更新
            val txtStep = findViewById<View>(R.id.textStep) as TextView
            txtStep.text = pedometer?.getSteps().toString() + "歩"

            //カロリーを更新
            val txtCal = findViewById<View>(R.id.textCal) as TextView
            val calorie:Double? = pedometer?.getCalorie()
            ("%.2f".format(calorie))     //3.14
            txtCal.text = "%.2f".format(calorie) + "kcal"


            //setTitle("歩数:"+txtStep.text+"カロリー:"+txtCal.text)
            handler.postDelayed(this,500)

        }
    }

    private var pedometer:Pedometer? = null;    //歩数オブジェクト

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(mapCallback)

        //定期処理を登録
        handler.post(run)

        //歩数計の初期化
        pedometer = Pedometer(this)

        // TODO: Context置き換え
        val button = findViewById<FloatingActionButton>(R.id.fab)
        button.setOnClickListener {
            mapApplication.handleActivityButton()
        }

    }
}
