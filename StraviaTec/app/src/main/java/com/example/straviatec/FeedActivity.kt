package com.example.straviatec

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.system.Os.close
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.straviatec.dataBase.UsersDBHelper
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

class FeedActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        jsonObject.put("nombre","Recreativa la Fortuna")
        jsonObject.put("tipo","Atletismo")
        jsonObject.put("progreso","20")
        jsonArray.put(jsonObject)
        drawActivities(jsonArray)
    }
    fun drawActivities(actividades: JSONArray){
        val layout = findViewById<LinearLayout>(R.id.linearLay)
        var marginY = 0F
        val marginX = 50F
        var y = 0f
        for (i in 0 until actividades.length()){
            val gridLayout = GridLayout(this)
            gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
            gridLayout.setColumnCount(2)
            gridLayout.setRowCount(2)
            gridLayout.minimumHeight = 300
            val actividad =actividades[i] as JSONObject
            val nombre =TextView(this)
            nombre.text = actividad.getString("nombre")
            val tipo = TextView(this)
            tipo.text = actividad.getString("tipo")
            val completitud = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
            completitud.progress = actividad.getString("progreso").toInt()
            val boton = Button(this)
            boton.background = ContextCompat.getDrawable(this,R.drawable.round_button)
            boton.text = "Realizar"

            //Nombre
            nombre.textSize = 24f
            nombre.x = 20f

            //Tipo
            tipo.textSize = 24f
            tipo.x = 80f


            //Completitud
            completitud.y = 100f
            completitud.x = 20f
            completitud.getProgressDrawable().setColorFilter(Color.parseColor("#167797"), android.graphics.PorterDuff.Mode.SRC_IN);

            //Boton
            boton.x = 80f
            boton.y = 70f
            boton.setOnClickListener{
                val baseDatos = UsersDBHelper(this)
                val cursor= baseDatos.user
                if (cursor.moveToFirst()) {
                    do {
                        println(cursor.getString(cursor.getColumnIndex("apellidos")))
                    } while (cursor.moveToNext())
                }
                SpotifyService.connect(this) {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                }
            }


            gridLayout.addView(nombre)
            gridLayout.addView(tipo)
            gridLayout.addView(completitud)
            gridLayout.addView(boton)
            gridLayout.y = y
            gridLayout.background = ContextCompat.getDrawable(this,R.drawable.rectangle)
            y = y + 100

            layout.addView(gridLayout)



        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}