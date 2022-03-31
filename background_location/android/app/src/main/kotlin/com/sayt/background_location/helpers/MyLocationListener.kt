package com.sayt.background_location.helpers

import android.location.Location

interface MyLocationListener {
    fun onLocationChanged(location: Location?)
}
