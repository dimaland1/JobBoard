package com.example.jobboard.Candidate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobboard.API.ApiInterface
import com.example.jobboard.API.Job
import com.example.jobboard.ConnexionActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ShowOffreActivity : AppCompatActivity() {

    private var connected = "";

    private var id: String = "1"
    private var jobOffer = mutableStateOf<Job?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val intent = intent
        connected = intent.getStringExtra("connected") ?: "true"
        id = intent.getStringExtra("offre_id") ?: "1"

        JobContent(id)

        setContent {
            ShowOffreScreen()
        }
    }

    fun JobContent(id: String) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.18.31:3020/")
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getJobOfferById(id)

        retrofitData.enqueue(object : Callback<Job> {
            override fun onResponse(call: Call<Job>, response: Response<Job>) {
                jobOffer.value = response.body() ?: null
            }

            override fun onFailure(call: Call<Job>, t: Throwable) {
                Log.e("SearchActivity", "Error: ${t.message}")
            }
        })
    }

    @Composable
    fun ShowOffreScreen() {
        val context = LocalContext.current
        val job = jobOffer.value

        Surface(color = Color.White) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.height(20.dp) )
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }

                job?.let {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = job.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${job.location}",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Description du poste",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = job.description,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(34.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        if(connected == "true"){
                            Button(
                                onClick = {
                                    val intent = Intent(context, ApplyActivity::class.java).apply {
                                        putExtra("offre_id", job.id.toString())
                                        putExtra("location", job.location)
                                        putExtra("job_title", job.title)
                                    }
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                Text("Postuler Maintenant")
                            }
                        }else{
                            Button(
                                onClick = {
                                    val intent = Intent(context, ConnexionActivity::class.java).apply {
                                    }
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                Text("Connecter vous !")
                            }
                        }
                    }
                } ?: run {
                    Text(text = "Loading...", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}
