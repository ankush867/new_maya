package com.example.presentation.study

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AssistantRepository
import com.example.presentation.components.GlassCard
import com.example.ui.theme.MayaAmberNeon
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaGreenNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaPurpleNeon
import com.example.ui.theme.MayaRedNeon
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary
import kotlinx.coroutines.launch

data class DrawingPath(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float = 6f
)

@Composable
fun StudyWhiteboardScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AssistantRepository.getInstance(context) }

    val paths = remember { mutableStateListOf<DrawingPath>() }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var selectedColor by remember { mutableStateOf(MayaCyanNeon) }
    var strokeWidth by remember { mutableStateOf(6f) }

    var isSolving by remember { mutableStateOf(false) }
    var aiSolution by remember { mutableStateOf<String?>(null) }

    val colors = listOf(MayaCyanNeon, MayaPurpleNeon, MayaAmberNeon, MayaGreenNeon, Color.White, MayaRedNeon)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .padding(bottom = 80.dp)
            .testTag("study_whiteboard_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MayaTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Interactive Whiteboard",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                    Text(
                        text = "Sketch math, diagrams & ask Maya to solve",
                        fontSize = 11.sp,
                        color = MayaCyanNeon
                    )
                }
            }

            IconButton(onClick = {
                paths.clear()
                currentPoints = emptyList()
                aiSolution = null
            }) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear Canvas",
                    tint = MayaTextSecondary
                )
            }
        }

        // Color & Brush Picker Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (selectedColor == color) 2.5.dp else 0.dp,
                            color = if (selectedColor == color) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { selectedColor = color }
                )
            }
        }

        // Canvas Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF040D1A))
                .border(1.5.dp, MayaCyanNeon.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(selectedColor, strokeWidth) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPoints = listOf(offset)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentPoints = currentPoints + change.position
                            },
                            onDragEnd = {
                                if (currentPoints.isNotEmpty()) {
                                    paths.add(DrawingPath(currentPoints, selectedColor, strokeWidth))
                                    currentPoints = emptyList()
                                }
                            }
                        )
                    }
            ) {
                // Draw existing paths
                paths.forEach { dPath ->
                    if (dPath.points.size > 1) {
                        val path = Path()
                        path.moveTo(dPath.points.first().x, dPath.points.first().y)
                        for (i in 1 until dPath.points.size) {
                            path.lineTo(dPath.points[i].x, dPath.points[i].y)
                        }
                        drawPath(
                            path = path,
                            color = dPath.color,
                            style = Stroke(width = dPath.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }

                // Draw current drawing path
                if (currentPoints.size > 1) {
                    val path = Path()
                    path.moveTo(currentPoints.first().x, currentPoints.first().y)
                    for (i in 1 until currentPoints.size) {
                        path.lineTo(currentPoints[i].x, currentPoints[i].y)
                    }
                    drawPath(
                        path = path,
                        color = selectedColor,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }
        }

        // Bottom AI Solver Action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (paths.isEmpty()) return@Button
                    isSolving = true
                    aiSolution = null

                    scope.launch {
                        // Create bitmap from paths
                        val bitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)
                        val canvas = android.graphics.Canvas(bitmap)
                        canvas.drawColor(android.graphics.Color.BLACK)

                        val paint = Paint().apply {
                            isAntiAlias = true
                            style = Paint.Style.STROKE
                            strokeCap = Paint.Cap.ROUND
                            strokeJoin = Paint.Join.ROUND
                        }

                        paths.forEach { dp ->
                            if (dp.points.size > 1) {
                                paint.color = dp.color.toArgb()
                                paint.strokeWidth = dp.strokeWidth * 1.5f
                                val androidPath = android.graphics.Path()
                                androidPath.moveTo(dp.points.first().x, dp.points.first().y)
                                for (i in 1 until dp.points.size) {
                                    androidPath.lineTo(dp.points[i].x, dp.points[i].y)
                                }
                                canvas.drawPath(androidPath, paint)
                            }
                        }

                        val result = repository.visionService.analyzeImage(
                            bitmap = bitmap,
                            prompt = "This is a whiteboard drawing of a math formula, physics diagram, or note. Explain and solve it step-by-step with clear reasoning.",
                            customKey = repository.settings.value.customApiKey
                        )
                        aiSolution = result
                        isSolving = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MayaCyanNeon),
                shape = RoundedCornerShape(14.dp),
                enabled = !isSolving
            ) {
                if (isSolving) {
                    CircularProgressIndicator(color = Color(0xFF041424), modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Maya is solving...", color = Color(0xFF041424), fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Solve", tint = Color(0xFF041424), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Explain & Solve with Maya", color = Color(0xFF041424), fontWeight = FontWeight.Bold)
                }
            }
        }

        // Solution Output Sheet
        if (aiSolution != null) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "MAYA STEP-BY-STEP SOLUTION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaCyanNeon
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = aiSolution ?: "",
                        fontSize = 13.sp,
                        color = MayaTextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
