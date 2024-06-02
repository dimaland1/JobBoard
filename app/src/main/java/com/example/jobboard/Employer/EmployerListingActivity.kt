package com.example.jobboard.Employer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.jobboard.API.ApiInterface
import com.example.jobboard.API.Job
import com.example.jobboard.Candidate.ShowOffreActivity
import com.example.jobboard.R
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class EmployerListingActivity : AppCompatActivity() {

    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        token = intent.getStringExtra("token").toString()

        setContent {
            ProvideWindowInsets {
                val jobOffers = remember { mutableStateOf<List<Job>>(listOf()) }

                val retrofitBuilder = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://192.168.18.31:3020/")
                    .build()
                    .create(ApiInterface::class.java)

                val retrofitData = retrofitBuilder.getJobOffersByEmployer(1)

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
                            .statusBarsPadding()
                            .navigationBarsPadding()
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 60.dp) // to avoid overlap with the button
                        ) {
                            items(jobOffers.value) { job ->
                                CardOffre(offre = job, nb_offres = 2)
                            }
                        }
                        Button(
                            onClick = {
                                val intent = Intent(this@EmployerListingActivity, AddOfferActivity::class.java).apply {
                                    putExtra("token", token)
                                }
                                startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Ajouter une offre", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CardOffre(offre: Job, nb_offres : Int) {
        val context = LocalContext.current

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable {
                    val intent = Intent(context, ViewJobOfferActivity::class.java).apply {
                        putExtra("offre_id", offre.id.toString() ) // id de l'offre
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
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = offre.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "📍 ${offre.location}", fontSize = 14.sp)
                    Text(
                        text = "${offre.remuneration} €",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    if(offre.period != "Temps plein"){
                        Text(
                            text = "${offre.period} mois",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }else{
                        Text(
                            text = "${offre.period}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
