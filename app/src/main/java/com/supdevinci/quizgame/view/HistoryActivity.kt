package com.supdevinci.quizgame.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.supdevinci.quizgame.data.AppDatabase
import com.supdevinci.quizgame.data.ScoreHistory
import com.supdevinci.quizgame.ui.theme.QuizGameTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizGameTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HistoryScreen()
                }
            }
        }
    }
}

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    var scores by remember { mutableStateOf<List<ScoreHistory>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            scores = db.scoreDao().getAllScores()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = com.supdevinci.quizgame.R.drawable.fond_global_app),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        IconButton(
            onClick = {
                val intent = Intent(context, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            },
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = com.supdevinci.quizgame.R.drawable.ic_arrow_back),
                contentDescription = "Retour",
                tint = Color.White
            )
        }
        Column(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.Center), // Centrage vertical et horizontal
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Historique des scores",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White // Titre en blanc
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (scores.isEmpty()) {
                Text("Aucun score enregistré.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(scores) { score ->
                        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(score.date))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Score : ${score.score}/${score.total}")
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(date, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                showDialog = true
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))) {
                Text("Supprimer l'historique")
            }
            if (showDialog) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirmation") },
                    text = { Text("Voulez-vous vraiment supprimer tout l'historique des scores ?") },
                    confirmButton = {
                        Button(onClick = {
                            showDialog = false
                            val db = AppDatabase.getDatabase(context)
                            scope.launch {
                                db.scoreDao().deleteAllScores()
                            }
                        }) {
                            Text("Oui")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Non")
                        }
                    }
                )
            }
        }
    }
}
