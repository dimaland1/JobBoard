package com.example.jobboard.Employer

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*

class EmployerListingActivity : AppCompatActivity() {

    var token = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        token = intent.getStringExtra("token").toString()

        setContent{
            Text( text= token)
        }

    }
}