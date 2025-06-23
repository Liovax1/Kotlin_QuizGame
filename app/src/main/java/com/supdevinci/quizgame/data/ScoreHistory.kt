package com.supdevinci.quizgame.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "score_history")
data class ScoreHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Int,
    val total: Int,
    val date: Long = System.currentTimeMillis()
)
