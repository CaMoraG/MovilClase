package com.example.primeraappjegt.Logica

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
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
import com.example.primeraappjegt.Datos.Datos
import com.example.primeraappjegt.R
import com.example.primeraappjegt.databinding.ActivityResultadosGpsBinding
import com.example.primeraappjegt.modelo.MyLocation
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
import org.json.JSONArray
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.util.Date
import kotlin.math.roundToInt

class ResultadosGPS : AppCompatActivity() {
    private lateinit var binding: ActivityResultadosGpsBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    var localizaciones: JSONArray = JSONArray()
    val filename = "locations.json"

    private val getLocationSettings = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if(result.resultCode == RESULT_OK){
            startLocationUpdates()
        }else{
            Toast.makeText(this, "No se puede obtener la ubicación", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultados_gps)

        requestPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION, "Se necesita permiso para acceder a la ubicación", Datos.MY_PERMISSIONS_LOCATION)
    }

    private fun initUI(){
        binding = ActivityResultadosGpsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationRequest = createLocationRequest()
        initLocationCallBack()

        //setResultadosGPS()
        checkLocationSettings()
    }

    private fun initLocationCallBack(){
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    binding.latitud.text = location.latitude.toString()
                    binding.longitud.text = location.longitude.toString()
                    binding.elevacion.text = location.altitude.toString()
                    binding.distancia.text = distance(location.latitude, location.longitude, 4.700866, -74.146038).toString()
                    writeJSONObject(location)
                    updateLocalizacionesGuardadas()
                }
            }
        }
    }

    private fun updateLocalizacionesGuardadas(){
        val localizacionesGuardadas = binding.ubicacionesGuardadas
        val jsonArray = readJSONArrayFromFile(filename)
        val list = ArrayList<String>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val location = "Latitud: ${item.getDouble("latitud")}, Longitud: ${item.getDouble("longitud")}, Fecha: ${item.getString("date")}"
            list.add(location)
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        localizacionesGuardadas.adapter = adapter
    }

    private fun setResultadosGPS(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mFusedLocationClient.lastLocation
                .addOnSuccessListener(this) { location ->
                    if (location != null) {
                        binding.latitud.text = location.latitude.toString()
                        binding.longitud.text = location.longitude.toString()
                        binding.elevacion.text = location.altitude.toString()
                        binding.distancia.text = distance(location.latitude, location.longitude, 4.700866, -74.146038).toString()
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

    fun distance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
        val latDistance = Math.toRadians(lat1 - lat2)
        val lngDistance = Math.toRadians(long1 - long2)
        val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val RADIUS_OF_EARTH_KM = 6371
        val result = RADIUS_OF_EARTH_KM * c
        return (result * 100.0).roundToInt() / 100.0
    }

    private fun writeJSONObject(location: Location) {
        localizaciones.put(
            MyLocation(
            Date(System.currentTimeMillis()), location.latitude,
            location.longitude).toJSON())
        var output: Writer?

        try {
            val file = File(baseContext.getExternalFilesDir(null), filename)
            Log.i("LOCATION", "Ubicacion de archivo: $file")
            output = BufferedWriter(FileWriter(file))
            output.write(localizaciones.toString())
            output.close()
            Toast.makeText(applicationContext, "Location saved", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
//Log error
        }
    }

    private fun readJSONArrayFromFile(fileName: String): JSONArray {
        val file = File(baseContext.getExternalFilesDir(null), fileName)
        if (!file.exists()) {
            Log.i("LOCATION", "Ubicacion de archivo: $file no encontrado")
            return JSONArray()
        }
        val jsonString = file.readText()
        return JSONArray(jsonString)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        if (mFusedLocationClient!=null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }
}