package com.example.primeraappjegt.Logica

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.primeraappjegt.R
import com.example.primeraappjegt.databinding.ActivityPantalla4Binding

class Pantalla4 : AppCompatActivity() {

    private lateinit var binding: ActivityPantalla4Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_pantalla4)

        binding = ActivityPantalla4Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.Titulo.text = "Pantalla 4 con binding"

        binding.button1.setOnClickListener {
            Intent(this, OSMMap::class.java).also {
                startActivity(it)
            }
        }
    }
}