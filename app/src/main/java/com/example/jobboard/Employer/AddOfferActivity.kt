package com.example.jobboard.Employer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobboard.API.AddJob
import com.example.jobboard.API.ApiInterface
import com.example.jobboard.API.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddOfferActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddOfferScreen()
        }
    }

    @Composable
    fun AddOfferScreen() {
        var entreprise by remember { mutableStateOf("") }
        var nom by remember { mutableStateOf("") }
        var metier by remember { mutableStateOf("") }
        var remuneration by remember { mutableStateOf("") }
        var periode by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var responsabilite by remember { mutableStateOf("") }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Votre offre",
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = nom,
                    style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Chez, $entreprise",
                    color = Color.Blue,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                InputField(value = entreprise, onValueChange = { entreprise = it }, label = "Entreprise*")
                InputField(value = nom, onValueChange = { nom = it }, label = "Titre du métier*")
                InputField(value = metier, onValueChange = { metier = it }, label = "Métier ciblé*")
                InputField(value = remuneration, onValueChange = { remuneration = it }, label = "Rémunération", keyboardType = KeyboardType.Number)
                InputField(value = periode, onValueChange = { periode = it }, label = "Période (mois)*", keyboardType = KeyboardType.Number)
                InputField(value = description, onValueChange = { description = it }, label = "Description")
                InputField(value = responsabilite, onValueChange = { responsabilite = it }, label = "Responsabilité")

                Button(
                    onClick = {
                        addOfferJob(
                            nom = nom,
                            description = description,
                            metier = metier,
                            periode = periode,
                            remuneration = remuneration
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(text = "Finaliser mon offre")
                }
            }
        }
    }

    fun addOfferJob(
        nom: String,
        description: String,
        metier: String,
        periode: String,
        remuneration: String
    ) {

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.1.15:3020/")
            .build()
            .create(ApiInterface::class.java)

        val addJob = AddJob(
            employer_id = 5,
            title = nom,
            description = description,
            target_job = metier,
            period = periode,
            remuneration = remuneration.toInt(),
            location = "Paris",
            status = "open"
        )

        val retrofitData = retrofitBuilder.addJobOffer(addJob)

        retrofitData.enqueue(
            object : Callback<Job> {
                override fun onResponse(call: Call<Job>, response: Response<Job>) {
                    if (response.isSuccessful) {
                        Log.d("Retrofit", "Offer added")
                        // retourné à la page précédente
                        val intent = Intent(this@AddOfferActivity, EmployerListingActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.e("Retrofit", "Failed to add offer")
                    }
                }

                override fun onFailure(call: Call<Job>, t: Throwable) {
                    Log.e("Retrofit", "Error: ${t.message}")
                }
            }
        )
    }

    @Composable
    fun InputField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        keyboardType: KeyboardType = KeyboardType.Text
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)) {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp)
                    .border(1.dp, Color.Gray)
            )
        }
    }
}