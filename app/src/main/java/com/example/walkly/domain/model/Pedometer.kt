package com.example.walkly.domain.model

import android.app.AlertDialog
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.Bundle
import androidx.core.content.ContextCompat.getSystemService
import com.example.walkly.MapsActivity
import com.example.walkly.R

class Pedometer {

    var systemService: SensorManager? = null;

    var sensor: Sensor? = null;

    init {

        // 初期化処理


        // ここに来てるかどうかを確認するために、
        // メッセージを表示してみる
        AlertDialog. Builder(MyApplication.getContext())
            .setTitle("テスト")
            .setMessage("来たよ！")
            .show()

    }

    fun getSteps() {


    }

}
   /*     fun setContentView(activityStep: Any)
        {

        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_step)

            initSensor()
        }

        private fun initSensor() {
            systemService = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensor = systemService!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        }

        override fun onResume() {
            super.onResume()
            systemService?.registerListener(this,sensor,1000);
        }

        override fun onPause() {
            super.onPause()
            systemService?.unregisterListener(this)
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent?) {
            val values = event?.values
            step.text = "Step number =" + values ​​!! [0]

        }
    }


}

    */