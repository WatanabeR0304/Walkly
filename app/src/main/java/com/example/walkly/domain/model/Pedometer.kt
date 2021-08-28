package com.example.walkly.domain.model

import android.app.AlertDialog
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.core.content.ContextCompat.getSystemService
import com.example.walkly.MapsActivity
import com.example.walkly.R

class Pedometer ( var mContext: Context ): SensorEventListener {

    //センサ類
    var systemService: SensorManager? = null;
    var sensor: Sensor? = null;

    //歩数カウンタ類
    private var mActive = true    //歩数計アクティブ状態
    private var mAccumStepCount: Long = 0        //計測している累計歩数
    private var mPrevStep: Long = 0        //前回のHW累計歩数(0の場合初回)
    private var mAccumTimeSec: Long = 0        //計測している累計秒数
    private var mPrevTime: Long = 0        //前回のタイムスタンプ[秒](0の場合初回)

    //カロリー計算用
    private var mMomentum:Double = 3.0     //運動強度[METS] 3は一般的な散歩での値
    private var mWeight:Double   = 65.0    //体重[kg]
    private var mCalorie:Double  = 0.0     //歩いて消費したカロリー[kcal]

    init {

        AlertDialog.Builder(mContext)
            .setTitle("初期化")
            .setMessage("来たよ！")
            .show()

        //センサの取得
        //(mContext.getSystemService は MyApplication.getContext()getSystemService でも動きます)
        systemService = mContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = systemService!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        //センサにイベントリスナを登録
        var ret: Boolean = false;
        ret = systemService!!.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        var a: Int;
        a = 0       //ブレークポイントを張るためのダミー

    }

    public fun resetStepCount() {
        //歩数カウンタ類をリセット
        mAccumStepCount = 0
        mPrevStep = 0
        mAccumTimeSec = 0
        mPrevTime = 0
    }

    public fun getSteps(): Long {
        //累計歩数を返します
        return mAccumStepCount
    }

    public fun getCalorie():Double {
        //消費カロリーを返します
        return mCalorie
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //特に何もしないです
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val sensor: Sensor?     = event?.sensor
        val values:  FloatArray? = event?.values
        val timestamp: Long     = event?.timestamp ?: 0

        /*
        AlertDialog.Builder(mContext)
            .setTitle("歩数")
            .setMessage("歩数に来たよ！")
            .show()

         */

        if ((sensor != null) && (values != null)) {
            if (sensor.type == Sensor.TYPE_STEP_COUNTER) {
                if (mActive) {
                    //HW累計歩数と時刻を取得
                    val hwstep = values[0].toLong()
                    val time = timestamp / 1000000000 //[ns]->[sec]

                    //差分を取得
                    if (mPrevStep == 0L) {
                        //初回なのでHW累計歩数を覚える
                        mPrevStep = hwstep
                        mPrevTime = time
                    } else {
                        //HW累計歩数と時刻の差分を計測している累計歩数と時間に加算
                        mAccumStepCount += hwstep - mPrevStep
                        mPrevStep = hwstep

                        //加算する差分時間を計算
                        var addSec = time - mPrevTime

                        //60秒以上差がある場合は、休憩していたと
                        //みなして、時間は加算しません
                        if( addSec >= 60 ) {
                            addSec = 0
                        }

                        mAccumTimeSec += addSec
                        mPrevTime = time

                        //ここまでの運動時間から消費カロリーを計算
                        //※計算式は下記のサイトを参考にしました
                        //https://news.mynavi.jp/article/nadeshiko-48/
                        //
                        // 消費カロリー[kcal] = 3[メッツ] × 体重[kg] × 運動時間[時] × 1.05
                        //
                        mCalorie = mMomentum * mWeight * (mAccumTimeSec.toDouble() / 3600.0) * 1.05


                        //確認のための表示
                        if (mAccumStepCount == (10).toLong())
                        {
                            AlertDialog.Builder(mContext)
                                .setTitle("累計歩数")
                                .setMessage("10歩！")
                                .show()
                        }
                    }
                }
            }
        }
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