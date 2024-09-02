package com.example.primeraappjegt.Logica

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import com.example.primeraappjegt.R
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class Pntalla3 : AppCompatActivity(), AdapterView.OnItemClickListener {
    var global = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pntalla3)
        //val spinner = findViewById<Spinner>(R.id.spinnerP3)
        //spinner.onItemClickListener = this

        val json = JSONObject(loadJSONFromAsset())
        val paisesJson = json.getJSONArray("paises")
        val arreglo = ArrayList<String>()
        for (i in 0 until paisesJson.length()){
            val jsonObject = paisesJson.getJSONObject(i)
            arreglo.add(jsonObject.getString("capital"))
        }

        val listView = findViewById<ListView>(R.id.listView1)
        listView.adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1,
            arreglo)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        global = parent?.selectedItem.toString()
        Toast.makeText(this, "Seleccionaste: $global", Toast.LENGTH_SHORT).show()
    }

    fun loadJSONFromAsset(): String? {
        var json: String? = null
        try {
            val isStream: InputStream = assets.open("paises.json")
            val size:Int = isStream.available()
            val buffer = ByteArray(size)
            isStream.read(buffer)
            isStream.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}