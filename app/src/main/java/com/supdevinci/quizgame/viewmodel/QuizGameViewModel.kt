package com.supdevinci.quizgame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.quizgame.data.RetrofitInstance
import com.supdevinci.quizgame.model.Question
import com.supdevinci.quizgame.service.Quizz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.create

class QuizGameViewModel : ViewModel() {
    private val quizzApi = RetrofitInstance.api.create(Quizz::class.java)

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchQuestions(categoryId: Int) {
        viewModelScope.launch {
            try {
                val response = quizzApi.getQuestions(category = categoryId)
                if (response.response_code == 0 && response.results.isNotEmpty()) {
                    _questions.value = response.results
                    _currentIndex.value = 0
                    _error.value = null
                } else {
                    _error.value = "Aucune question trouvée."
                }
            } catch (e: Exception) {
                _error.value = "Erreur réseau : ${e.localizedMessage}"
            }
        }
    }

    fun nextQuestion() {
        if (_currentIndex.value < _questions.value.size - 1) {
            _currentIndex.value += 1
        }
    }

    fun reset() {
        _questions.value = emptyList()
        _currentIndex.value = 0
        _error.value = null
    }
}
