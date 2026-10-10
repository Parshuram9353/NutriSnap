package com.aurafit.app.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.aurafit.app.core.security.EncryptedPrefs
import com.aurafit.app.core.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    encryptedPrefs: EncryptedPrefs,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val goals by viewModel.userGoals.collectAsState()

    var apiKey by remember { mutableStateOf(encryptedPrefs.getOpenAiKey()) }
    var supabaseUrl by remember { mutableStateOf(encryptedPrefs.getSupabaseUrl()) }
    var supabaseKey by remember { mutableStateOf(encryptedPrefs.getSupabaseKey()) }
    var cloudSyncEnabled by remember { mutableStateOf(!encryptedPrefs.isOfflineMode()) }
    var showSavedMessage by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Settings & Nutrition Goals", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Configure your daily macros, calorie targets, and ChatGPT Vision backend.", fontSize = 11.sp, color = Color.Gray)
            }
        }

        // Daily Nutrition Targets Configuration
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("🎯 Daily Nutrition & Macro Goals", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)

                var localCalories by remember { mutableStateOf("") }
                var localProtein by remember { mutableStateOf("") }
                var localCarbs by remember { mutableStateOf("") }
                var localFat by remember { mutableStateOf("") }

                LaunchedEffect(goals) {
                    localCalories = goals.calorieTarget.toString()
                    localProtein = goals.proteinTargetG.toString()
                    localCarbs = goals.carbTargetG.toString()
                    localFat = goals.fatTargetG.toString()
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = localCalories,
                        onValueChange = { localCalories = it },
                        label = { Text("Calories (kcal)", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0D1017),
                            unfocusedContainerColor = Color(0xFF0D1017),
                            focusedBorderColor = MacroViolet,
                            unfocusedBorderColor = SlateBorder
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = localProtein,
                        onValueChange = { localProtein = it },
                        label = { Text("Protein (g)", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0D1017),
                            unfocusedContainerColor = Color(0xFF0D1017),
                            focusedBorderColor = MacroViolet,
                            unfocusedBorderColor = SlateBorder
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = localCarbs,
                        onValueChange = { localCarbs = it },
                        label = { Text("Carbohydrates (g)", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0D1017),
                            unfocusedContainerColor = Color(0xFF0D1017),
                            focusedBorderColor = WaterSky,
                            unfocusedBorderColor = SlateBorder
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = localFat,
                        onValueChange = { localFat = it },
                        label = { Text("Fats (g)", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0D1017),
                            unfocusedContainerColor = Color(0xFF0D1017),
                            focusedBorderColor = Spo2Magenta,
                            unfocusedBorderColor = SlateBorder
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Button(
                    onClick = {
                        val calVal = localCalories.toIntOrNull() ?: goals.calorieTarget
                        val protVal = localProtein.toIntOrNull() ?: goals.proteinTargetG
                        val carbVal = localCarbs.toIntOrNull() ?: goals.carbTargetG
                        val fatVal = localFat.toIntOrNull() ?: goals.fatTargetG

                        viewModel.updateGoals(
                            age = goals.age,
                            heightCm = goals.heightCm,
                            weightKg = goals.weightKg,
                            gender = goals.gender,
                            stepTarget = goals.stepTarget,
                            waterTargetMl = goals.waterTargetMl,
                            calorieTarget = calVal,
                            proteinTargetG = protVal,
                            carbTargetG = carbVal,
                            fatTargetG = fatVal
                        )
                        showSavedMessage = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MacroViolet),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Nutrition Goals", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                if (showSavedMessage) {
                    Text(
                        "✓ Targets updated successfully!",
                        fontSize = 11.sp,
                        color = Color(0xFF6EE7B7),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        // OpenAI ChatGPT API Configuration
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🤖", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ChatGPT Backend Configuration", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Text(
                    "Insert your OpenAI API Key (`sk-...`) to enable live GPT-4o-mini Vision food recognition. The AI model analyzes photo pixels to identify dishes, portions, calories, protein, carbs, and fats.",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    lineHeight = 15.sp
                )

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = {
                        apiKey = it
                        encryptedPrefs.saveOpenAiKey(it)
                    },
                    placeholder = { Text("sk-Proj-...", fontSize = 11.sp, color = Color.DarkGray) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0D1017),
                        unfocusedContainerColor = Color(0xFF0D1017),
                        focusedBorderColor = MacroViolet,
                        unfocusedBorderColor = SlateBorder
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (apiKey.isNotBlank()) "✓ API Key Configured" else "⚡ Running in Built-in AI Mode",
                        fontSize = 10.sp,
                        color = if (apiKey.isNotBlank()) Color(0xFF6EE7B7) else MacroViolet,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (apiKey.isNotBlank()) {
                        TextButton(onClick = {
                            apiKey = ""
                            encryptedPrefs.saveOpenAiKey("")
                        }) {
                            Text("Clear Key", fontSize = 10.sp, color = Color(0xFFFCA5A5))
                        }
                    }
                }
            }
        }

        // Storage & Offline Mode
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("🔒 Privacy & Storage Mode", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(3f)) {
                        Text("Local-First Storage", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text("All nutrition logs and meal records are saved in your local encrypted Room database.", fontSize = 10.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = !cloudSyncEnabled,
                        onCheckedChange = {
                            cloudSyncEnabled = !it
                            encryptedPrefs.saveOfflineMode(it)
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = MacroViolet)
                    )
                }
            }
        }
    }
}
