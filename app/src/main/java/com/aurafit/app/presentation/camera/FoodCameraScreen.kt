package com.aurafit.app.presentation.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurafit.app.R
import com.aurafit.app.core.theme.*
import com.aurafit.app.presentation.dashboard.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodCameraScreen(
    dashboardViewModel: DashboardViewModel,
    onNavigateToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by dashboardViewModel.uiState.collectAsState()

    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var foodContextNote by remember { mutableStateOf("") }
    var showLoggedToast by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var apiKeyInput by remember { mutableStateOf("") }

    // Camera capture launcher (takes photo directly into preview)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            selectedBitmap = bitmap
            dashboardViewModel.discardScannedMeal()
            showLoggedToast = false
        }
    }

    // Gallery upload launcher (loads chosen image into preview)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    if (bitmap != null) {
                        selectedBitmap = bitmap
                        dashboardViewModel.discardScannedMeal()
                        showLoggedToast = false
                    }
                }
            } catch (e: Exception) {
                // Handled gracefully
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
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
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "NutriSnap",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Surface(
                        onClick = {
                            apiKeyInput = dashboardViewModel.getOpenAiKey()
                            showApiKeyDialog = true
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = if (uiState.hasOpenAiKey) Color(0xFF133827) else MacroViolet.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (uiState.hasOpenAiKey) Color(0xFF10B981) else MacroViolet.copy(alpha = 0.35f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                if (uiState.hasOpenAiKey) "🟢 OpenAI Active" else "⚙️ Set OpenAI Key",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.hasOpenAiKey) Color(0xFF6EE7B7) else MacroViolet
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Take a photo or upload an image, then tap Send to fetch accurate nutritional data from ChatGPT.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    lineHeight = 15.sp
                )
            }
        }

        // Camera Viewfinder / Image Preview Area
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceDark)
                    .border(
                        1.dp,
                        if (uiState.isAnalyzingImage) MacroViolet else SlateBorder,
                        RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selectedBitmap != null) {
                    // Show captured or uploaded food photo preview
                    Image(
                        bitmap = selectedBitmap!!.asImageBitmap(),
                        contentDescription = "Food Preview",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Analyzing Loading Overlay
                    if (uiState.isAnalyzingImage) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(24.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = MacroViolet,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(44.dp)
                                )
                                Text(
                                    "Analyzing meal with ChatGPT...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "Fetching accurate ingredients, portion size & macros",
                                    fontSize = 10.sp,
                                    color = Color.LightGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    // Empty State: Prompt to Click Photo or Upload
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MacroViolet.copy(alpha = 0.12f))
                                .border(1.dp, MacroViolet.copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📸", fontSize = 28.sp)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "No Photo Selected",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Take a photo or upload an image to begin",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons: Take Photo & Upload Image (or Send to ChatGPT when photo is selected)
        item {
            if (selectedBitmap == null) {
                // Initial State: Take Photo or Upload Image
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MacroViolet),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("📷 Take Photo", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("🖼️ Upload Image", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            } else if (uiState.activeScannedFoodResult == null) {
                // Photo selected but not sent yet: Show Send to ChatGPT Button and Retake options
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // SEND TO CHATGPT BUTTON
                    Button(
                        onClick = {
                            selectedBitmap?.let { bmp ->
                                dashboardViewModel.analyzeFoodImage(bmp, foodContextNote.ifBlank { null })
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MacroViolet),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !uiState.isAnalyzingImage
                    ) {
                        if (uiState.isAnalyzingImage) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyzing...", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        } else {
                            Text("🚀 Send to ChatGPT", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // Retake or Pick Another Photo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { cameraLauncher.launch(null) },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
                            enabled = !uiState.isAnalyzingImage
                        ) {
                            Text("📷 Retake", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
                            enabled = !uiState.isAnalyzingImage
                        ) {
                            Text("🖼️ Pick Another", fontSize = 11.sp)
                        }

                        TextButton(
                            onClick = {
                                selectedBitmap = null
                                dashboardViewModel.discardScannedMeal()
                            },
                            modifier = Modifier.height(40.dp),
                            enabled = !uiState.isAnalyzingImage
                        ) {
                            Text("Clear", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                // Meal already analyzed: Button to Scan/Take Another Food
                Button(
                    onClick = {
                        selectedBitmap = null
                        dashboardViewModel.discardScannedMeal()
                        showLoggedToast = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("📷 Scan Another Photo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }

        // Optional Note Input Field (shown before analysis)
        if (uiState.activeScannedFoodResult == null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = foodContextNote,
                        onValueChange = { foodContextNote = it },
                        label = { Text("Food Note / Context (Optional)", fontSize = 11.sp) },
                        placeholder = { Text("e.g. 'banana', 'pizza slice', 'chicken salad'", fontSize = 11.sp, color = Color.DarkGray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0D1017),
                            unfocusedContainerColor = Color(0xFF0D1017),
                            focusedBorderColor = MacroViolet,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        enabled = !uiState.isAnalyzingImage
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (uiState.hasOpenAiKey) "🟢 Photo will be sent directly to ChatGPT Vision" else "⚡ Analyzing using smart visual food recognition",
                            fontSize = 10.sp,
                            color = if (uiState.hasOpenAiKey) Color(0xFF6EE7B7) else Color.LightGray
                        )
                        if (!uiState.hasOpenAiKey) {
                            TextButton(
                                onClick = {
                                    apiKeyInput = dashboardViewModel.getOpenAiKey()
                                    showApiKeyDialog = true
                                },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text("Add Key", fontSize = 10.sp, color = MacroViolet)
                            }
                        }
                    }
                }
            }
        }

        // Analysis Error Notice Card
        uiState.imageAnalysisError?.let { errorMsg ->
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF2D181C))
                        .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = errorMsg,
                            fontSize = 10.sp,
                            color = Color(0xFFFCA5A5),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { dashboardViewModel.clearImageAnalysisError() },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Text("✕", fontSize = 10.sp, color = Color(0xFFFCA5A5))
                        }
                    }
                }
            }
        }

        // Analysis Result Card (Accurate Nutrition returned from ChatGPT)
        uiState.activeScannedFoodResult?.let { result ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(SurfaceDark)
                        .border(1.dp, MacroViolet.copy(alpha = 0.35f), RoundedCornerShape(22.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Header Result
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val foodName = result.items.firstOrNull()?.foodName ?: "Analyzed Food"
                                Text(
                                    foodName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                val portion = result.items.firstOrNull()?.portion ?: "1 serving"
                                Text(
                                    "Portion: $portion",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            // Calories Banner
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "${result.totals.calories}",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MacroViolet
                                )
                                Text(
                                    "calories",
                                    fontSize = 10.sp,
                                    color = Color.LightGray
                                )
                            }
                        }

                        Divider(color = Color.White.copy(alpha = 0.08f))

                        // Macronutrient Breakdown
                        Text(
                            "Macronutrients Breakdown",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MacroNutrientPill(
                                label = "Protein",
                                value = "${result.totals.protein}g",
                                color = MacroViolet,
                                modifier = Modifier.weight(1f)
                            )
                            MacroNutrientPill(
                                label = "Carbs",
                                value = "${result.totals.carbs}g",
                                color = WaterSky,
                                modifier = Modifier.weight(1f)
                            )
                            MacroNutrientPill(
                                label = "Fat",
                                value = "${result.totals.fat}g",
                                color = Spo2Magenta,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Food Description / Nutritional Insight from ChatGPT
                        val description = result.items.firstOrNull()?.description
                            ?: result.summary.ifBlank { "Nutritionally evaluated meal item." }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.04f))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    "💡 ChatGPT Nutritional Insight",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.LightGray
                                )
                                Text(
                                    description,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        // Action Buttons: Log to Daily Macros or Discard
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    dashboardViewModel.confirmAndLogScannedMeal()
                                    selectedBitmap = null
                                    showLoggedToast = true
                                },
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WeightTeal),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("✓ Add to Daily Macros", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    selectedBitmap = null
                                    dashboardViewModel.discardScannedMeal()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Discard", fontSize = 12.sp, color = Color.LightGray)
                            }
                        }
                    }
                }
            }
        }

        // Success Toast Notification when logged
        item {
            AnimatedVisibility(
                visible = showLoggedToast,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF133827))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "✓ Meal successfully added to your Dashboard!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6EE7B7)
                        )
                        TextButton(onClick = {
                            showLoggedToast = false
                            onNavigateToDashboard()
                        }) {
                            Text("View Dashboard", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showApiKeyDialog) {
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🤖", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("OpenAI API Key", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Enter your OpenAI API Key (starts with sk-...) to send images to ChatGPT Vision (GPT-4o-mini). If no key is set, the app will run using intelligent local visual food recognition.",
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        lineHeight = 15.sp
                    )

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        placeholder = { Text("sk-Proj-...", fontSize = 11.sp, color = Color.DarkGray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0D1017),
                            unfocusedContainerColor = Color(0xFF0D1017),
                            focusedBorderColor = MacroViolet,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    if (uiState.hasOpenAiKey) {
                        TextButton(
                            onClick = {
                                apiKeyInput = ""
                                dashboardViewModel.clearOpenAiKey()
                                showApiKeyDialog = false
                            },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Text("Clear / Remove Key", fontSize = 11.sp, color = Color(0xFFEF4444))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        dashboardViewModel.saveOpenAiKey(apiKeyInput)
                        showApiKeyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MacroViolet),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Key", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) {
                    Text("Cancel", fontSize = 12.sp, color = Color.Gray)
                }
            }
        )
    }
}

@Composable
private fun MacroNutrientPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(label, fontSize = 9.sp, color = color, fontWeight = FontWeight.Medium)
        }
    }
}
