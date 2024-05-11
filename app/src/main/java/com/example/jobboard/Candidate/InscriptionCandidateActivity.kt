package com.example.jobboard.Candidate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jobboard.API.ApiInterface
import com.example.jobboard.ConnexionActivity
import com.example.jobboard.API.JWTLOGIN
import com.example.jobboard.API.registerRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class InscriptionCandidateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CreateAccountScreen()
        }

    }


    @Composable
    fun CreateAccountScreen() {

        var name by remember { mutableStateOf("") }
        var firstName by remember { mutableStateOf("") }
        var nationality by remember { mutableStateOf("") }
        var birthDate by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        var cvLink by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Créer un compte candidat",
                fontSize = MaterialTheme.typography.h5.fontSize,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            TextField(label = { Text("Prenom") },value = firstName, onValueChange = {
                firstName = it
            })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = name, onValueChange = {
                name = it
            },
                label = { Text("Nom") })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                label = { Text("Nationalité") },
                value = nationality, onValueChange = {
                // Vérifier si la nationalité est valide
                nationality = it
            })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(label = { Text("Date de naissance") },
                value = birthDate, onValueChange = {
                // Vérifier si le numéro de téléphone est valide
                birthDate = it
            })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(label = { Text("Numéro de telephone") },
                value = phone, onValueChange = {
                // Vérifier si le numéro de téléphone est valide
                phone = it
             })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(label = { Text("Ville") },value = city, onValueChange = {
                city = it
            })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(label = { Text("email") },value = email, onValueChange = {
                email = it
            })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                label = { Text("Mot de passe") },
                value = password, onValueChange = {
                password = it
            })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(label = { Text("CV") },
                value = cvLink, onValueChange = {
                    cvLink = it
                })
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                // Enregistrer le compte candidat dans la base de données
                saveCandidateAccount(
                    name,
                    firstName,
                    nationality,
                    birthDate,
                    phone,
                    email,
                    city,
                    cvLink,
                    "",
                    password
                )
            }) {
                Text("Créer le compte")
            }
        }
    }

    // Fonction factice pour enregistrer le compte candidat dans la base de données
    fun saveCandidateAccount(
        name: String, firstName: String, nationality: String, birthDate: String, phone: String,
        email: String, city: String, cvLink: String, comments: String, password: String
    ) {

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.1.15:3020/")
            .build()
            .create(ApiInterface::class.java)

        val registerRequest = registerRequest(
            name,
            firstName,
            nationality,
            birthDate,
            phone,
            email,
            city,
            cvLink,
            password,
            "",
            "",
            "",
            "",
            "candidate"
        )

        val retrofitData = retrofitBuilder.register(registerRequest)

        retrofitData.enqueue(object : Callback<JWTLOGIN> {
            override fun onResponse(call: Call<JWTLOGIN>, response: Response<JWTLOGIN>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@InscriptionCandidateActivity, ConnexionActivity::class.java).apply {
                        // Vous pouvez passer des données à la nouvelle activité si nécessaire
                        putExtra("nom_utilisateur", name)
                    }
                    startActivity(intent)
                } else {
                    Log.e("InscriptionCandidateActivity", "Failed to create account")
                }
            }

            override fun onFailure(call: Call<JWTLOGIN>, t: Throwable) {
                Log.e("InscriptionCandidateActivity", "Error: ${t.message}")
            }
        })

    }
}