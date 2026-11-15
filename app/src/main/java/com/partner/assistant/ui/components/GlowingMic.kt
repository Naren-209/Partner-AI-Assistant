package com.partner.assistant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.partner.assistant.ui.theme.NeonBlue
import com.partner.assistant.ui.theme.NeonPurple

@Composable
fun GlowingMic(isListening: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")

    // Pulsing animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.4f else 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)

            // Draw outer glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonPurple.copy(alpha = 0.3f), Color.Transparent),
                    center = center,
                    radius = size.minDimension / 2 * pulseScale
                ),
                radius = size.minDimension / 2 * pulseScale,
                center = center
            )

            // Draw neon ring
            drawCircle(
                brush = Brush.sweepGradient(colors = listOf(NeonBlue, NeonPurple, NeonBlue)),
                radius = size.minDimension / 2.5f,
                center = center,
                style = Stroke(width = 4.dp.toPx())
            )
        }

        // Inner solid circle
        Canvas(modifier = Modifier.size(180.dp)) {
            drawCircle(
                color = Color(0xFF1A1A2E),
                radius = size.minDimension / 2
            )
        }

        // The Mic Icon
        Icon(
            imageVector = Icons.Filled.Mic,
            contentDescription = "Microphone",
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )
    }
}