package com.example.primeraappjegt.Logica

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.primeraappjegt.R
import com.example.primeraappjegt.databinding.ActivityMapsBinding
import com.example.primeraappjegt.databinding.ActivityOsmmapBinding
import org.osmdroid.config.Configuration
import com.example.primeraappjegt.BuildConfig
import com.example.primeraappjegt.Datos.Datos
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.events.MapListener

class OSMMap : AppCompatActivity() {

    private lateinit var binding: ActivityOsmmapBinding
    private lateinit var map: MapView
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var currentLocationMarker: Marker

    private val getLocationSettings = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if(result.resultCode == RESULT_OK){
            startLocationUpdates()
        }else{
            Toast.makeText(this, "No se puede obtener la ubicación", Toast.LENGTH_SHORT).show()
        }
    }


    val latitude = 4.658886
    val longitude = -74.076926
    val startPoint = GeoPoint(latitude, longitude)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_osmmap)

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID)

        initUI()
    }

    private fun initUI(){
        binding = ActivityOsmmapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        map = binding.map

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationRequest = createLocationRequest()

        currentLocationMarker = Marker(map)
        currentLocationMarker.icon = ContextCompat.getDrawable(this, R.drawable.marker_svgrepo_com)
        map.overlays.add(currentLocationMarker)

        initLocationCallBack()
        checkLocationSettings()


    }

    private fun initLocationCallBack(){
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    GeoPoint(location.latitude, location.longitude).let {
                        val mapController: IMapController = map.controller
                        mapController.setZoom(20.0)
                        mapController.setCenter(it)
                        currentLocationMarker.position = it
                        currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        currentLocationMarker.title = "Current Location"
                        map.invalidate() // Refresh the map
                        Log.d("Location", "Location update: $it")
                    }
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
        }
    }

    private fun checkLocationSettings(){
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            startLocationUpdates()
        }

        task.addOnFailureListener { e ->
            if ((e as ApiException).statusCode == CommonStatusCodes.RESOLUTION_REQUIRED){
                val resolvable = e as ResolvableApiException
                val isr = IntentSenderRequest.Builder(resolvable.resolution).build()
                getLocationSettings.launch(isr)
            }else{
                Toast.makeText(this, "No se puede obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createLocationRequest(): LocationRequest =
// New builder
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,10000).apply {
            setMinUpdateIntervalMillis(5000)
        }.build()


    private fun requestPermission(context: Activity, permiso: String, justificacion: String, idCode: Int) {
        //val textView = findViewById<TextView>(R.id.resultadosContactosTextView)
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            // Si el permiso no ha sido concedido, lo solicitamos
            ActivityCompat.requestPermissions(context, arrayOf(permiso), idCode)
        } else {
            // Si el permiso ya ha sido concedido, iniciamos la actividad
            initUI()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Datos.MY_PERMISSIONS_LOCATION -> {
                //val textView = findViewById<TextView>(R.id.resultadosContactosTextView)
                var mensaje = ""
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    initUI()
                } else {
                    Toast.makeText(this, "Permiso denegado es necesario", Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {

            }
        }
    }

    private fun adjustMarkerSize() {
        val zoomLevel = map.zoomLevelDouble
        val scaleFactor = zoomLevel / 20.0 // Adjust the divisor to control scaling
        val icon = ContextCompat.getDrawable(this, R.drawable.marker_svgrepo_com)
        icon?.setBounds(0, 0, (icon.intrinsicWidth * scaleFactor).toInt(), (icon.intrinsicHeight * scaleFactor).toInt())
        currentLocationMarker.icon = icon
        map.invalidate()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        val mapController: IMapController = map.controller
        mapController.setZoom(18.0)
        mapController.setCenter(this.startPoint)
    }
    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}