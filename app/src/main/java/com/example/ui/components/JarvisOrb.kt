package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BlueSecondary

@Composable
fun JarvisOrb(modifier: Modifier = Modifier, isThinking: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isThinking) 1000 else 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "orbRotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isThinking) 800 else 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "orbScale"
    )

    Box(modifier = modifier.size(100.dp)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val brush = Brush.sweepGradient(
                colors = listOf(BluePrimary, BlueSecondary, BluePrimary, Color.Transparent),
            )
            
            drawCircle(
                brush = brush,
                radius = (size.minDimension / 2) * scale,
                style = Stroke(width = 4.dp.toPx())
            )
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(BlueSecondary.copy(alpha = 0.5f), Color.Transparent)
                ),
                radius = (size.minDimension / 2) * (scale * 0.8f)
            )
        }
    }
}
