package com.example.jobboard.Employer

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.jobboard.API.ApiInterface
import com.example.jobboard.API.Job
import com.example.jobboard.Candidate.ShowOffreActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import com.example.jobboard.R
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class EmployerListingActivity : AppCompatActivity() {

    var token = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        token = intent.getStringExtra("token").toString()

        setContent {
            val jobOffers = remember { mutableStateOf<List<Job>>(listOf()) }

            val retrofitBuilder = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://192.168.1.15:3020/")
                .build()
                .create(ApiInterface::class.java)

            val retrofitData = retrofitBuilder.getJobOffersByEmployer(5)

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

            Surface {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 60.dp) // to avoid overlap with the button
                    ) {
                        items(jobOffers.value) { job ->
                            CardOffre(offre = job)
                        }
                    }
                    Button(
                        onClick = {
                            val intent = Intent(this@EmployerListingActivity, AddOfferActivity::class.java).apply {
                                putExtra("token", token)
                            }
                            startActivity(intent)
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Ajouter une offre")
                    }
                }
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
                        putExtra("offre_id", offre.id.toString())   // id de l'offre
                    }
                    context.startActivity(intent)
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = offre.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = offre.period, fontSize = 14.sp)
                    Text(text = offre.location, fontSize = 14.sp)
                    Text(
                        text = offre.status,
                        fontSize = 14.sp,
                        color = when (offre.status) {
                            "refusé" -> Color.Red
                            "accepté" -> Color.Green
                            else -> Color.Black
                        }
                    )
                }
            }
        }
    }
}