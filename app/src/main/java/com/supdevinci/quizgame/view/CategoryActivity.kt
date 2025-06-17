package com.supdevinci.quizgame.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.supdevinci.quizgame.ui.theme.QuizGameTheme

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizGameTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CategoryScreen { categoryId ->
                        val intent = Intent(this, QuizActivity::class.java)
                        intent.putExtra("categoryId", categoryId)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryScreen(onStartQuiz: (Int) -> Unit) {
    val categories = remember {
        listOf(
            Category("Culture générale", 9),
            Category("Musique", 12),
            Category("Jeux vidéo", 15),
            Category("Science & nature", 17),
            Category("Informatique", 18),
            Category("Mathématiques", 19),
            Category("Sports", 21),
            Category("Géographie", 22),
            Category("Histoire", 23),
            Category("Politique", 24),
            Category("Célébrités", 26),
            Category("Animaux", 27),
            Category("Véhicules", 28)
        )
    }
    var selectedIndex by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Choisissez une catégorie", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
        androidx.compose.material3.DropdownMenu(
            expanded = true,
            onDismissRequest = {},
        ) {
            categories.forEachIndexed { index, category ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = { selectedIndex = index }
                )
            }
        }
        Button(onClick = { onStartQuiz(categories[selectedIndex].id) }, modifier = Modifier.padding(top = 24.dp)) {
            Text("Commencer le quiz")
        }
    }
}

class Category(val name: String, val id: Int)
