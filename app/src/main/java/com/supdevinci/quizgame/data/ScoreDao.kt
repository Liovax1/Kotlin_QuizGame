package com.supdevinci.quizgame.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {
    @Insert
    suspend fun insertScore(score: ScoreHistory)

    @Query("SELECT * FROM score_history ORDER BY date DESC")
    suspend fun getAllScores(): List<ScoreHistory>

    @Query("DELETE FROM score_history")
    suspend fun deleteAllScores()
}
