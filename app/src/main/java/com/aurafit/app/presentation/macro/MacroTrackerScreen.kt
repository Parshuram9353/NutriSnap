package com.aurafit.app.presentation.macro

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurafit.app.core.theme.*
import com.aurafit.app.presentation.dashboard.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacroTrackerScreen(
    dashboardViewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    var foodText by remember { mutableStateOf("") }
    val uiState by dashboardViewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // View title
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("AI Nutrition Tracker", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Type in natural language to parse meals instantly via OpenAI.", fontSize = 11.sp, color = Color.Gray)
            }
        }

        // Capture Field Card
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
                Text(
                    "What did you eat?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                OutlinedTextField(
                    value = foodText,
                    onValueChange = { foodText = it },
                    placeholder = { Text("e.g. 100g cooked rice, 5 eggs, 250ml milk", fontSize = 12.sp, color = Color.DarkGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0D1017),
                        unfocusedContainerColor = Color(0xFF0D1017),
                        focusedBorderColor = MacroViolet,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )



                Button(
                    onClick = {
                        if (foodText.isNotBlank()) {
                            dashboardViewModel.analyzeFoodInput(foodText)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MacroViolet),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !uiState.isAnalyzingFood && foodText.isNotBlank()
                ) {
                    if (uiState.isAnalyzingFood) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Analyze Food Logs", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Display potential API Warnings/Errors
        uiState.foodAnalysisError?.let { err ->
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2D181C))
                        .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = err,
                                fontSize = 10.sp,
                                color = Color(0xFFFCA5A5),
                                lineHeight = 1.3.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { dashboardViewModel.clearFoodAnalysisError() },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Text("✕", fontSize = 10.sp, color = Color(0xFFFCA5A5))
                            }
                        }
                        
                        if (err.contains("429") || err.contains("Quota") || err.contains("API key") || err.contains("API Key") || err.contains("Unauthorized") || err.contains("Http error") || err.contains("HTTP error")) {
                            Button(
                                onClick = { dashboardViewModel.clearOpenAiKey() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.align(Alignment.End).height(26.dp)
                            ) {
                                Text("Clear API Key & Run Offline", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Parsed Table Result Display
        uiState.activeParsedFoodResult?.let { data ->
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark)
                        .border(1.dp, MacroViolet.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Parsed Nutritional Data", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    
                    // Table Rows
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Food Item", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.weight(2f))
                            Text("Cals", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            Text("P", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                            Text("C", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                            Text("F", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                        }

                        data.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.foodName, fontSize = 11.sp, color = Color.White, modifier = Modifier.weight(2f))
                                Text("${item.calories} kcal", fontSize = 11.sp, color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                Text("${item.protein}g", fontSize = 11.sp, color = Color.White, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                                Text("${item.carbs}g", fontSize = 11.sp, color = Color.White, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                                Text("${item.fat}g", fontSize = 11.sp, color = Color.White, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Divider(color = Color.White.copy(alpha = 0.06f))
                        
                        // Totals Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MacroViolet.copy(alpha = 0.05f))
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Sum", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MacroViolet, modifier = Modifier.weight(2f))
                            Text("${data.totals.calories} kcal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MacroViolet, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            Text("${data.totals.protein}g", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MacroViolet, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                            Text("${data.totals.carbs}g", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MacroViolet, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                            Text("${data.totals.fat}g", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MacroViolet, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                dashboardViewModel.confirmAndLogActiveMeal(foodText)
                                foodText = ""
                            },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = WeightTeal),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Confirm & Log", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        
                        Button(
                            onClick = { dashboardViewModel.discardActiveMeal() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Discard", fontSize = 11.sp, color = Color.LightGray)
                        }
                    }
                }
            }
        }

        // Daily targets dashboard indicator
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Daily Nutrition Target",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MacroGoalRing(label = "Protein", value = uiState.proteinConsumed, target = uiState.goals.proteinTargetG, color = MacroViolet)
                    MacroGoalRing(label = "Carbs", value = uiState.carbsConsumed, target = uiState.goals.carbTargetG, color = WaterSky)
                    MacroGoalRing(label = "Fat", value = uiState.fatConsumed, target = uiState.goals.fatTargetG, color = Spo2Magenta)
                }
            }
        }
    }
}

@Composable
fun MacroGoalRing(label: String, value: Int, target: Int, color: Color) {
    val progress = if (target > 0) (value.toFloat() / target).coerceIn(0f, 1f) else 0f
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(50.dp)) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    style = Stroke(width = 3.dp.toPx())
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = progress * 360f,
                    useCenter = false,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Text("${(progress * 100).toInt()}%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 9.sp, color = Color.Gray)
        Text("${value}/${target}g", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}
