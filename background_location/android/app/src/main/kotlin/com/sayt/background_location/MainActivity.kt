package com.sayt.background_location

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sayt.background_location.helpers.LocationHelper
import com.sayt.background_location.helpers.MyLocationListener
import com.sayt.background_location.services.LocationService
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import java.lang.Exception

class MainActivity : FlutterActivity() {
    private val METHOD_CHANNEL_NAME = "com.sayt.background_location/method"
    private val START_LOCATION_CHANNEL_NAME = "com.sayt.background_location/startLocation"
    private val STOP_LOCATION_CHANNEL_NAME = "com.sayt.background_location/stopLocation"
    private val locationHelper: LocationHelper = LocationHelper()

    private var methodChannel: MethodChannel? = null

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        setUpChannel(this, flutterEngine.dartExecutor.binaryMessenger)
    }


    private fun setUpChannel(context: Context, messenger: BinaryMessenger) {
        methodChannel = MethodChannel(messenger, METHOD_CHANNEL_NAME)
        methodChannel!!.setMethodCallHandler { call, result ->
            when (call.method) {
                "requestPermissions" -> {
                    print("requestPermissions")
                    checkPermission()
                    result.success("requestPermissions")
                }
                "startLocationService" -> {
                    startLocationService(call.argument("postUrl")!!)
//                    result.success(call.argument("postUrl"))
                    result.success("startLocationService")
                }
                "stopLocationService" -> {
                    print("stopLocationService")
                    stopLocationService()
                    result.success("stopLocationService")
                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }


    private fun startLocationService(postUrl: String) {
        if (!checkPermission()) return;
        if (!this.isMyServiceRunning(LocationService::class.java)) {
            val intent = Intent(this, LocationService::class.java)
            intent.putExtra("postUrl", postUrl)
            ContextCompat.startForegroundService(this, intent)
            locationHelper.startListeningUserLocation( this , object:MyLocationListener {
                override fun onLocationChanged(location: Location?) {
                      location?.let {
//                          Log.d("TAG", "startLocationService: location $location")
//                          Log.d("TAG", "startLocationService: Latitude ${location.latitude} , Longitude ${location.longitude}")
//                          Log.d("TAG", "startLocationService: Bearing ${location.bearing} , Speed ${location.speed}")
//                          Log.d("TAG", "On interface")
                      }
                 }
            })
        }
    }

    private fun stopLocationService() {

        try {
            locationHelper.stopListeningUserLocation()
            stopService(Intent(this, LocationService::class.java))
        } catch (e: Exception) {
            print(e.stackTrace);
        }

    }

    private fun Context.isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == serviceClass.name }
    }


    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val result1 = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val result2 =
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        if (!result2) {
            requestPermission()
        }
        return result2
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1
        )
    }
}
