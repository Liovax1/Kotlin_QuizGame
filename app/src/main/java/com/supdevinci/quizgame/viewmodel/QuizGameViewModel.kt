package com.supdevinci.quizgame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.supdevinci.quizgame.data.RetrofitInstance
import com.supdevinci.quizgame.model.Question
import com.supdevinci.quizgame.service.Quizz
import java.net.URLDecoder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    val question = MutableStateFlow<String?>(null)
    val answers = MutableStateFlow<List<String>>(emptyList())

    var translator : Translator? = null

    init {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.FRENCH)
            .build()

        translator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        translator!!.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {

            }
            .addOnFailureListener { exception ->
                println("TEST FAILED")
            }
    }

    fun fetchQuestions(categoryId: Int) {
        viewModelScope.launch {
            try {
                val response = quizzApi.getQuestions(category = categoryId)
                if (response.response_code == 0 && response.results.isNotEmpty()) {
                    val translated = response.results.map { q ->
                        q.copy(
                            question = decodeUtf8(q.question),
                            correct_answer = decodeUtf8(q.correct_answer),
                            incorrect_answers = q.incorrect_answers.map { decodeUtf8(it) }
                        )
                    }
                    _questions.value = translated
                    _currentIndex.value = 0
                    _error.value = null
                    translateToFrench(questions.value.get(_currentIndex.value).question)
                } else {
                    _error.value = "Aucune question trouvée."
                }
            } catch (e: Exception) {
                _error.value = "Erreur réseau : ${e.localizedMessage}"
            }
        }
    }

    private fun decodeUtf8(text: String): String {
        return try {
            java.net.URLDecoder.decode(text, "UTF-8")
        } catch (e: Exception) {
            text
        }
    }

    fun translateToFrench(text: String) {
        val q = questions.value.getOrNull(_currentIndex.value)
        if (q == null) return
        // Traduire la question
        translator?.translate(q.question)
            ?.addOnSuccessListener { translatedText ->
                question.value = translatedText
                // Traduire toutes les réponses (correcte + incorrectes)
                val allAnswers = q.incorrect_answers + q.correct_answer
                val translatedList = mutableListOf<String>()
                var count = 0
                allAnswers.forEach { ans ->
                    translator?.translate(ans)
                        ?.addOnSuccessListener { translatedAns ->
                            translatedList.add(translatedAns)
                            count++
                            if (count == allAnswers.size) {
                                answers.value = translatedList.shuffled()
                            }
                        }
                        ?.addOnFailureListener {
                            translatedList.add(ans)
                            count++
                            if (count == allAnswers.size) {
                                answers.value = translatedList.shuffled()
                            }
                        }
                }
                _error.value = ""
            }
            ?.addOnFailureListener { exception ->
                _error.value = exception.message
            }
    }

    fun nextQuestion() {
        if (_currentIndex.value < _questions.value.size - 1) {
            _currentIndex.value += 1
            translateToFrench(questions.value.get(_currentIndex.value).question)
        }
    }

    fun reset() {
        _questions.value = emptyList()
        _currentIndex.value = 0
        _error.value = null
    }
}
