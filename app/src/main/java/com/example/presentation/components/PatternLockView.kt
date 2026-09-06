package com.example.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaPurpleNeon
import kotlin.math.hypot

@Composable
fun PatternLockView(
    modifier: Modifier = Modifier,
    pattern: List<Int>,
    onPatternChange: (List<Int>) -> Unit,
    activeColor: Color = MayaCyanNeon,
    inactiveColor: Color = Color(0xFF334155),
    lineColor: Color = MayaCyanNeon
) {
    val haptic = LocalHapticFeedback.current
    var currentTouchPosition by remember { mutableStateOf<Offset?>(null) }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentTouchPosition = offset
                            val dot = findDotAt(offset, size.width.toFloat(), size.height.toFloat())
                            if (dot != null && !pattern.contains(dot)) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onPatternChange(listOf(dot))
                            }
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentTouchPosition = change.position
                            val dot = findDotAt(change.position, size.width.toFloat(), size.height.toFloat())
                            if (dot != null && !pattern.contains(dot)) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                val updated = pattern.toMutableList().apply { add(dot) }
                                onPatternChange(updated)
                            }
                        },
                        onDragEnd = {
                            currentTouchPosition = null
                        },
                        onDragCancel = {
                            currentTouchPosition = null
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val dot = findDotAt(offset, size.width.toFloat(), size.height.toFloat())
                        if (dot != null) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (pattern.contains(dot)) {
                                val updated = pattern.filter { it != dot }
                                onPatternChange(updated)
                            } else {
                                val updated = pattern + dot
                                onPatternChange(updated)
                            }
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val cellWidth = width / 3f
            val cellHeight = height / 3f

            fun getDotCenter(index: Int): Offset {
                val col = index % 3
                val row = index / 3
                return Offset(
                    x = cellWidth * (col + 0.5f),
                    y = cellHeight * (row + 0.5f)
                )
            }

            // Draw connecting lines between connected pattern dots
            if (pattern.isNotEmpty()) {
                val path = Path()
                val firstCenter = getDotCenter(pattern.first())
                path.moveTo(firstCenter.x, firstCenter.y)

                for (i in 1 until pattern.size) {
                    val pt = getDotCenter(pattern[i])
                    path.lineTo(pt.x, pt.y)
                }

                // If user is currently dragging, draw line to finger
                currentTouchPosition?.let { touchPos ->
                    path.lineTo(touchPos.x, touchPos.y)
                }

                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(
                        width = 8.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            // Draw the 9 dots
            for (i in 0..8) {
                val center = getDotCenter(i)
                val isSelected = pattern.contains(i)

                if (isSelected) {
                    // Outer glow/ring
                    drawCircle(
                        color = activeColor.copy(alpha = 0.25f),
                        radius = 28.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = activeColor,
                        radius = 12.dp.toPx(),
                        center = center
                    )
                } else {
                    // Inactive dot
                    drawCircle(
                        color = inactiveColor,
                        radius = 8.dp.toPx(),
                        center = center
                    )
                }
            }
        }
    }
}

private fun findDotAt(offset: Offset, width: Float, height: Float): Int? {
    val cellWidth = width / 3f
    val cellHeight = height / 3f
    val hitRadius = cellWidth * 0.40f

    for (i in 0..8) {
        val col = i % 3
        val row = i / 3
        val centerX = cellWidth * (col + 0.5f)
        val centerY = cellHeight * (row + 0.5f)
        val distance = hypot(offset.x - centerX, offset.y - centerY)
        if (distance <= hitRadius) {
            return i
        }
    }
    return null
}
