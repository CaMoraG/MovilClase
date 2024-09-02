package com.example.primeraappjegt.Logica

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.primeraappjegt.R

class PermisosEjemplo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permisos_ejemplo)

        inicializar()
    }

    private fun inicializar(){
        val imageButtonContactos = findViewById<ImageButton>(R.id.imageButtonContactos)
        val imageButtonCamara = findViewById<ImageButton>(R.id.imageButtonCamara)

        inicializarBotonContactos(imageButtonContactos)
        inicializarBotonCamara(imageButtonCamara)
    }

    private fun inicializarBotonContactos(imageButtonContacos: ImageButton){
        imageButtonContacos.setOnClickListener {
            startActivity(Intent(this, ResultadoContactos::class.java))
        }
    }

    private fun inicializarBotonCamara(imageButtonCamara: ImageButton){
        imageButtonCamara.setOnClickListener {
            startActivity(Intent(this, CamaraAcciones::class.java))
        }
    }

}