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
import com.example.straviatec.dataBase.UsersDBHelper
import com.example.straviatec.dataBase.Usuario
import kotlinx.android.synthetic.main.activity_login.*
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
                                deleteDatabase(UsersDBHelper.DATABASE_NAME);
                                baseDatos.createUser(
                                    baseDatos.readableDatabase, Usuario(
                                        response.getInt("idusuario"),
                                        response.getString("nombreusuario"),
                                        response.getString("primernombre"),
                                        response.getString("apellidos"),
                                        LocalDateTime.parse(response.get("fechanacimiento") as CharSequence?),
                                        response.getString("nacionalidad"),
                                        response.getString("contrasena"),
                                        optByteArray(response,"foto"),
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
                                val intent = Intent(this, FeedActivity::class.java)
                                intent.putExtra("Cliente", response.toString())
                                intent.putExtra("url", url)
                                finish()
                                startActivity(intent)
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
    fun optByteArray(json: JSONObject, key: String): UByteArray?
    {
        // http://code.google.com/p/android/issues/detail?id=13830
        if (json.isNull(key))
            return null;
        else
            return json.optString(key, null).toByteArray().toUByteArray();
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