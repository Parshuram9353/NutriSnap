package com.aurafit.app.presentation.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.aurafit.app.R
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurafit.app.core.theme.*
import com.aurafit.app.domain.model.MacroLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToCamera: () -> Unit = {},
    onNavigateToTextLog: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val todayFormatted = remember {
        SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date())
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.nutrisnap_logo),
                        contentDescription = "NutriSnap Logo",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "NutriSnap",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = todayFormatted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MacroViolet.copy(alpha = 0.15f))
                        .border(1.dp, MacroViolet.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        "AI Vision Active",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MacroViolet
                    )
                }
            }
        }

        // Daily Calories Overview Card
        item {
            val calorieTarget = uiState.goals.calorieTarget
            val caloriesConsumed = uiState.caloriesConsumed
            val calorieProgress = if (calorieTarget > 0) {
                (caloriesConsumed.toFloat() / calorieTarget).coerceIn(0f, 1f)
            } else 0f
            val remainingCalories = (calorieTarget - caloriesConsumed).coerceAtLeast(0)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SlateBorder, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Daily Calories",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    "$caloriesConsumed",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    " / $calorieTarget kcal",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                                )
                            }
                        }

                        // Circular Progress Dial
                        Box(contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.size(72.dp)) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.06f),
                                    style = Stroke(width = 7.dp.toPx())
                                )
                                drawArc(
                                    brush = Brush.sweepGradient(listOf(MacroViolet, WaterSky, MacroViolet)),
                                    startAngle = -90f,
                                    sweepAngle = calorieProgress * 360f,
                                    useCenter = false,
                                    style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            Text(
                                "${(calorieProgress * 100).toInt()}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Remaining calories banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.04f))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Remaining Today",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                            Text(
                                "🔥 $remainingCalories kcal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MacroViolet
                            )
                        }
                    }
                }
            }
        }

        // Macronutrients Breakdown Cards (Protein, Carbs, Fat)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Macronutrient Targets",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MacroTargetCard(
                        title = "Protein",
                        consumed = uiState.proteinConsumed,
                        target = uiState.goals.proteinTargetG,
                        color = MacroViolet,
                        modifier = Modifier.weight(1f)
                    )
                    MacroTargetCard(
                        title = "Carbs",
                        consumed = uiState.carbsConsumed,
                        target = uiState.goals.carbTargetG,
                        color = WaterSky,
                        modifier = Modifier.weight(1f)
                    )
                    MacroTargetCard(
                        title = "Fat",
                        consumed = uiState.fatConsumed,
                        target = uiState.goals.fatTargetG,
                        color = Spo2Magenta,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }



        // Today's Meals History List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Today's Logged Meals",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                TextButton(onClick = onNavigateToTextLog) {
                    Text("+ Add via Text", fontSize = 11.sp, color = MacroViolet, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (uiState.todayMacrosList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceDark)
                        .border(1.dp, SlateBorder, RoundedCornerShape(18.dp))
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🥗", fontSize = 32.sp)
                        Text(
                            "No meals logged today yet",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Tap 'Open Camera' to take a photo of your food!",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(uiState.todayMacrosList) { meal ->
                MealHistoryItem(
                    meal = meal,
                    onDelete = { viewModel.deleteMacro(meal) }
                )
            }
        }
    }
}

@Composable
private fun MacroTargetCard(
    title: String,
    consumed: Int,
    target: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (target > 0) (consumed.toFloat() / target).coerceIn(0f, 1f) else 0f

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .border(1.dp, SlateBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(54.dp)) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.05f),
                        style = Stroke(width = 4.dp.toPx())
                    )
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = progress * 360f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Text(
                    "${(progress * 100).toInt()}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${consumed}g",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "/ ${target}g",
                    fontSize = 9.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun MealHistoryItem(
    meal: MacroLog,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.foodName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("P: ${meal.protein}g", fontSize = 10.sp, color = MacroViolet, fontWeight = FontWeight.SemiBold)
                    Text("C: ${meal.carbs}g", fontSize = 10.sp, color = WaterSky, fontWeight = FontWeight.SemiBold)
                    Text("F: ${meal.fat}g", fontSize = 10.sp, color = Spo2Magenta, fontWeight = FontWeight.SemiBold)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "${meal.calories} kcal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Text("✕", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}
