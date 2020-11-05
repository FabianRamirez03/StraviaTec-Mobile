package com.example.straviatec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class loginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)
            button2.setOnClickListener{
                SpotifyService.connect(this) {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                }
            }
    }
}