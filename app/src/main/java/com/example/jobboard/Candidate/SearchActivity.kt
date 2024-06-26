package com.example.jobboard.Candidate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobboard.API.ApiInterface
import com.example.jobboard.API.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private var JobName: String = ""
    private var city: String = ""
    private var jobOffers = mutableStateOf<List<Job>>(listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        JobName = intent.getStringExtra("JobName") ?: "Développeur"
        city = intent.getStringExtra("city") ?: "Lyon"

        getJobOffersBy(JobName, city)
        // jetpack compose
        setContent {
            MaterialTheme{
                PreviewSearchScreen()
            }
        }
    }

    fun getJobOffersBy(JobName: String, city: String) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.18.31:3020/")
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getJobOffersBy(city, JobName)

        retrofitData.enqueue(object : Callback<List<Job>> {
            override fun onResponse(call: Call<List<Job>>, response: Response<List<Job>>) {
                jobOffers.value = response.body() ?: listOf()
            }

            override fun onFailure(call: Call<List<Job>>, t: Throwable) {
                Log.e("SearchActivity", "Error: ${t.message}")
            }
        })
    }

    @Preview
    @Composable
    fun PreviewSearchScreen() {
        MaterialTheme {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .width(360.dp)
                        .height(640.dp)
                ){
                    headerSearch()
                    SearchBar ()
                    SearchScreen ()
                }

            }
        }
    }

    @Composable
    fun headerSearch() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Recherche d'offres",
                style = MaterialTheme.typography.h4,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }

    @Composable
    fun SearchScreen() {
        Surface(color = Color.White) {
            Column(){
                for(offre in jobOffers.value){
                    CardOffre(offre)
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
                        text = "entreprise",
                        fontSize = 14.sp,
                        color = Color.Gray
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
            intent.getStringExtra("JobName")?.let {
                OutlinedTextField(
                    value = it,
                    onValueChange = { jobName = it },
                    label = { Text("Job") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                intent.getStringExtra("city")?.let {
                    OutlinedTextField(
                        value = it,
                        onValueChange = { city = it },
                        label = { Text("Ville") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                }
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


}



