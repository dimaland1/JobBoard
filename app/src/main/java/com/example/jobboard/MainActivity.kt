package com.example.jobboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jobboard.Candidate.OffresActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LoginScreen(onLoginClick = {
                startActivity(Intent(this@MainActivity, ConnexionActivity::class.java))
            }, onAnonymeClick = {
                startActivity(Intent(this@MainActivity, OffresActivity::class.java).apply {
                    putExtra("connected", "false")
                })
            })
        }

    }


}

@Composable
fun LoginScreen(onLoginClick: () -> Unit, onAnonymeClick: () -> Unit) {
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
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(text = "Connexion")
            }

            Button(
                onClick = onAnonymeClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Connecter en tant qu'invité")
            }
        }
    }
}


@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen({}, {})
}
