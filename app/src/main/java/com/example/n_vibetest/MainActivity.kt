package com.example.n_vibetest

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.n_vibetest.utils.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.DirectionsLeg
import com.google.maps.model.DirectionsResult
import com.google.maps.model.DirectionsStep
import com.google.maps.model.GeocodingResult
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var etDepart: EditText
    private lateinit var etArrivee: EditText
    private lateinit var btnRechercher: Button
    private lateinit var mMap: GoogleMap
    private lateinit var directionService: DirectionService
    private lateinit var geoApiContext: GeoApiContext
    private lateinit var tvDuration: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("MainActivity", "onCreate")
        val dexOutputDir: File = codeCacheDir
        dexOutputDir.setReadOnly()
        initializeViews()
        initializeMapAndServices()
        setupButtonClick()
    }

    private fun initializeViews() {
        etDepart = findViewById(R.id.etDepart)
        etArrivee = findViewById(R.id.etArrivee)
        btnRechercher = findViewById(R.id.btnRechercher)
        tvDuration = findViewById(R.id.tvDuration)
    }

    private fun initializeMapAndServices(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        directionService = DirectionService(Constants.getGoogleServicesApiKey(this))
        geoApiContext = GeoApiContext.Builder()
            .apiKey(Constants.getGoogleServicesApiKey(this))
            .build()

    }


    private fun setupButtonClick() {
        btnRechercher.setOnClickListener {
            val depart = etDepart.text.toString().trim()
            val arrivee = etArrivee.text.toString().trim()

            if (depart.isEmpty() || arrivee.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer une adresse de départ et une adresse d'arrivée", Toast.LENGTH_SHORT).show()
            } else {
                if (isAddressInFrance(depart,Constants.getGoogleServicesApiKey(this)) && isAddressInFrance(arrivee,Constants.getGoogleServicesApiKey(this))) {
                    val result = directionService.getDirections(depart, arrivee)
                    if (result != null) {
                        drawRoute(result)
                        val departLatLng = getLatLngFromAddress(this, depart)
                        val arriveeLatLng = getLatLngFromAddress(this, arrivee)

                        if (departLatLng != null && arriveeLatLng != null) {
                            // Vous pouvez maintenant utiliser departLatLng et arriveeLatLng pour dessiner la route, etc.
                            // Ajouter le marqueur 1
                            mMap.addMarker(MarkerOptions().position(departLatLng).title("Départ"))

                            // Ajouter le marqueur 2
                            mMap.addMarker(MarkerOptions().position(arriveeLatLng).title("Arrivée"))
                        } else {
                            // Gérer le cas où l'une ou les deux adresses n'ont pas pu être converties en coordonnées LatLng
                            if (departLatLng == null) {
                                // Gérer l'erreur pour l'adresse de départ
                                Log.e("Error", "Impossible de convertir l'adresse de départ en coordonnées LatLng")
                            }
                            if (arriveeLatLng == null) {
                                // Gérer l'erreur pour l'adresse d'arrivée
                                Log.e("Error", "Impossible de convertir l'adresse d'arrivée en coordonnées LatLng")
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Les adresses doivent être valides et situées en France", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume()")
        geoApiContext.shutdown()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy()")
        geoApiContext.shutdown()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Coordonnées géographiques de Paris
        val position = Constants.DEFAULT_POSITION

        // Déplacer la caméra pour centrer la carte sur Paris
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position))

        // Zoom sur Paris
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, Constants.DEFAULT_ZOOM_LEVEL), 3000, null)
    }

    private fun drawRoute(directionsResult: DirectionsResult) {
        //Effacer les ancien routes
        mMap.clear()

        val legs = directionsResult.routes[0].legs

        for (leg: DirectionsLeg in legs) {
            val steps: List<DirectionsStep> = leg.steps.toList()
            val durationText = leg.duration.humanReadable // Temps d'estimation de marche en format texte

            tvDuration.text = "Durée estimée de marche : $durationText"

            for (step: DirectionsStep in steps) {
                val polylineOptions = PolylineOptions()
                val points = step.polyline.decodePath()

                for (point: com.google.maps.model.LatLng in points) {
                    val latLng = LatLng(point.lat, point.lng)
                    polylineOptions.add(latLng)
                }

                mMap.addPolyline(polylineOptions)
            }
        }
    }

    private fun isAddressInFrance(address: String, apiKey: String): Boolean {
        val context = GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()

        val results: Array<GeocodingResult> = GeocodingApi.geocode(context, address).await()

        for (result in results) {
            val location = result.geometry.location
            val lat = location.lat
            val lng = location.lng

            // Vérifiez si les coordonnées se trouvent en France
            if (lat in 41.0..51.0 && lng in -5.0..10.0) {
                return true
            }
        }

        return false
    }

    private fun getLatLngFromAddress(activity: MainActivity, address: String): LatLng? {
        val geocoder = Geocoder(activity)
        try {
            val addresses: List<Address> = geocoder.getFromLocationName(address, 1)!!
            if (addresses.isNotEmpty()) {
                val location = addresses[0]
                return LatLng(location.latitude, location.longitude)
            } else {
                // Aucune adresse n'a été trouvée
                return null
            }
        } catch (e: IOException) {
            // Une erreur s'est produite lors de la conversion de l'adresse en coordonnées
            e.printStackTrace()
            return null
        }
    }

}
