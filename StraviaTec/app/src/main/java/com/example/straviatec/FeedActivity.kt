package com.example.straviatec

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.straviatec.dataBase.*
import kotlinx.android.synthetic.main.activity_feed.*
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FeedActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var url: String
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        url = intent.getStringExtra("url").toString()
        val header = navView.getHeaderView(0)
        val user = header.findViewById<TextView>(R.id.NombreCliente)
        val img = header.findViewById<ImageView>(R.id.imagenUsuario)
        val baseDatos = UsersDBHelper(this)
        val cursor= baseDatos.user
        var nombreUsuario = ""
        var imagen = ""
        var id = 0
        if (cursor.moveToFirst()) {
            do {
                nombreUsuario = cursor.getString(cursor.getColumnIndex("nombreUsuario"))
                imagen = cursor.getString(cursor.getColumnIndex("foto"))
                id = cursor.getInt(cursor.getColumnIndex("idUsuario"))
            } while (cursor.moveToNext())
        }
        user.setText(nombreUsuario)//Mostrar Usuario

        //Mostrar Imagen
        if (imagen != null){
            Log.i("TAG", imagen.length.toString())
            imagen = imagen.split(",")[1]
            var imagen64 = Base64.decode(imagen, Base64.CRLF)
            var bitmap =  BitmapFactory.decodeByteArray(imagen64, 0, imagen64.size);
            img.setImageBitmap(bitmap)
        }
        //Carreras
        val carrerasDB = CarreraDBHelper(this)
        val retosDB = RetoDBHelper(this)
        val carreras = carrerasDB.getListaCarreras(id)
        val retos = retosDB.getListaRetos(id)
        Log.e("KM", retos[0].kilometraje)
        Log.e("KM", "HOLAAAAAAAAASDFGHJKL")
        println(retos[0].kilometraje)
        drawActividades(retos,carreras)


    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun drawActividades(retos: List<Reto>, carreras: List<Carrera>) {
        val layout = findViewById<LinearLayout>(R.id.linearLay)
        layout.minimumHeight = 1080
        var marginY = 0F
        val marginX = 50F
        var y = 50f
        for (carrera in carreras) {
            val gridLayout = GridLayout(this)
            gridLayout.setAlignmentMode(GridLayout.ALIGN_MARGINS);
            gridLayout.setColumnCount(3)
            gridLayout.setRowCount(4)
            gridLayout.minimumHeight = 300
            gridLayout.y = y

            val nombre = TextView(this)
            nombre.text = carrera.nombreCarrera

            val tipo = TextView(this)
            tipo.text = "Tipo: ${carrera.tipo}"

            val categoria = TextView(this)
            categoria.text = carrera.categoria
            categoria.setTextSize(24f)

            val fecha = TextView(this)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            fecha.text = carrera.fecha.format(formatter)

            val km = TextView(this)
            km.text = "Kilometraje:  ${carrera.kilometraje}"

            val altura = TextView(this)
            altura.text = "Altura: ${carrera.altura}"


            val boton = Button(this)
            boton.background = ContextCompat.getDrawable(this, R.drawable.round_button)
            boton.text = "Realizar"

            val loading = ProgressBar(this)
            loading.visibility = View.INVISIBLE

            //Nombre
            nombre.textSize = 24f
            nombre.x = 20f

            //Tipo
            tipo.textSize = 24f
            tipo.x = 100f


            //categoria
            categoria.y = 0f
            categoria.x = 200f

            //fecha
            fecha.y = 54f
            fecha.x = 20f

            //Kilometraje
            km.y = 54f
            km.x = 100f

            //Altura
            altura.y = 54f
            altura.x = 200f


            //Boton
            boton.x = 150f
            boton.y = 50f
            loading.x = 200f
            loading.y = 50f
            if (carrera.completitud) {
                boton.visibility = View.INVISIBLE
            }
            boton.setOnClickListener {
                boton.visibility = View.INVISIBLE
                loading.visibility = View.VISIBLE
                loading.getIndeterminateDrawable().setColorFilter(
                    Color.parseColor("#167797"),
                    android.graphics.PorterDuff.Mode.MULTIPLY
                );
                SpotifyService.connect(this) {
                    val intent = Intent(this, MapsActivity::class.java)
                    intent.putExtra("Tipo", "carrera")
                    intent.putExtra("id", carrera.idCarrera)
                    intent.putExtra("url", url)
                    startActivity(intent)
                }
            }


            gridLayout.addView(nombre)
            gridLayout.addView(tipo)
            gridLayout.addView(categoria)
            gridLayout.addView(fecha)
            gridLayout.addView(km)
            gridLayout.addView(altura)
            gridLayout.addView(boton)
            gridLayout.addView(loading, 6)
            gridLayout.y = y
            gridLayout.background = ContextCompat.getDrawable(this, R.drawable.rectangle)
            y = y + 100

            layout.addView(gridLayout)

        }
        //Retos
        for (reto in retos) {
            val gridLayout = GridLayout(this)
            gridLayout.setAlignmentMode(GridLayout.ALIGN_MARGINS);
            gridLayout.setColumnCount(3)
            gridLayout.setRowCount(4)
            gridLayout.minimumHeight = 310

            val nombre = TextView(this)
            nombre.text = reto.nombreReto

            val tipo = TextView(this)
            tipo.text = "${reto.tipoReto}"

            val tipoAct = TextView(this)
            tipoAct.text = "${reto.tipoActividad}"
            tipoAct.setTextSize(24f)


            val km = TextView(this)
            km.text = "Kilometraje:  ${reto.kilometraje}"

            val altura = TextView(this)
            altura.text = "Altura: ${reto.altura}"


            val boton = Button(this)
            boton.background = ContextCompat.getDrawable(this, R.drawable.round_button)
            boton.text = "Realizar"

            val loading = ProgressBar(this)
            loading.visibility = View.INVISIBLE

            //Nombre
            nombre.textSize = 24f
            nombre.x = 20f

            //Tipo
            tipo.textSize = 24f
            tipo.x = 100f


            //categoria
            tipoAct.y = 0f
            tipoAct.x = 200f


            //Kilometraje
            km.y = 54f
            km.x = 100f

            //Altura
            altura.y = 54f
            altura.x = 230f


            //Boton
            boton.x = -150f
            boton.y = 120f
            loading.x = 250f
            loading.y = 20f
            if (reto.completitud) {
                boton.visibility = View.INVISIBLE
            }
            boton.setOnClickListener {
                boton.visibility = View.INVISIBLE
                loading.visibility = View.VISIBLE
                loading.getIndeterminateDrawable().setColorFilter(
                    Color.parseColor("#167797"),
                    android.graphics.PorterDuff.Mode.MULTIPLY
                );
                SpotifyService.connect(this) {
                    val intent = Intent(this, MapsActivity::class.java)
                    intent.putExtra("Tipo", "reto")
                    intent.putExtra("id", reto.idReto)
                    intent.putExtra("url", url)
                    startActivity(intent)
                }
            }


            gridLayout.addView(nombre)
            gridLayout.addView(tipo)
            gridLayout.addView(tipoAct)
            gridLayout.addView(km)
            gridLayout.addView(altura)
            gridLayout.addView(boton)
            gridLayout.addView(loading, 6)
            gridLayout.y = y
            gridLayout.background = ContextCompat.getDrawable(this, R.drawable.rectangle_color)
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