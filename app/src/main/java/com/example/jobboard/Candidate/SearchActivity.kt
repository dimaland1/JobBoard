package com.example.jobboard.Candidate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
            .baseUrl("http://192.168.1.15:3020/")
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
                    cardOffre(offre)
                }
            }
        }
    }


    @Composable
    fun cardOffre(offre: Job) {
        Surface(
            modifier = Modifier
                .padding(8.dp)
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

    @Composable
    fun SearchBar() {
        var JobName by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        val context = LocalContext.current

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            BasicTextField(
                value = JobName,
                onValueChange = { JobName = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.padding(4.dp)) {
                        if (JobName.isEmpty()) {
                            Text("Designer", color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f))
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

                    if(city == "" && JobName == ""){
                        return@Button
                    }
                    // Lorsque le bouton est cliqué, lancez une nouvelle Activity avec searchText comme paramètre
                    val intent = Intent(context, SearchActivity::class.java).apply {
                        putExtra("JobName", JobName)
                        putExtra("city", city)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .background(color = Color.White)
            ) {
                Text("Rechercher",
                    fontSize = 8.sp,
                )
            }
        }
    }

}



