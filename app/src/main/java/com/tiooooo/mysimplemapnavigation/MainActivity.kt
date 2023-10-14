package com.tiooooo.mysimplemapnavigation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLatLong: LatLng

    private lateinit var tvLatLong: TextView
    private lateinit var btnOpenMap: MaterialButton
    private lateinit var btnOpenNavigation: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        tvLatLong = findViewById(R.id.tvLatLong)
        btnOpenMap = findViewById(R.id.btnOpenMap)
        btnOpenNavigation = findViewById(R.id.btnNavigation)

        btnOpenMap.setOnClickListener {
            showMap(currentLatLong.latitude, currentLatLong.longitude, "Lokasi Saya")
        }

        btnOpenNavigation.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        getMyLocation()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        } else Toast.makeText(this, "Akses Lokasi tidak diberikan", Toast.LENGTH_SHORT).show()
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                it?.let {
                    val position = LatLng(it.latitude, it.longitude)
                    setUpLocation(it.latitude, it.longitude)
                    currentLatLong = position
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setUpLocation(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            tvLatLong.text = addresses?.get(0)?.getAddressLine(0) ?: "memuat..."
        } catch (err: Exception) {
            Log.d("Lokasi", "Latlong $latitude, $longitude")
        }
    }

    fun showMap(latitude: Double, longitude: Double, label: String) {
        val geoLocation = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = geoLocation
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}
