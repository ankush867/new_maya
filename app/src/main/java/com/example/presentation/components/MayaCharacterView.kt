package com.example.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.model.AssistantState
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaPurpleNeon
import com.example.ui.theme.MayaTextPrimary

@Composable
fun MayaCharacterView(
    state: AssistantState,
    speakerAmplitude: Float,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "character_anim")

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura"
    )

    val auraColor = when (state) {
        AssistantState.LISTENING -> MayaCyanNeon
        AssistantState.THINKING -> MayaPurpleNeon
        AssistantState.SPEAKING -> Color(0xFF38BDF8)
        AssistantState.EXECUTING_TOOL -> MayaCyanNeon
        else -> MayaCyanNeon.copy(alpha = 0.6f)
    }

    Box(
        modifier = modifier
            .size(size)
            .offset(y = floatOffset.dp)
            .clickable(onClick = onClick)
            .testTag("maya_character_view"),
        contentAlignment = Alignment.Center
    ) {
        // Glowing Aura background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(auraScale)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            auraColor.copy(alpha = 0.35f),
                            auraColor.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Character Frame
        Surface(
            modifier = Modifier
                .size(size * 0.88f)
                .clip(CircleShape),
            shape = CircleShape,
            color = Color(0xFF0E1726),
            border = BorderStroke(
                2.dp,
                Brush.sweepGradient(
                    listOf(
                        auraColor,
                        MayaPurpleNeon,
                        auraColor
                    )
                )
            ),
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.maya_character),
                    contentDescription = "Maya - Your AI Assistant",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Cyber scanline gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x1000F0FF),
                                    Color.Transparent,
                                    Color(0x30060A14)
                                )
                            )
                        )
                )
            }
        }

        // State indicator pill badge at bottom
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 6.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0E1726).copy(alpha = 0.95f),
            border = BorderStroke(1.dp, auraColor.copy(alpha = 0.7f))
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MayaTextPrimary
                )
            }
        }
    }
}
