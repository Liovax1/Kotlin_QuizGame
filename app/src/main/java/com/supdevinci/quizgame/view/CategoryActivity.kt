package com.supdevinci.quizgame.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.supdevinci.quizgame.ui.theme.QuizGameTheme

data class Category(val name: String, val id: Int)

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
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Choisissez une catégorie", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        // Zone de sélection stylisée différemment
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(16.dp)
        ) {
            Text(
                text = categories[selectedIndex].name,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onStartQuiz(categories[selectedIndex].id) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Commencer le quiz")
        }
    }
}
