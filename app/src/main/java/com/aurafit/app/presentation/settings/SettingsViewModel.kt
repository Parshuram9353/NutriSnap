package com.aurafit.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurafit.app.domain.model.UserGoal
import com.aurafit.app.domain.repository.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: HealthRepository
) : ViewModel() {

    val userGoals: StateFlow<UserGoal> = repository.getUserGoals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserGoal()
        )

    fun updateGoals(
        age: Int,
        heightCm: Int,
        weightKg: Double,
        gender: String,
        stepTarget: Int,
        waterTargetMl: Int,
        calorieTarget: Int,
        proteinTargetG: Int,
        carbTargetG: Int,
        fatTargetG: Int
    ) {
        viewModelScope.launch {
            repository.saveUserGoals(
                UserGoal(
                    stepTarget = stepTarget,
                    waterTargetMl = waterTargetMl,
                    calorieTarget = calorieTarget,
                    proteinTargetG = proteinTargetG,
                    carbTargetG = carbTargetG,
                    fatTargetG = fatTargetG,
                    age = age,
                    heightCm = heightCm,
                    weightKg = weightKg,
                    gender = gender
                )
            )
        }
    }
}
