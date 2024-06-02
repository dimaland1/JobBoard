package com.example.jobboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.font.FontWeight
import com.example.jobboard.API.ApiInterface
import com.example.jobboard.API.JWTLOGIN
import com.example.jobboard.API.LoginRequest
import com.example.jobboard.Candidate.InscriptionCandidateActivity
import com.example.jobboard.Candidate.OffresActivity
import com.example.jobboard.Employer.EmployerListingActivity
import com.example.jobboard.Employer.InscriptionEmployerActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConnexionActivity : ComponentActivity() {

    var qui_es: String by mutableStateOf("candidate")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(color = Color(0xFFF5F5F5), modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Connexion",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ConnexionField()
                }
            }
        }
    }

    @Composable
    fun ConnexionField() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            ToggleExample()
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // connexion à l'API
                    val retrofitBuilder = Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("http://192.168.18.31:3020/")
                        .build()
                        .create(ApiInterface::class.java)

                    val user = LoginRequest(email, password, qui_es)

                    val retrofitData = retrofitBuilder.login(user)

                    retrofitData.enqueue(object : Callback<JWTLOGIN> {
                        override fun onResponse(call: Call<JWTLOGIN>, response: Response<JWTLOGIN>) {
                            if (response.isSuccessful) {
                                if (qui_es == "candidate") {
                                    startActivity(Intent(this@ConnexionActivity, OffresActivity::class.java).apply {
                                        // Vous pouvez passer des données à la nouvelle activité si nécessaire
                                        putExtra("connected", "true")
                                        putExtra("nom_utilisateur", response.body()?.nom_utilisateur)
                                        putExtra("logo_url", response.body()?.logo_url )
                                    })
                                } else {
                                    startActivity(Intent(this@ConnexionActivity, EmployerListingActivity::class.java).apply {
                                        // pass the token to the next activity
                                        putExtra("token", response.body()?.token)
                                    })
                                }
                            }
                        }

                        override fun onFailure(call: Call<JWTLOGIN>, t: Throwable) {
                            // afficher un message d'erreur
                            Log.e("ConnexionActivity", "Error: ${t.message}")
                        }
                    })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(48.dp)
            ) {
                Text("connexion")
            }
            Inscription()
        }
    }

    @Composable
    fun Inscription() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Vous n'avez pas de compte ?", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    startActivity(Intent(this@ConnexionActivity, InscriptionCandidateActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(48.dp)
            ) {
                Text("Creer un compte Candidat")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    startActivity(Intent(this@ConnexionActivity, InscriptionEmployerActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(48.dp)
            ) {
                Text("Creer un compte Employeur")
            }
        }
    }

    @Composable
    fun ToggleExample() {
        var isChecked by remember { mutableStateOf(false) }

        LaunchedEffect(isChecked) {
            qui_es = if (isChecked) "employer" else "candidate"
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Vous êtes un employeur ?", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Switch(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colors.primary,
                    checkedTrackColor = MaterialTheme.colors.primary.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.LightGray
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Toggle is ${if (isChecked) "OUI" else "NON"}", fontSize = 16.sp)
        }
    }
}