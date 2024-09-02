package com.example.primeraappjegt.Logica

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.primeraappjegt.Datos.Datos
import com.example.primeraappjegt.R

class CamaraAcciones : AppCompatActivity() {
    private lateinit var btnTomarFoto: Button
    private lateinit var btnSeleccionarFoto: Button
    private lateinit var ivFoto: ImageView


    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            ivFoto.setImageURI(data?.data)
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val extras: Bundle? = data?.extras
            val imageBitmap = extras?.get("data") as? Bitmap
            ivFoto.setImageBitmap(imageBitmap)
        }
    }

    val permisos = listOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )
    val justificaciones = listOf(
        "Se necesita permiso para acceder a la galería",
        "Se necesita permiso para acceder a la cámara"
    )
    val idCodes = listOf(
        Datos.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE,
        Datos.MY_PERMISSIONS_REQUEST_CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camara_acciones)

        initGUI()
    }

    private fun initGUI(){
        btnTomarFoto = findViewById(R.id.btnTomarFoto)
        btnSeleccionarFoto = findViewById(R.id.btnSeleccionarFoto)
        ivFoto = findViewById(R.id.ivFoto)
        initGallery()
        initCamera()
    }

    private fun initGallery(){
        requestPermissions(this, permisos, justificaciones, idCodes)
        if (btnSeleccionarFoto.isEnabled){
            btnSeleccionarFoto.setOnClickListener {
                val pickImage = Intent(Intent.ACTION_PICK)
                pickImage.type = "image/*"
                //startActivityForResult(pickImage, Datos.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                pickImageLauncher.launch(pickImage)
            }
        }
    }
    private fun initCamera(){
        if (btnTomarFoto.isEnabled){
            btnTomarFoto.setOnClickListener {
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                //startActivityForResult(takePicture, Datos.MY_PERMISSIONS_REQUEST_CAMERA)
                takePictureLauncher.launch(takePicture)
            }
        }
    }

    private fun requestPermissions(context: Activity, permisos: List<String>, justificaciones: List<String>, idCodes: List<Int>, index: Int = 0) {
        if (index >= permisos.size) return

        val permiso = permisos[index]
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, arrayOf(permiso), idCodes[index])
        } else {
            // Si el permiso ya ha sido concedido, solicitar el siguiente permiso
            requestPermissions(context, permisos, justificaciones, idCodes, index + 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Datos.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permiso concedido
                } else {
                    btnSeleccionarFoto.isEnabled = false
                }
                // Solicitar el siguiente permiso
                requestPermissions(this, permisos, justificaciones, idCodes, 1)
            }
            Datos.MY_PERMISSIONS_REQUEST_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permiso concedido
                } else {
                    btnTomarFoto.isEnabled = false
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            Datos.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                if (resultCode == RESULT_OK){
                    ivFoto.setImageURI(data?.data)
                }
            }
            Datos.MY_PERMISSIONS_REQUEST_CAMERA -> {
                if (resultCode == RESULT_OK){
                    val extras: Bundle? = data?.extras
                    val imageBitmap = extras?.get("data") as? Bitmap
                    ivFoto.setImageBitmap(imageBitmap)
                }
            }
        }
    }
}