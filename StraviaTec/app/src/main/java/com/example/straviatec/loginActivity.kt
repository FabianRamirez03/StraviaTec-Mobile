package com.example.straviatec

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.straviatec.dataBase.UsersDBHelper
import kotlinx.android.synthetic.main.activity_login.*

class loginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)
        button2.visibility = View.VISIBLE
        loading.visibility = View.INVISIBLE
            button2.setOnClickListener{
                val baseDatos = UsersDBHelper(this)
                val cursor= baseDatos.user
                if (cursor.moveToFirst()) {
                    do {
                        println(cursor.getString(cursor.getColumnIndex("nombre")))
                    } while (cursor.moveToNext())
                }

                button2.visibility = View.INVISIBLE
                loading.visibility = View.VISIBLE
                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
//                SpotifyService.connect(this) {
//                    val intent = Intent(this, MapsActivity::class.java)
//                    startActivity(intent)
//                }
            }
    }
}