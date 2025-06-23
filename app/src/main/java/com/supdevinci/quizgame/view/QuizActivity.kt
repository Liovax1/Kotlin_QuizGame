package com.supdevinci.quizgame.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.supdevinci.quizgame.R
import com.supdevinci.quizgame.ui.theme.QuizGameTheme
import com.supdevinci.quizgame.viewmodel.QuizGameViewModel

class QuizActivity : ComponentActivity() {
    private val viewModel: QuizGameViewModel by viewModels()
    private var categoryId: Int = 9
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryId = intent.getIntExtra("categoryId", 9)
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
                        QuizScreen(
                            viewModel = viewModel,
                            categoryId = categoryId,
                            onQuizFinished = { finalScore, total ->
                                val intent = Intent(this@QuizActivity, ResultActivity::class.java)
                                intent.putExtra("score", finalScore)
                                intent.putExtra("total", total)
                                startActivity(intent)
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("ContextCastToActivity")
@Composable
fun QuizScreen(viewModel: QuizGameViewModel, categoryId: Int, onQuizFinished: (Int, Int) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fond_global_app),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        var selectedAnswer by remember { mutableStateOf<String?>(null) }
        val questions by viewModel.questions.collectAsState()
        val currentIndex by viewModel.currentIndex.collectAsState()
        val error by viewModel.error.collectAsState()
        val questionTranslated by viewModel.question.collectAsState()
        val answers by viewModel.answers.collectAsState()
        val score by viewModel.score.collectAsState()

        LaunchedEffect(categoryId) {
            viewModel.fetchQuestions(categoryId)
        }

        if (questions.isNotEmpty() && questionTranslated != null && currentIndex < questions.size && answers.isNotEmpty()) {
            val q = questions[currentIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center, // centrer verticalement
                horizontalAlignment = Alignment.CenterHorizontally // centrer horizontalement
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally // centrer horizontalement
                ) {
                    Text(
                        "Question ${currentIndex + 1}/${questions.size}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp),
                        color = Color.White
                    )
                    Text(
                        questionTranslated ?: "",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 24.dp),
                        color = Color.White
                    )
                    answers.forEach { answer ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = selectedAnswer == answer,
                                onClick = { selectedAnswer = answer },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.White,
                                    unselectedColor = Color.White,
                                    disabledSelectedColor = Color.White,
                                    disabledUnselectedColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(answer, color = Color.White)
                        }
                    }
                }

                Button(
                    onClick = {
                        if (selectedAnswer != null) {
                            // On vérifie la réponse et on incrémente le score dans le ViewModel
                            viewModel.submitAnswer(selectedAnswer!!)
                        }
                        if (currentIndex == questions.size - 1) {
                            onQuizFinished(score, questions.size)
                        } else {
                            viewModel.nextQuestion()
                            selectedAnswer = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff3366ff)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),

                    enabled = selectedAnswer != null
                ) {
                    Text(if (currentIndex == questions.size - 1) "Voir le résultat" else "Question suivante", color = Color.White)
                }
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(error ?: "Erreur inconnue", color = MaterialTheme.colorScheme.error)
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

private fun answerEquivalent(correct: String, answers: List<String>): String? {
    return answers.find { it == correct }
}
