package com.example.jobboard.Candidate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.jobboard.API.ApiInterface
import com.example.jobboard.API.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class OffresActivity : ComponentActivity() {

    var nom_utilisateur = ""

    var connected = ""

    var logo_url = ""

    // context
    val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        connected = intent.getStringExtra("connected") ?: "true"
        nom_utilisateur = intent.getStringExtra("nom_utilisateur") ?: "Anonyme"
        logo_url = intent.getStringExtra("logo_url") ?: ""

        setContent {
            val jobOffers = remember { mutableStateOf<List<Job>>(listOf()) }

            val retrofitBuilder = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://192.168.18.31:3020/")
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
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                item {
                    Header(nom_utilisateur)
                    SearchBar()
                    Spacer(modifier = Modifier.height(16.dp))
                }
                items(jobs) { job ->
                    CardOffre(offre = job)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    @Composable
    fun Header(name: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Bonjour,\n$name :)",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if(logo_url == ""){
                CircleAvatar(initial = name[0])
            }else{
                Image(
                    painter = rememberAsyncImagePainter(logo_url),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .clickable(onClick = {
                            val intent = Intent(context, ApplicationActivity::class.java)
                            context.startActivity(intent)
                        })
                )
            }
        }
    }

    @Composable
    fun CircleAvatar(initial: Char) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .background(color = Color.Blue, shape = CircleShape)
                .clickable(onClick = {
                    val intent = Intent(context, ApplicationActivity::class.java)
                    context.startActivity(intent)
                })
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = jobName,
                onValueChange = { jobName = it },
                label = { Text("Job") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Ville") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Button(
                    onClick = {
                        if (city.isNotEmpty() || jobName.isNotEmpty()) {
                            val intent = Intent(context, SearchActivity::class.java).apply {
                                putExtra("JobName", jobName)
                                putExtra("city", city)
                            }
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier
                        .height(56.dp)
                ) {
                    Text(
                        "\uD83D\uDD0E",
                        fontSize = 24.sp,
                    )
                }
            }
        }
    }


    @Composable
    fun CardOffre(offre: Job) {
        val context = LocalContext.current

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable {
                    val intent = Intent(context, ShowOffreActivity::class.java).apply {
                        putExtra("offre_id", offre.id.toString())   // id de l'offre
                        putExtra("connected",connected)
                    }
                    context.startActivity(intent)
                }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = offre.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "📍 ${offre.location}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Publié il y a 2 jours",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Go to job details",
                    tint = Color.Gray
                )
            }
        }
    }
}

