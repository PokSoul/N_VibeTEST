package com.example.n_vibetest

import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.errors.ApiException
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import java.io.IOException

class DirectionService(private val apiKey: String) {
    // Méthode pour obtenir les directions entre deux points
    fun getDirections(start: String, end: String): DirectionsResult? {
        val context = GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()

        try {
            return DirectionsApi.newRequest(context)
                .mode(TravelMode.WALKING) // Mode de déplacement: à pied
                .origin(start)
                .destination(end)
                .await()
        } catch (e: ApiException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}
