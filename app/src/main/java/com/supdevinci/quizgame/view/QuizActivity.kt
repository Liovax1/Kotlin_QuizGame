package com.supdevinci.quizgame.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.supdevinci.quizgame.viewmodel.QuizGameViewModel
import com.supdevinci.quizgame.ui.theme.QuizGameTheme

class QuizActivity : ComponentActivity() {
    private val viewModel: QuizGameViewModel by viewModels()
    private var categoryId: Int = 9
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryId = intent.getIntExtra("categoryId", 9)
        setContent {
            QuizGameTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    QuizScreen(
                        viewModel = viewModel,
                        categoryId = categoryId,
                        onQuizFinished = { finalScore, total ->
                            val intent = Intent(this, ResultActivity::class.java)
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

@Composable
fun QuizScreen(viewModel: QuizGameViewModel, categoryId: Int, onQuizFinished: (Int, Int) -> Unit) {
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Question ${currentIndex + 1}/${questions.size}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    questionTranslated ?: "",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 24.dp)
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
                            onClick = { selectedAnswer = answer }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(answer)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = selectedAnswer != null
            ) {
                Text(if (currentIndex == questions.size - 1) "Voir le résultat" else "Question suivante")
            }
        }
    } else if (error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(error ?: "Erreur inconnue", color = MaterialTheme.colorScheme.error)
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

private fun answerEquivalent(correct: String, answers: List<String>): String? {
    return answers.find { it == correct }
}
