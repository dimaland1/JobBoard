package com.example.jobboard.Candidate

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobboard.API.ApiInterface
import com.example.jobboard.API.Job
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApplicationActivity : AppCompatActivity() {
    private var id: String = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        id = intent.getStringExtra("offre_id") ?: "1"

        enableEdgeToEdge()

        setContent {
            ProvideWindowInsets {
                MaterialTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        ApplicationScreen()
                    }
                }
            }
        }
    }

    private fun getApplications(
        candidateId: String,
        onResult: (List<Job>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.18.31:3020/")
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getApplications(candidateId)

        retrofitData.enqueue(object : Callback<List<Job>> {
            override fun onResponse(call: Call<List<Job>>, response: Response<List<Job>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onResult(it)
                    } ?: onError(Throwable("Response body is null"))
                } else {
                    onError(Throwable("Response is not successful"))
                }
            }

            override fun onFailure(call: Call<List<Job>>, t: Throwable) {
                onError(t)
            }
        })
    }

    @Composable
    fun ApplicationScreen() {
        // Utiliser remember pour conserver l'état des candidatures
        var candidatures by remember { mutableStateOf<List<Job>>(emptyList()) }
        var error by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            getApplications(id,
                onResult = {
                    candidatures = it
                },
                onError = {
                    error = it.message
                })
        }

        Column(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Button(
                onClick = {
                    getApplications(id,
                        onResult = {
                            candidatures = it
                        },
                        onError = {
                            error = it.message
                        })
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3A86FF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Rafraîchir les candidatures", color = Color.White)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(candidatures) { job ->
                    JobItem(job)
                }
            }

            error?.let {
                Text(text = it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }

    @Composable
    fun JobItem(job: Job) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .padding(16.dp)
            ) {
                Text(text = job.title, style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = job.description, style = MaterialTheme.typography.body2)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Location: ${job.location}", style = MaterialTheme.typography.body2)
                if(job.period != "Temps plein") {Text(text = "Période: ${job.period} mois", style = MaterialTheme.typography.body2)} else
                {
                    Text(text = "Période: ${job.period}", style = MaterialTheme.typography.body2)
                }
                Text(text = "Rémunération: ${job.remuneration}€", style = MaterialTheme.typography.body2)
                Text(text = "Statut: ${job.status}", style = MaterialTheme.typography.body2)
            }
        }
    }
}
