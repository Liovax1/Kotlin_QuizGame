package com.supdevinci.quizgame.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.supdevinci.quizgame.R
import com.supdevinci.quizgame.ui.theme.QuizGameTheme
import androidx.compose.ui.platform.LocalContext
import com.supdevinci.quizgame.data.AppDatabase
import com.supdevinci.quizgame.data.ScoreHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val score = intent.getIntExtra("score", 0)
        val total = intent.getIntExtra("total", 0)
        setContent {
            QuizGameTheme {
                val context = LocalContext.current
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        IconButton(onClick = {
                            (context as? Activity)?.finish()
                        }, modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_back),
                                contentDescription = "Retour",
                                tint = Color.White
                            )
                        }
                        ResultScreen(score = score, total = total) {
                            // Sauvegarde du score dans Room
                            val db = AppDatabase.getDatabase(this@ResultActivity)
                            CoroutineScope(Dispatchers.IO).launch {
                                db.scoreDao().insertScore(ScoreHistory(score = score, total = total))
                            }
                            val intent = Intent(this@ResultActivity, HomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ContextCastToActivity")
@Composable
fun ResultScreen(score: Int, total: Int, onBackHome: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fond_global_app),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Votre score : $score/$total", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 24.dp), color = Color.White)
            Button(
                onClick = onBackHome,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff3366ff))) {

                Text("Retour à l'accueil")
            }
        }
    }
}
