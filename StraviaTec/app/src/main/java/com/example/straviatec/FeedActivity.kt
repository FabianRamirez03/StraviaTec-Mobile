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
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.straviatec.dataBase.*
import kotlinx.android.synthetic.main.activity_feed.*
import org.json.JSONObject
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class FeedActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var url: String
    var id =  0
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
        if (cursor.moveToFirst()) {
            do {
                nombreUsuario = cursor.getString(cursor.getColumnIndex("nombreUsuario"))
                imagen = cursor.getString(cursor.getColumnIndex("foto"))
                id = cursor.getInt(cursor.getColumnIndex("idUsuario"))
            } while (cursor.moveToNext())
        }
        user.setText(nombreUsuario)//Mostrar Usuario

        //Mostrar Imagen
        if (imagen != null) {
            try {
                Log.i("TAG", imagen.length.toString())
                imagen = imagen.split(",")[1]
                var imagen64 = Base64.decode(imagen, Base64.CRLF)
                var bitmap = BitmapFactory.decodeByteArray(imagen64, 0, imagen64.size);
                img.setImageBitmap(bitmap)
            } catch (e: Exception) {
            }
        }
        val carrerasDB = CarreraDBHelper(this)
        val retosDB = RetoDBHelper(this)
        val actividadesDB = ActividadDBHelper(this)
        val carreras = carrerasDB.getListaCarreras(id)
        val retos = retosDB.getListaRetos(id)
        val actividades = actividadesDB.getListaActividades(id)
        Log.e("ACTIVIDAD", actividades.size.toString())
        drawActividades(retos,carreras, actividades)

        sync.setOnClickListener{
            syncServer(retos,carreras,actividades)
        }


        actividad.setOnClickListener{
            SpotifyService.connect(this) {
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra("Tipo", "actividad")
                intent.putExtra("id", 0)
                intent.putExtra("usuario", id)
                intent.putExtra("url", url)
                startActivity(intent)
            }

        }


    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun drawActividades(retos: List<Reto>, carreras: List<Carrera>, actividades: List<Actividad>) {
        val layout = findViewById<LinearLayout>(R.id.linearLay)
        layout.minimumHeight = 3080
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
            tipo.text = carrera.tipo

            val categoria = TextView(this)
            categoria.text = carrera.categoria
            categoria.setTextSize(24f)

            val fecha = TextView(this)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            fecha.text = carrera.fecha.format(formatter)

            val km = TextView(this)
            km.text = "Km: ${carrera.kilometraje}"

            val altura = TextView(this)
            altura.text = "Altura: ${carrera.altura}"


            val boton = Button(this)
            boton.background = ContextCompat.getDrawable(this, R.drawable.round_button)
            boton.text = "Realizar"

            val loading = ProgressBar(this)
            loading.visibility = View.INVISIBLE

            //Nombre
            nombre.textSize = 18f
            nombre.x = 20f

            //Tipo
            tipo.textSize = 18f
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
            boton.x = 120f
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
                    intent.putExtra("usuario", id)
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
            km.text = "Km:  ${reto.kilometraje}"

            val altura = TextView(this)
            altura.text = "Altura: ${reto.altura}"

            val objetivo = TextView(this)
            objetivo.text = "Objetivo: ${reto.objetivo}"


            val boton = Button(this)
            boton.background = ContextCompat.getDrawable(this, R.drawable.round_button)
            boton.text = "Realizar"

            val loading = ProgressBar(this)
            loading.visibility = View.INVISIBLE

            //Nombre
            nombre.textSize = 18f
            nombre.x = 20f

            //Tipo
            tipo.textSize = 18f
            tipo.x = 100f


            //categoria
            tipoAct.y = 0f
            tipoAct.x = 200f


            //Kilometraje
            km.y = 54f
            km.x = 20f

            //Altura
            altura.y = 54f
            altura.x = 100f

            //Objetivo
            objetivo.y = 54f
            objetivo.x = 200f


            //Boton
            boton.x = 350f
            boton.y = 50f
            loading.x = 150f
            loading.y = 50f
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
                    intent.putExtra("usuario", id)
                    intent.putExtra("url", url)
                    startActivity(intent)
                }
            }


            gridLayout.addView(nombre)
            gridLayout.addView(tipo)
            gridLayout.addView(tipoAct)
            gridLayout.addView(km)
            gridLayout.addView(altura)
            gridLayout.addView(objetivo)
            gridLayout.addView(boton)
            gridLayout.addView(loading, 7)
            gridLayout.y = y
            gridLayout.background = ContextCompat.getDrawable(this, R.drawable.rectangle_color)
            y = y + 100

            layout.addView(gridLayout)

        }
        //Actividades
        for (actividad in actividades) {
            val gridLayout = GridLayout(this)
            gridLayout.setAlignmentMode(GridLayout.ALIGN_MARGINS);
            gridLayout.setColumnCount(3)
            gridLayout.setRowCount(4)
            gridLayout.minimumHeight = 310

            val nombre = TextView(this)
            nombre.text = actividad.nombreActividad

            val tipo = TextView(this)
            tipo.text = "${actividad.tipo}"


            val km = TextView(this)
            km.text = "Km:  ${actividad.km}"

            val altura = TextView(this)
            altura.text = "Altura: ${actividad.altura}"

            val duracion = TextView(this)
            duracion.text = actividad.duracion

            val fecha = TextView(this)
            fecha.text = actividad.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))



            //Nombre
            nombre.textSize = 20f
            nombre.x = 20f

            //Tipo
            tipo.textSize = 20f
            tipo.x = 100f

            //Fecha
            fecha.y = 54f
            fecha.x = 100f


            //Kilometraje
            km.y = 54f
            km.x = 20f

            //Altura
            altura.textSize = 20f
            altura.x = 200f

            //Duracion
            duracion.y = 54f
            duracion.x = 200f



            gridLayout.addView(nombre)
            gridLayout.addView(tipo)
            gridLayout.addView(altura)
            gridLayout.addView(km)
            gridLayout.addView(fecha)
            gridLayout.addView(duracion)
            gridLayout.y = y
            gridLayout.background = ContextCompat.getDrawable(this, R.drawable.rectangle_color_orange)
            y = y + 100

            layout.addView(gridLayout)

        }

    }

    fun syncServer(retos: List<Reto>, carreras: List<Carrera>, actividades: List<Actividad>){
        for (reto in retos) {
            val queue = Volley.newRequestQueue(this)
            val jsonObject = JSONObject()
            jsonObject.put("Idreto", reto.idReto)
            jsonObject.put("Iddeportista", reto.idDeportista)
            jsonObject.put("Duracion", reto.duracion)
            jsonObject.put("Kilometraje", reto.kilometraje)
            jsonObject.put("Altura", reto.altura)
            jsonObject.put("Completitud", reto.completitud)
            jsonObject.put("Recorrido", reto.recorrido)
            val stringRequest = JsonObjectRequest(
                Request.Method.POST,
                "${url}Retos/updateUserRetos",
                jsonObject,
                Response.Listener { response ->
                    Toast.makeText(this, "Retos actualizados correctamente :)", Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                })
            queue.add(stringRequest)
        }

        for(carrera in carreras){
            val queue = Volley.newRequestQueue(this)
            val jsonObject = JSONObject()
            jsonObject.put("Idcarrera", carrera.idCarrera)
            jsonObject.put("Iddeportista", carrera.idDeportista)
            jsonObject.put("Tiemporegistrado", carrera.duracion)
            jsonObject.put("Kilometraje", carrera.kilometraje)
            jsonObject.put("Altura", carrera.altura)
            jsonObject.put("Completitud", carrera.completitud)
            jsonObject.put("Recorrido", carrera.recorrido)
            val stringRequest = JsonObjectRequest(
                Request.Method.POST,
                "${url}Carrera/updateUserCarrera",
                jsonObject,
                Response.Listener { response -> Toast.makeText(this, "Carreras actualizadas correctamente :)", Toast.LENGTH_SHORT).show() },
                Response.ErrorListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                })
            queue.add(stringRequest)
        }
        for(actividad in actividades){
            Log.e("Sincronizado",actividad.sincronizado.toString())
            if (!actividad.sincronizado){
                val queue = Volley.newRequestQueue(this)
                val jsonObject = JSONObject()
                jsonObject.put("Iddeportista", actividad.idDeportista)
                jsonObject.put("Nombreactividad", actividad.nombreActividad)
                jsonObject.put("Duracion", actividad.duracion)
                jsonObject.put("Kilometraje", actividad.km)
                jsonObject.put("Altura", actividad.altura)
                jsonObject.put("Mapa", actividad.recorrido)
                jsonObject.put("Fecha", actividad.fecha)
                jsonObject.put("Tipoactividad", actividad.tipo)
                val stringRequest = JsonObjectRequest(
                    Request.Method.POST,
                    "${url}Actividad/addActivity",
                    jsonObject,
                    Response.Listener { response -> actividad.sincronizado = true
                    val act = ActividadDBHelper(this)
                    act.updateActividad(actividad,actividad.idActividad)
                        Toast.makeText(this, "Se añadió la actividad correctamente :)", Toast.LENGTH_LONG).show()},
                    Response.ErrorListener {
                        Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                    })
                queue.add(stringRequest)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}