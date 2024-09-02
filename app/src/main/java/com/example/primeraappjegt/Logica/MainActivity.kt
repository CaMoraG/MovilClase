package com.example.primeraappjegt.Logica

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.primeraappjegt.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, PermisosEjemplo::class.java)

        //identificacion de objetos por codigo
        val nombre:TextView = findViewById(R.id.textNombre)
        val edad: TextView = findViewById(R.id.textEdad)
        val boton: Button = findViewById(R.id.buttonSaludo)
        var contador = (edad.text as String).toInt()
        val texto:EditText = findViewById(R.id.editTexto)



        //cambios de propiedades
        nombre.text ="Pepito perez"
        edad.text = 45.toString()

        for(i in 0.. 10){

            //LOGs
            Log.i("PrimeraAppJEGT", "Este es un mensaje de información") //Information
            Log.d("PrimeraAppJEGT", "Este es un mensaje de depuración") //Debug
            Log.w("PrimeraAppJEGT", "Este es un mensaje de advertencia") //Warning
            Log.e("PrimeraAppJEGT", "Este es un mensaje de error") //Error
        }


        //Acciones de boton
        boton.setOnClickListener {

            //Toast
            Toast.makeText(this,"Texto introducido: ${texto.text}", Toast.LENGTH_LONG).show()
            //contador++
            val info = Bundle()
            info.putString("nombre", "Camilo")
            info.putString("apellido", "Mora")
            info.putInt("edad", 21)
            info.putFloat("peso", 60.1F)

            intent.putExtra("texto", texto.text)
            intent.putExtra("informacion", info)
            startActivity(intent)

        }




    }
}