
package com.example.walkly

import android.os.Bundle
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

    private var pedometer:Pedometer? = null;    //歩数オブジェクト

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(mapCallback)


        //歩数計の初期化

        pedometer = Pedometer(this)

        // TODO: Context置き換え
        val button = findViewById<FloatingActionButton>(R.id.fab)
        button.setOnClickListener {
            mapApplication.handleActivityButton()
        }

    }
}
