package com.example.primeraappjegt.Logica

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.primeraappjegt.R

class Pantalla2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla2)

        val boton2: Button = findViewById(R.id.botonSaludo2)
        val nombreIngresado: EditText = findViewById(R.id.nombreIngresado)
        val nombreRecibido = intent.getBundleExtra("informacion")!!.getString("nombre")

        intent.getStringExtra("texto")
        nombreIngresado.text = SpannableStringBuilder(nombreRecibido);

        Toast.makeText(baseContext, nombreRecibido, Toast.LENGTH_LONG).show()

        Toast.makeText(this,"Buenas" + nombreIngresado.text, Toast.LENGTH_SHORT).show()
    }
}