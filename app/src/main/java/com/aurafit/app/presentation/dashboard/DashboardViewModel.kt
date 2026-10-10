package com.aurafit.app.presentation.dashboard

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurafit.app.core.security.EncryptedPrefs
import com.aurafit.app.data.network.ParsedFoodItem
import com.aurafit.app.data.network.ParsedNutritionResponse
import com.aurafit.app.data.network.ParsedTotals
import com.aurafit.app.domain.model.MacroLog
import com.aurafit.app.domain.model.UserGoal
import com.aurafit.app.domain.repository.HealthRepository
import com.aurafit.app.domain.usecase.AnalyzeFoodImageUseCase
import com.aurafit.app.domain.usecase.ParseFoodTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class DashboardUiState(
    val caloriesConsumed: Int = 0,
    val proteinConsumed: Int = 0,
    val carbsConsumed: Int = 0,
    val fatConsumed: Int = 0,
    val goals: UserGoal = UserGoal(),
    val todayMacrosList: List<MacroLog> = emptyList(),

    // AI Text Nutrition Parser State
    val isAnalyzingFood: Boolean = false,
    val activeParsedFoodResult: ParsedNutritionResponse? = null,
    val foodAnalysisError: String? = null,

    // AI Camera / Image Vision State
    val isAnalyzingImage: Boolean = false,
    val scannedBitmap: Bitmap? = null,
    val activeScannedFoodResult: ParsedNutritionResponse? = null,
    val imageAnalysisError: String? = null,
    val hasOpenAiKey: Boolean = false,

    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: HealthRepository,
    private val parseFoodTextUseCase: ParseFoodTextUseCase,
    private val analyzeFoodImageUseCase: AnalyzeFoodImageUseCase,
    private val encryptedPrefs: EncryptedPrefs
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        val today = dateFormat.format(Date())
        loadNutritionData(today)
        _uiState.update { it.copy(hasOpenAiKey = encryptedPrefs.getOpenAiKey().isNotBlank()) }
    }

    private fun loadNutritionData(date: String) {
        viewModelScope.launch {
            combine(
                repository.getMacrosForDate(date),
                repository.getUserGoals()
            ) { macros: List<MacroLog>, goals: UserGoal ->
                val totalCal = macros.sumOf { it.calories }
                val totalProt = macros.sumOf { it.protein }
                val totalCarb = macros.sumOf { it.carbs }
                val totalFat = macros.sumOf { it.fat }

                _uiState.value.copy(
                    caloriesConsumed = totalCal,
                    proteinConsumed = totalProt,
                    carbsConsumed = totalCarb,
                    fatConsumed = totalFat,
                    goals = goals,
                    todayMacrosList = macros
                )
            }.catch { e ->
                _uiState.update { it.copy(error = e.localizedMessage) }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    // ==========================================
    // AI Vision Food Camera & Photo Upload Scanner
    // ==========================================

    fun analyzeFoodImage(bitmap: Bitmap, foodHint: String? = null) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAnalyzingImage = true,
                    scannedBitmap = bitmap,
                    activeScannedFoodResult = null,
                    imageAnalysisError = null
                )
            }

            analyzeFoodImageUseCase.execute(bitmap, foodHint)
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            isAnalyzingImage = false,
                            activeScannedFoodResult = response,
                            imageAnalysisError = null
                        )
                    }
                }
                .onFailure { error ->
                    // Fallback to accurate visual spectrum and food database classifier
                    val fallback = analyzeFoodImageUseCase.analyzeBitmapVisually(bitmap, foodHint)
                    val errorNotice = if (error.localizedMessage?.contains("429") == true) {
                        "OpenAI Quota Limit Exceeded. Using intelligent visual analysis."
                    } else if (error.localizedMessage?.contains("401") == true) {
                        "OpenAI Error: Invalid API Key. Using intelligent visual analysis."
                    } else {
                        "Notice: ${error.localizedMessage ?: "Offline"}. Using visual analysis."
                    }
                    _uiState.update {
                        it.copy(
                            isAnalyzingImage = false,
                            activeScannedFoodResult = fallback,
                            imageAnalysisError = errorNotice
                        )
                    }
                }
        }
    }

    fun confirmAndLogScannedMeal() {
        val result = _uiState.value.activeScannedFoodResult ?: return
        val foodName = result.items.firstOrNull()?.foodName ?: "Scanned Meal"
        viewModelScope.launch {
            analyzeFoodImageUseCase.saveParsedMeal(result, "Camera Scan: $foodName")
            _uiState.update {
                it.copy(
                    activeScannedFoodResult = null,
                    scannedBitmap = null,
                    imageAnalysisError = null
                )
            }
        }
    }

    fun discardScannedMeal() {
        _uiState.update {
            it.copy(
                activeScannedFoodResult = null,
                scannedBitmap = null,
                imageAnalysisError = null
            )
        }
    }

    fun clearImageAnalysisError() {
        _uiState.update { it.copy(imageAnalysisError = null) }
    }

    // ==========================================
    // AI Text Nutrition Parser
    // ==========================================

    fun analyzeFoodInput(text: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzingFood = true, foodAnalysisError = null) }

            val apiKey = encryptedPrefs.getOpenAiKey()
            val isOffline = encryptedPrefs.isOfflineMode()
            if (apiKey.isNotBlank() && !isOffline) {
                parseFoodTextUseCase.execute(text)
                    .onSuccess { response ->
                        _uiState.update { it.copy(isAnalyzingFood = false, activeParsedFoodResult = response) }
                    }
                    .onFailure { error ->
                        val fallback = parseFoodLocally(text)
                        val errorText = if (error.localizedMessage?.contains("429") == true) {
                            "OpenAI Quota Exceeded (Error 429). Using offline database."
                        } else {
                            "Notice: ${error.localizedMessage}. Using offline calculations."
                        }
                        _uiState.update {
                            it.copy(
                                isAnalyzingFood = false,
                                activeParsedFoodResult = fallback,
                                foodAnalysisError = errorText
                            )
                        }
                    }
            } else {
                delay(800)
                val response = parseFoodLocally(text)
                _uiState.update { it.copy(isAnalyzingFood = false, activeParsedFoodResult = response) }
            }
        }
    }

    fun confirmAndLogActiveMeal(rawPrompt: String) {
        val result = _uiState.value.activeParsedFoodResult ?: return
        viewModelScope.launch {
            parseFoodTextUseCase.saveParsedMeal(result, rawPrompt)
            _uiState.update { it.copy(activeParsedFoodResult = null, foodAnalysisError = null) }
        }
    }

    fun discardActiveMeal() {
        _uiState.update { it.copy(activeParsedFoodResult = null, foodAnalysisError = null) }
    }

    fun clearFoodAnalysisError() {
        _uiState.update { it.copy(foodAnalysisError = null) }
    }

    fun saveOpenAiKey(key: String) {
        encryptedPrefs.saveOpenAiKey(key.trim())
        _uiState.update { it.copy(hasOpenAiKey = key.trim().isNotBlank()) }
    }

    fun getOpenAiKey(): String = encryptedPrefs.getOpenAiKey()

    fun clearOpenAiKey() {
        encryptedPrefs.saveOpenAiKey("")
        _uiState.update { it.copy(foodAnalysisError = null, hasOpenAiKey = false) }
    }

    fun deleteMacro(macro: MacroLog) {
        viewModelScope.launch {
            repository.deleteMacro(macro)
        }
    }

    // Quick offline dictionary helper
    private fun parseFoodLocally(text: String): ParsedNutritionResponse {
        val clean = text.lowercase()
        val items = mutableListOf<ParsedFoodItem>()
        var totCal = 0
        var totProt = 0
        var totCarb = 0
        var totFat = 0

        val regex = "(\\d+)?\\s*(g|ml|medium|large)?\\s*(apple|apples|banana|bananas|orange|cooked rice|rice|egg white|egg whites|egg|eggs|chicken breast|chicken|milk|pizza|salad|bread)".toRegex()
        val matches = regex.findAll(clean).toList()

        if (matches.isNotEmpty()) {
            for (match in matches) {
                val qty = match.groupValues[1].toIntOrNull() ?: 1
                val name = match.groupValues[3]

                var cal = 0
                var prot = 0
                var carb = 0
                var fat = 0
                var portion = "$qty $name"

                when (name) {
                    "apple", "apples" -> {
                        cal = 95 * qty; prot = 0; carb = 25 * qty; fat = 0; portion = "$qty medium apple (~182g)"
                    }
                    "banana", "bananas" -> {
                        cal = 105 * qty; prot = 1 * qty; carb = 27 * qty; fat = 0; portion = "$qty medium banana (~118g)"
                    }
                    "cooked rice", "rice" -> {
                        val mult = (if (qty <= 10) qty * 100 else qty) / 100f
                        cal = (130 * mult).toInt(); prot = (3 * mult).toInt(); carb = (28 * mult).toInt(); fat = 0; portion = "${(mult * 100).toInt()}g cooked rice"
                    }
                    "egg", "eggs" -> {
                        cal = 70 * qty; prot = 6 * qty; carb = 1 * qty; fat = 5 * qty; portion = "$qty large eggs"
                    }
                    "egg white", "egg whites" -> {
                        cal = 17 * qty; prot = 4 * qty; carb = 0; fat = 0; portion = "$qty egg whites"
                    }
                    "chicken", "chicken breast" -> {
                        val mult = (if (qty <= 10) qty * 100 else qty) / 100f
                        cal = (165 * mult).toInt(); prot = (31 * mult).toInt(); carb = 0; fat = (4 * mult).toInt(); portion = "${(mult * 100).toInt()}g chicken"
                    }
                    "milk" -> {
                        val mult = (if (qty <= 10) qty * 250 else qty) / 100f
                        cal = (60 * mult).toInt(); prot = (3 * mult).toInt(); carb = (5 * mult).toInt(); fat = (3 * mult).toInt(); portion = "${(mult * 100).toInt()}ml milk"
                    }
                    "pizza" -> {
                        cal = 285 * qty; prot = 12 * qty; carb = 36 * qty; fat = 10 * qty; portion = "$qty slice(s) pizza"
                    }
                    "salad" -> {
                        cal = 120 * qty; prot = 4 * qty; carb = 12 * qty; fat = 6 * qty; portion = "$qty bowl(s) salad"
                    }
                    "bread" -> {
                        cal = 80 * qty; prot = 3 * qty; carb = 15 * qty; fat = 1 * qty; portion = "$qty slice(s) bread"
                    }
                }

                items.add(
                    ParsedFoodItem(
                        foodName = name.replaceFirstChar { it.uppercase() },
                        portion = portion,
                        calories = cal,
                        protein = prot,
                        carbs = carb,
                        fat = fat,
                        description = "Accurate reference nutritional values"
                    )
                )
                totCal += cal
                totProt += prot
                totCarb += carb
                totFat += fat
            }
        }

        if (items.isEmpty()) {
            items.add(
                ParsedFoodItem(
                    foodName = text.replaceFirstChar { it.uppercase() },
                    portion = "1 serving",
                    calories = 150,
                    protein = 8,
                    carbs = 20,
                    fat = 4,
                    description = "Estimated nutritional value"
                )
            )
            totCal = 150
            totProt = 8
            totCarb = 20
            totFat = 4
        }

        return ParsedNutritionResponse(
            items = items,
            totals = ParsedTotals(totCal, totProt, totCarb, totFat),
            summary = "Parsed meal nutrients"
        )
    }
}
