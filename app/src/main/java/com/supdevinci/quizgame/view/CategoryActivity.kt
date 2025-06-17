package com.supdevinci.quizgame.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Category(val name: String, val id: Int)

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CategoryScreen { categoryId ->
                    val intent = Intent(this, QuizActivity::class.java)
                    intent.putExtra("categoryId", categoryId)
                    startActivity(intent)
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
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Choisissez une catégorie",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = { expanded = true }) {
            Text(categories[selectedIndex].name)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEachIndexed { index, category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        selectedIndex = index
                        expanded = false
                    }
                )
            }
        }
        Button(
            onClick = { onStartQuiz(categories[selectedIndex].id) },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Commencer le quiz")
        }
    }
}
