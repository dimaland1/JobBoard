package com.example.jobboard.Employer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.jobboard.API.ApiInterface
import com.example.jobboard.API.ApplicationsResponse
import com.example.jobboard.API.Job
import com.example.jobboard.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ViewJobOfferActivity : AppCompatActivity() {
    private var jobId: String = ""
    private val jobOffer = mutableStateOf<Job?>(null)
    private val applications = mutableStateOf<List<ApplicationsResponse>>(listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jobId = intent.getStringExtra("offre_id").toString()

        setContent {
            ViewJobOfferScreen()
        }

        fetchJobOfferDetails()
        fetchJobApplications()
    }

    private fun fetchJobOfferDetails() {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.18.31:3020/")
            .build()
            .create(ApiInterface::class.java)

        val call = retrofit.getJobOfferById(jobId)
        call.enqueue(object : Callback<Job> {
            override fun onResponse(call: Call<Job>, response: Response<Job>) {
                if (response.isSuccessful) {
                    jobOffer.value = response.body()
                } else {
                    Log.e("Retrofit", "Failed to fetch job details")
                }
            }

            override fun onFailure(call: Call<Job>, t: Throwable) {
                Log.e("Retrofit", "Error: ${t.message}")
            }
        })
    }

    private fun fetchJobApplications() {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.18.31:3020/")
            .build()
            .create(ApiInterface::class.java)

        val call = retrofit.getJobApplications(jobId)
        call.enqueue(object : Callback<List<ApplicationsResponse>> {
            override fun onResponse(
                call: Call<List<ApplicationsResponse>>,
                response: Response<List<ApplicationsResponse>>
            ) {
                if (response.isSuccessful) {
                    applications.value = response.body() ?: listOf()
                } else {
                    Log.e("Retrofit", "Failed to fetch applications")
                }
            }

            override fun onFailure(call: Call<List<ApplicationsResponse>>, t: Throwable) {
                Log.e("Retrofit", "Error: ${t.message}")
            }
        })
    }

    @Composable
    fun ViewJobOfferScreen() {
        val context = LocalContext.current
        val job = jobOffer.value
        val applicationList = applications.value

        Surface(color = Color.White) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
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
                        text = "Déposer un offre",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                }
                job?.let {
                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Chez, Figma",
                        color = Color.Blue,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Nombre de candidatures : ${applicationList.size}",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn {
                        items(applicationList) { application ->
                            ApplicationCard(application = application)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { /* Modify offer logic */ },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .padding(end = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Modifier l'annonce", color = Color.White, fontSize = 16.sp)
                        }
                        Button(
                            onClick = {
                                val retrofit = Retrofit.Builder()
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .baseUrl("http://192.168.18.31:3020/")
                                    .build()
                                    .create(ApiInterface::class.java)

                                val call = retrofit.deleteJobOffer(jobId)
                                call.enqueue(object : Callback<Void> {
                                    override fun onResponse(
                                        call: Call<Void>,
                                        response: Response<Void>
                                    ) {
                                        if (response.isSuccessful) {
                                            // Navigate back or show a success message
                                            val intent = Intent(context, EmployerListingActivity::class.java).apply {
                                                //putExtra("offre_id", offre.id.toString() ) // id de l'offre
                                            }
                                            context.startActivity(intent)
                                        } else {
                                            Log.e("Retrofit", "Failed to delete job offer")
                                        }
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        Log.e("Retrofit", "Error: ${t.message}")
                                    }
                                })
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .padding(start = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Supprimer l'annonce", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ApplicationCard(application: ApplicationsResponse) {
        val parsedDate = application.dateCandidate.substring(0, 10)

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(application.logo_url),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "${application.name} ${application.first_name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Candidat depuis le $parsedDate",
                        fontSize = 14.sp
                    )
                    Text(
                        text = application.status,
                        fontSize = 14.sp,
                        color = when (application.status) {
                            "refusé" -> Color.Red
                            "accepté" -> Color.Green
                            else -> Color.Blue
                        }
                    )
                }
            }
        }
    }
}
