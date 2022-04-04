package com.sayt.background_location.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sayt.background_location.R
import com.sayt.background_location.api.CallAPI
import com.sayt.background_location.api.Empty
import com.sayt.background_location.helpers.LocationHelper
import com.sayt.background_location.helpers.MyLocationListener
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection


class LocationService : Service() {


    private var isServiceStarted = false
    private val NOTIFICATION_CHANNEL_ID = "my_notification_location"
    private val TAG = "LocationService"
    private var canHitEndPoint = true

    var mLocation: Location? = null

    override fun onCreate() {
        super.onCreate()

        isServiceStarted = true
        canHitEndPoint = true

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(false)
                .setSmallIcon(R.drawable.launch_background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description = NOTIFICATION_CHANNEL_ID
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
            startForeground(1, builder.build())
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val postUrl = intent?.getStringExtra("postUrl")

        LocationHelper().startListeningUserLocation(
            this, object : MyLocationListener {
                override fun onLocationChanged(location: Location?) {
                    mLocation = location
                    mLocation?.let {
                        if (!canHitEndPoint || postUrl == null || postUrl == "") return

                        val postData = JSONObject()
                        try {
                            postData.put("lat", it.latitude) // A sample POST field
                            postData.put("lng", it.longitude) // Another sample POST field
                            postData.put("heading", it.bearing)
                            Log.d(TAG, "onLocationChanged: location $it")

                            Empty(postUrl).execute(
//                                postUrl,
                                postData.toString()
                            )

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

//                        postUrl = "$postUrl?lat=${it.latitude}&lng=${it.longitude}&heading=${it.bearing}/"

//                        AppExecutors.instance?.networkIO()?.execute {
//                            val apiClient = ApiClient.getInstance(this@LocationService, postUrl!!)
//                                .create(ApiClient::class.java)
//                            val response = apiClient.updateLocation()
//                            response.enqueue(object : Callback<LocationResponse> {
//                                override fun onResponse(
//                                    call: Call<LocationResponse>,
//                                    response: Response<LocationResponse>
//                                ) {
//                                    Log.d(TAG, "onLocationChanged: location $it")
//                                    Log.d(TAG, "onLocationChanged: Latitude ${it.latitude} , Longitude ${it.longitude}")
//                                    Log.d(TAG, "onLocationChanged: Bearing ${it.bearing} , Speed ${it.speed}")
//                                    Log.d(TAG, "run: Running = Location Update Successful")
//                                }
//
//                                override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
//                                    Log.d(TAG, "run: Running = Location Update Failed")
//
//                                }
//                            })
//
//                        }
//

                    }
                }
            })
        return START_STICKY;
    }


    fun performPostCall(
        requestURL: String?,
        postDataParams: HashMap<String?, String?>
    ): String? {
        val url: URL
        var response: String? = ""
        try {
            url = URL(requestURL)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.setReadTimeout(15000)
            conn.setConnectTimeout(15000)
            conn.setRequestMethod("GET")
            conn.setDoInput(true)
            conn.setDoOutput(true)
            val os: OutputStream = conn.getOutputStream()
            val writer = BufferedWriter(
                OutputStreamWriter(os, "UTF-8")
            )
            writer.write(getPostDataString(postDataParams))
            writer.flush()
            writer.close()
            os.close()
            val responseCode: Int = conn.getResponseCode()
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                var line: String?
                val br = BufferedReader(InputStreamReader(conn.getInputStream()))
                while (br.readLine().also { line = it } != null) {
                    response += line
                }
            } else {
                response = ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    private fun getPostDataString(params: HashMap<String?, String?>): String {
        val result = StringBuilder()
        var first = true
        for ((key, value) in params.entries) {
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value, "UTF-8"))
        }
        return result.toString()
    }


    override fun onDestroy() {
        super.onDestroy()
        canHitEndPoint = false
        isServiceStarted = false
        LocationHelper().stopListeningUserLocation()
    }


    companion object {
        var mLocation: Location? = null
        var isServiceStarted = false
    }
}