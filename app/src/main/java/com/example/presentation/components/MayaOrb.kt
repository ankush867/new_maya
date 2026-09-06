package com.example.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.core.model.AssistantState
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaPurpleNeon
import com.example.ui.theme.MayaRedNeon
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MayaOrb(
    state: AssistantState,
    amplitude: Float,
    modifier: Modifier = Modifier,
    size: Dp = 130.dp,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_anim")

    // Slow breathing for Idle
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    // Fast rotation for Thinking
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulse for waves
    val pulseFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    val baseGlowColor = when (state) {
        AssistantState.LISTENING -> MayaCyanNeon
        AssistantState.THINKING -> MayaPurpleNeon
        AssistantState.SPEAKING -> Color(0xFF38BDF8)
        AssistantState.ERROR -> MayaRedNeon
        AssistantState.OFFLINE -> Color(0xFF64748B)
        else -> MayaCyanNeon
    }

    val dynamicScale = when (state) {
        AssistantState.SPEAKING, AssistantState.LISTENING -> 1f + (amplitude * 0.35f)
        AssistantState.THINKING -> 1.02f
        AssistantState.IDLE -> breatheScale
        else -> 1f
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(dynamicScale)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .testTag("maya_central_orb"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2, this.size.height / 2)
            val radius = this.size.minDimension / 2.3f

            // Outer expanding rings during listening / speaking
            if (state == AssistantState.LISTENING || state == AssistantState.SPEAKING) {
                val waveRadius1 = radius + (pulseFraction * 24.dp.toPx())
                val waveAlpha1 = (1f - pulseFraction).coerceIn(0f, 0.8f)
                drawCircle(
                    color = baseGlowColor.copy(alpha = waveAlpha1 * 0.5f),
                    radius = waveRadius1,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )

                val waveRadius2 = radius + (((pulseFraction + 0.5f) % 1f) * 24.dp.toPx())
                val waveAlpha2 = (1f - ((pulseFraction + 0.5f) % 1f)).coerceIn(0f, 0.8f)
                drawCircle(
                    color = MayaPurpleNeon.copy(alpha = waveAlpha2 * 0.4f),
                    radius = waveRadius2,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Thinking rotating particle arcs
            if (state == AssistantState.THINKING) {
                for (i in 0 until 6) {
                    val angleRad = Math.toRadians((rotationAngle + i * 60).toDouble())
                    val pX = center.x + (radius + 8.dp.toPx()) * cos(angleRad).toFloat()
                    val pY = center.y + (radius + 8.dp.toPx()) * sin(angleRad).toFloat()
                    drawCircle(
                        color = if (i % 2 == 0) MayaCyanNeon else MayaPurpleNeon,
                        radius = 3.dp.toPx(),
                        center = Offset(pX, pY)
                    )
                }
            }

            // Main glowing background gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        baseGlowColor.copy(alpha = 0.9f),
                        baseGlowColor.copy(alpha = 0.5f),
                        Color(0x000E1726)
                    ),
                    center = center,
                    radius = radius * 1.3f
                ),
                radius = radius * 1.2f,
                center = center
            )

            // Inner dark neon core
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0E1E38),
                        Color(0xFF0A0F1D)
                    )
                ),
                radius = radius * 0.88f,
                center = center
            )

            // Core neon border ring
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        baseGlowColor,
                        MayaPurpleNeon,
                        baseGlowColor
                    )
                ),
                radius = radius * 0.88f,
                center = center,
                style = Stroke(width = 2.5.dp.toPx())
            )
        }

        // Center Icon
        val icon = when (state) {
            AssistantState.SPEAKING -> Icons.Default.Stop
            AssistantState.OFFLINE -> Icons.Default.MicOff
            else -> Icons.Default.Mic
        }

        Icon(
            imageVector = icon,
            contentDescription = "Microphone control - ${state.label}",
            tint = baseGlowColor,
            modifier = Modifier.size(size * 0.38f)
        )
    }
}
