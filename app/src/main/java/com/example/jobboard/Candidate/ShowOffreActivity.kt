package com.example.jobboard.Candidate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.jobboard.API.ApiInterface
import com.example.jobboard.API.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ShowOffreActivity : AppCompatActivity() {

    // offre_id passer en intent
    private var id : String = "1"
    private var jobOffer = mutableStateOf<Job?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val intent = intent
        // reprendre l'id de l'offre passe en intent

        id = intent.getStringExtra("offre_id") ?: "1"

        JobContent(id)

        setContent {
            ShowOffreScreen()
        }
    }

    fun JobContent(id : String){
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.1.15:3020/")
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

        Surface(color = Color.White) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "JobBoard",
                    style = MaterialTheme.typography.h4,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = jobOffer.value?.title ?: "Inconnue",
                    style = MaterialTheme.typography.h4,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Description \n " +
                            "${jobOffer.value?.description ?: "Inconnue"}",
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // ajouter un bouton pour postuler a l'offre
                Button(onClick = {
                    // nouvelle activite pour postuler

                    val intent = Intent(context, ApplyActivity::class.java).apply {
                        putExtra("offre_id", jobOffer.value?.id.toString())
                    }
                    context.startActivity(intent)
                }) {
                    Text("Postuler")
                }
            }
        }
    }
}