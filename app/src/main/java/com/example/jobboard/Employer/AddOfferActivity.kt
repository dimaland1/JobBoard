package com.example.jobboard.Employer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
        var city by remember { mutableStateOf("") }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Déposer une offre",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                }
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
                InputField(value = nom, onValueChange = { nom = it }, label = "Nom*")
                InputField(value = city, onValueChange = { city = it }, label = "Ville*")
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
                            remuneration = remuneration,
                            city = city
                        )
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Finaliser mon offre", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }

    fun addOfferJob(
        nom: String,
        description: String,
        metier: String,
        periode: String,
        remuneration: String,
        city : String
    ) {

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.18.31:3020/")
            .build()
            .create(ApiInterface::class.java)

        val addJob = AddJob(
            employer_id = 1,
            title = nom,
            description = description,
            target_job = metier,
            period = periode,
            remuneration = remuneration.toInt(),
            location = city,
            status = "Ouvert"
        )

        val retrofitData = retrofitBuilder.addJobOffer(addJob)

        retrofitData.enqueue(
            object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("Retrofit", "Offer added")
                        // retourné à la page précédente
                        val intent = Intent(this@AddOfferActivity, EmployerListingActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.e("Retrofit", "Failed to add offer")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
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
                    .background(Color.White, shape = RoundedCornerShape(4.dp))
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
                    .padding(8.dp)
            )
        }
    }
}
