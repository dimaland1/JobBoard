package com.example.jobboard.Employer

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
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InscriptionEmployerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ProvideWindowInsets {
                CreateAccountScreen()
            }
        }
    }

    @Composable
    fun CreateAccountScreen() {

        var companyName by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        var cp by remember { mutableStateOf("") }
        var link by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Créer un compte employeur",
                fontSize = MaterialTheme.typography.h5.fontSize,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            TextField(value = companyName, onValueChange = {
                companyName = it
            },
                label = { Text("Nom de l'entreprise") })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(label = { Text("Address") },value = address, onValueChange = {
                address = it
            })
            Spacer(modifier = Modifier.height(16.dp))
            Row {

                TextField(modifier = Modifier.width(150.dp),label = { Text("Ville") },value = city, onValueChange = {
                    city = it
                })
                Spacer(modifier = Modifier.width(16.dp))
                TextField(
                    modifier = Modifier.width(100.dp),
                    label = { Text("Cp") },value = cp, onValueChange = {
                        cp = it
                    })
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(label = { Text("email") },value = email, onValueChange = {
                email = it
            })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(label = { Text("Numéro de telephone") },
                value = phone, onValueChange = {
                    // Vérifier si le numéro de téléphone est valide
                    phone = it
                })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                label = { Text("Mot de passe") },
                value = password, onValueChange = {
                    password = it
                })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(label = { Text("Lien vers le site web") },value = link, onValueChange = {
                link = it
            })
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                // Enregistrer le compte employeur dans la base de données
                saveEmployerAccount(
                    address = address,
                    phone = phone,
                    email = email,
                    city = city,
                    cp = cp,
                    link = link,
                    companyName = companyName,
                    password = password
                )
            }) {
                Text("Créer le compte")
            }
        }
    }

    // Fonction pour enregistrer le compte employeur dans la base de données
    fun saveEmployerAccount(
        address: String,
        phone: String,
        email: String,
        city: String,
        cp: String,
        link: String,
        companyName : String,
        password: String
    ) {

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.18.31:3020/")
            .build()
            .create(ApiInterface::class.java)

        val registerRequest = registerRequest(
            "",
            "",
            "",
            "",
            phone,
            email,
            city,
            "",
            password,
            address,
            cp,
            link,
            companyName,
            "employer"
        )

        val retrofitData = retrofitBuilder.register(registerRequest)

        retrofitData.enqueue(object : Callback<JWTLOGIN> {
            override fun onResponse(call: Call<JWTLOGIN>, response: Response<JWTLOGIN>) {
                if (response.isSuccessful) {
                    Log.e("InscriptionEmployerActivity", "Account created successfully")
                    // ouvrir l'intent de la page d'accueil
                    val intent = Intent(this@InscriptionEmployerActivity, ConnexionActivity::class.java).apply {
                    }
                    startActivity(intent)
                } else {
                    Log.e("InscriptionEmployerActivity", "Failed to create account")
                }
            }

            override fun onFailure(call: Call<JWTLOGIN>, t: Throwable) {
                Log.e("InscriptionEmployerActivity", "Error: ${t.message}")
            }
        })

    }
}
