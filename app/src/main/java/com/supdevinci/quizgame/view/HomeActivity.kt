
package com.supdevinci.quizgame.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.supdevinci.quizgame.R
import com.supdevinci.quizgame.ui.theme.QuizGameTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizGameTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen(
                        onStartClick = {
                            startActivity(Intent(this, CategoryActivity::class.java))
                        },
                        onHistoryClick = {
                            // TODO: Historique
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onStartClick: () -> Unit, onHistoryClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Quiz Illustration",
            modifier = Modifier.size(140.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text("Bienvenue sur mon quizz", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Testez vos connaissances et développez votre culture générale.",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = onStartClick,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Commencer à jouer")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onHistoryClick,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005DFF))
        ) {
            Text("Historique des scores")
        }
    }
}
