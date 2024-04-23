package com.example.n_vibetest.utils

import android.content.Context
import com.example.n_vibetest.R
import com.google.android.gms.maps.model.LatLng

object Constants {
    fun getGoogleServicesApiKey(context: Context): String {
        return context.getString(R.string.google_services_api_key)
    }
    const val DEFAULT_ZOOM_LEVEL = 12f
    val DEFAULT_POSITION = LatLng(48.8566, 2.3522)
}