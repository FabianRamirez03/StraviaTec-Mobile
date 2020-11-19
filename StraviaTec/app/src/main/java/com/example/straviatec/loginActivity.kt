package com.example.straviatec

import android.app.Activity;
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.straviatec.dataBase.*
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.*

class loginActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)
        button2.visibility = View.VISIBLE
        loading.visibility = View.INVISIBLE
        val url = "http://192.168.100.212/APIStraviaTec/"
        val queue = Volley.newRequestQueue(this)
        val jsonObject = JSONObject()
            button2.setOnClickListener{
                button2.visibility = View.INVISIBLE
                loading.visibility = View.VISIBLE
                jsonObject.put("NombreUsuario",usuario.text)
                jsonObject.put("Contrasena",password.text)
                val stringRequest = JsonObjectRequest(Request.Method.POST,"${url}Usuario/validarUser", jsonObject,Response.Listener { response->
                    if(response.getBoolean("validacion")){
                        val userRequest = JsonObjectRequest(Request.Method.POST,"${url}Usuario/porNombreUsuario", jsonObject,Response.Listener { response->
                            if(response != null){
                                val baseDatos = UsersDBHelper(this)
                                val id =  response.getInt("idusuario");
                                deleteDatabase(UsersDBHelper.DATABASE_NAME);
                                deleteDatabase(CarreraDBHelper.DATABASE_NAME);
                                deleteDatabase(RetoDBHelper.DATABASE_NAME);
                                baseDatos.createUser(
                                    baseDatos.readableDatabase, Usuario(
                                        response.getInt("idusuario"),
                                        response.getString("nombreusuario"),
                                        response.getString("primernombre"),
                                        response.getString("apellidos"),
                                        LocalDateTime.parse(response.get("fechanacimiento") as CharSequence?),
                                        response.getString("nacionalidad"),
                                        response.getString("contrasena"),
                                        optString(response,"foto"),
                                        optInt(response,"edad"),
                                        optString(response,"categoria")
                                    )
                                )
                                val cursor= baseDatos.user
                                if (cursor.moveToFirst()) {
                                    do {
                                        println(cursor.getString(cursor.getColumnIndex("fechaNacimiento")))
                                    } while (cursor.moveToNext())
                                }
                                val queue = Volley.newRequestQueue(this)
                                val jsonObjectCarr = JSONObject()
                                jsonObjectCarr.put("Idusuario",id)
                                var JsonReqCarr = MyJsonArrayRequest(
                                    Request.Method.POST,"${url}Carrera/carrerasPorUsuario",jsonObjectCarr,
                                    Response.Listener<JSONArray> { response->
                                        for (i in 0 until response.length()){
                                            val carrera = response[i] as JSONObject
                                            val carreraDB = CarreraDBHelper(this)
                                            carreraDB.createCarrera(
                                                carreraDB.readableDatabase, Carrera(
                                                    carrera.getInt("idDeportista"),
                                                    carrera.getString("categoria"),
                                                    carrera.getInt("idCarrera"),
                                                    carrera.getString("nombreCarrera"),
                                                    carrera.getString("tipo"),
                                                    LocalDateTime.parse(carrera.get("fecha") as CharSequence?),
                                                    carrera.getString("kilometraje"),
                                                    carrera.getString("altura"),
                                                    carrera.getString("duracion"),
                                                    carrera.getBoolean("completitud"),
                                                    carrera.getString("recorrido")

                                                )
                                            )

                                            //Retos
                                            val jsonObjectReto = JSONObject()
                                            jsonObjectReto.put("Idusuario",id)
                                            var JsonReqReto = MyJsonArrayRequest(
                                                Request.Method.POST,"${url}Retos/retosPorUsuario",jsonObjectReto,
                                                Response.Listener<JSONArray> { response->
                                                    for (i in 0 until response.length()){
                                                        val reto = response[i] as JSONObject
                                                        val retoDB = RetoDBHelper(this)
                                                        retoDB.createReto(
                                                            retoDB.readableDatabase, Reto(
                                                                reto.getInt("idDeportista"),
                                                                reto.getInt("idReto"),
                                                                reto.getString("nombreReto"),
                                                                reto.getString("objetivo"),
                                                                reto.getString("tipoActividad"),
                                                                reto.getString("tipoReto"),
                                                                LocalDateTime.parse(reto.get("fechaInicio") as CharSequence?),
                                                                LocalDateTime.parse(reto.get("fechaFinal") as CharSequence?),
                                                                reto.getString("kilometraje"),
                                                                reto.getString("altura"),
                                                                reto.getString("duracion"),
                                                                reto.getBoolean("completitud"),
                                                                reto.getString("recorrido")

                                                            )
                                                        )
                                                        val intent = Intent(this, FeedActivity::class.java)
                                                        intent.putExtra("Cliente", response.toString())
                                                        intent.putExtra("url", url)
                                                        finish()
                                                        startActivity(intent)
                                                    }
                                                },
                                                Response.ErrorListener {
                                                    Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
                                                })
                                            queue.add(JsonReqReto)
                                        }
                                    },
                                    Response.ErrorListener {
                                        Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
                                    })
                                queue.add(JsonReqCarr)
                            }
                        },Response.ErrorListener {Toast.makeText(this,it.toString(), Toast.LENGTH_LONG).show()
                            button2.visibility = View.VISIBLE
                            loading.visibility = View.INVISIBLE})
                        queue.add(userRequest)
                    }
                    else{
                        Toast.makeText(this,"Usuario o contraseña incorrecta", Toast.LENGTH_LONG).show()
                        button2.visibility = View.VISIBLE
                        loading.visibility = View.INVISIBLE
                    }
                },Response.ErrorListener {
                    Toast.makeText(this,it.toString(), Toast.LENGTH_LONG).show()
                    button2.visibility = View.VISIBLE
                    loading.visibility = View.INVISIBLE})
                queue.add(stringRequest)

            }
    }
    fun optString(json: JSONObject, key: String): String?
    {
        // http://code.google.com/p/android/issues/detail?id=13830
        if (json.isNull(key))
            return null;
        else
            return json.optString(key, null);
    }
    fun optInt(json: JSONObject, key: String): Int?
    {
        // http://code.google.com/p/android/issues/detail?id=13830
        if (json.isNull(key))
            return null;
        else
            return json.optString(key, null).toInt();
    }
}