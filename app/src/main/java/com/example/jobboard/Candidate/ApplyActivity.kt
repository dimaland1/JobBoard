package com.example.jobboard.Candidate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jobboard.API.ApiInterface
import com.example.jobboard.API.Apply
import com.example.jobboard.API.Job
import com.example.jobboard.API.applyRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApplyActivity : ComponentActivity() {

    private var id : String = "1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        id = intent.getStringExtra("offre_id") ?: "1"



        setContent {
            MaterialTheme {
                ApplyScreen()
            }
        }
    }

    fun ApplyJob(email : String, dateNaissance : String, nom : String, prenom : String, nationalite : String){
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.1.15:3020/")
            .build()
            .create(ApiInterface::class.java)

        val apply = applyRequest(
            email = email,
            job_id = intent.getStringExtra("offre_id") ?: "1",
            name = nom,
            first_name = prenom,
            nationality = nationalite,
            dateNaissance = dateNaissance
        )


        val retrofitData = retrofitBuilder.apply(apply)


        retrofitData.enqueue(object : Callback<Apply> {
            override fun onResponse(call: Call<Apply>, response: Response<Apply>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@ApplyActivity, ApplicationActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.d("ApplyActivity", "onResponse: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Apply>, t: Throwable) {
                Log.d("ApplyActivity", "onFailure: $t")
            }
        })

}

@Composable
fun ApplyScreen() {
    var prenom by remember { mutableStateOf("") }
    var nom by remember { mutableStateOf("") }
    var nationalite by remember { mutableStateOf("") }
    var dateNaissance by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cvFile by remember { mutableStateOf<String?>(null) }
    var motivationFile by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF2F2F2)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "VOUS CANDIDATER POUR L'OFFRE",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Design Lead",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Chez, Figma",
                fontWeight = FontWeight.Bold,
                color = Color.Blue,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            OutlinedTextField(
                value = prenom,
                onValueChange = { prenom = it },
                label = { Text("Prénom*") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text("Nom*") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nationalite,
                onValueChange = { nationalite = it },
                label = { Text("Nationalité") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = dateNaissance,
                onValueChange = { dateNaissance = it },
                label = { Text("Date de naissance") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email*") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Charger un autre CV ?",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (cvFile != null) {
                FileItem(fileName = cvFile!!) { cvFile = null }
            } else {
                UploadButton(text = "Charger un CV") { cvFile = "Mon_CV_Jhon_Doe.pdf" }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ajouter une lettre de motivation ?",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (motivationFile != null) {
                FileItem(fileName = motivationFile!!) { motivationFile = null }
            } else {
                UploadButton(text = "Charger une lettre de motivation") { motivationFile = "Ma_Lettre_Jhon_Doe.pdf" }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { ApplyJob(
                   email, dateNaissance, nom, prenom, nationalite
                ) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)
            ) {
                Text(text = "Finaliser ma candidature", color = Color.White)
            }
        }
    }
}


@Composable
fun UploadButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text)
    }
}

@Composable
fun FileItem(fileName: String, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFFFEDED), shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(fileName, fontWeight = FontWeight.Bold)
    }
}



}
