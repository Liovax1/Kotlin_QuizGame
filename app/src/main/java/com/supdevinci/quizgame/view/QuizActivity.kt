package com.supdevinci.quizgame.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.supdevinci.quizgame.viewmodel.QuizGameViewModel
import com.supdevinci.quizgame.ui.theme.QuizGameTheme
import kotlinx.coroutines.launch
import java.net.URLDecoder

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
    var score by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    val questions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val error by viewModel.error.collectAsState()
    val questionTranslated by viewModel.question.collectAsState()
    val answers by viewModel.answers.collectAsState()

    LaunchedEffect(categoryId) {
        viewModel.fetchQuestions(categoryId)
    }

    if (questions.isNotEmpty() && questionTranslated != null && currentIndex < questions.size && answers.isNotEmpty()) {
        val q = questions[currentIndex]
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Question ${currentIndex + 1}", style = MaterialTheme.typography.titleLarge)
            Text(questionTranslated ?: "", modifier = Modifier.padding(vertical = 16.dp))
            answers.forEach { answer ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedAnswer == answer,
                        onClick = { selectedAnswer = answer }
                    )
                    Text(answer)
                }
            }
            Button(
                onClick = {
                    if (selectedAnswer == answers.find { it == answerEquivalent(q.correct_answer, answers) }) score++
                    if (currentIndex == questions.size - 1) {
                        onQuizFinished(score, questions.size)
                    } else {
                        viewModel.nextQuestion()
                        selectedAnswer = null
                    }
                },
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(if (currentIndex == questions.size - 1) "Voir le résultat" else "Question suivante")
            }
        }
    } else if (error != null) {
        Text(error ?: "Erreur inconnue", color = MaterialTheme.colorScheme.error)
    } else {
        CircularProgressIndicator()
    }
}

// Fonction utilitaire pour retrouver la bonne réponse traduite dans la liste
private fun answerEquivalent(correct: String, answers: List<String>): String? {
    // On suppose que la traduction conserve l'ordre, sinon on peut améliorer la logique
    return answers.find { it == correct }
}
