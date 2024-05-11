package com.example.jobboard.Candidate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobboard.API.ApiInterface
import com.example.jobboard.API.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class OffresActivity : ComponentActivity() {

    var nom_utilisateur = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        nom_utilisateur = intent.getStringExtra("nom_utilisateur") ?: "Anonyme"

        setContent {
            val jobOffers = remember { mutableStateOf<List<Job>>(listOf()) }

            val retrofitBuilder = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://192.168.1.15:3020/")
                .build()
                .create(ApiInterface::class.java)

            val retrofitData = retrofitBuilder.getJobOffers()

            retrofitData.enqueue(object : Callback<List<Job>?> {
                override fun onResponse(call: Call<List<Job>?>, response: Response<List<Job>?>) {
                    if (response.isSuccessful) {
                        jobOffers.value = response.body() ?: listOf()
                    } else {
                        Log.e("Retrofit", "Failed to get jobs")
                    }
                }

                override fun onFailure(call: Call<List<Job>?>, t: Throwable) {
                    Log.e("Retrofit", "Error: ${t.message}")
                }
            })

            MaterialTheme {
                OffresScreen(jobs = jobOffers.value)
            }
        }
    }

    @Composable
    fun OffresScreen(jobs: List<Job>) {
        Surface {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(640.dp)
            ) {
                header(nom_utilisateur)
                SearchBar()
                listOffres(jobs)
            }
        }
    }

    @Composable
    fun header(name: String) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            // space between the two elements
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Bonjour,\n" +
                            "$name :)",
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.h5
                )
            }
            CircleAvatar(initial = name[0])
        }
    }

    @Composable
    fun CircleAvatar(initial: Char) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .background(color = Color.Blue, shape = CircleShape)
        ) {
            Text(
                text = initial.toString(),
                fontSize = 20.sp,
                style = MaterialTheme.typography.h6,
                color = Color.White
            )
        }
    }

    @Composable
    fun SearchBar() {
        var jobName by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        val context = LocalContext.current

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            BasicTextField(
                value = jobName,
                onValueChange = { jobName = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.padding(4.dp)) {
                        if (jobName.isEmpty()) {
                            Text(
                                "Designer",
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                            )
                        }
                        innerTextField()
                    }
                }
            )
            BasicTextField(
                value = city,
                onValueChange = { city = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.padding(4.dp)) {
                        if (city.isEmpty()) {
                            Text("Lyon", color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f))
                        }
                        innerTextField()
                    }
                }
            )
            Button(
                onClick = {
                    if (city == "" && jobName == "") {
                        return@Button
                    }
                    val intent = Intent(context, SearchActivity::class.java).apply {
                        putExtra("JobName", jobName)
                        putExtra("city", city)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .background(color = Color.White)
            ) {
                Text(
                    "Rechercher",
                    fontSize = 8.sp,
                )
            }
        }
    }

    @Composable
    fun listOffres(offres: List<Job>) {
        Column {
            for (offre in offres) {
                CardOffre(offre = offre)
            }
        }
    }

    @Composable
    fun CardOffre(offre: Job) {
        val context = LocalContext.current

        Surface(
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    val intent = Intent(context, ShowOffreActivity::class.java).apply {
                        // Vous pouvez passer des données à la nouvelle activité si nécessaire
                        putExtra("offre_id", offre.id.toString())
                    }
                    context.startActivity(intent)
                }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = offre.title)
                Text(text = offre.period)
                Text(text = offre.location)
            }
        }
    }

}



