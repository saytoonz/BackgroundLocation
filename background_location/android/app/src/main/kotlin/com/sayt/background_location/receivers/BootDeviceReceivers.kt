package com.sayt.background_location.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.sayt.background_location.services.LocationService

class BootDeviceReceivers: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        p0?.let {
            ContextCompat.startForegroundService(it, Intent(it, LocationService::class.java))
        }
    }

}