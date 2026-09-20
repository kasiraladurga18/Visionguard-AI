package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HighContrastCyan
import com.example.ui.theme.HighContrastRed
import com.example.ui.theme.HighContrastYellow
import com.example.ui.theme.OnPrimaryDark

@Composable
fun AccessibleActionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color = Color.White,
    borderColor: Color = Color.White.copy(alpha = 0.3f),
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
            .testTag(testTag)
            .semantics {
                this.role = Role.Button
                this.contentDescription = "$title, $subtitle. Double tap to activate."
            }
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = BorderStroke(2.dp, borderColor),
        tonalElevation = 6.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = contentColor.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
fun AnimatedScanningFrame(
    isScanning: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan_animation")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_laser"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val strokeWidth = 5.dp.toPx()
        val cornerLen = 40.dp.toPx()
        val w = size.width
        val h = size.height

        // Top-Left Corner
        drawLine(HighContrastCyan, Offset(30f, 30f), Offset(30f + cornerLen, 30f), strokeWidth)
        drawLine(HighContrastCyan, Offset(30f, 30f), Offset(30f, 30f + cornerLen), strokeWidth)

        // Top-Right Corner
        drawLine(HighContrastCyan, Offset(w - 30f, 30f), Offset(w - 30f - cornerLen, 30f), strokeWidth)
        drawLine(HighContrastCyan, Offset(w - 30f, 30f), Offset(w - 30f, 30f + cornerLen), strokeWidth)

        // Bottom-Left Corner
        drawLine(HighContrastCyan, Offset(30f, h - 30f), Offset(30f + cornerLen, h - 30f), strokeWidth)
        drawLine(HighContrastCyan, Offset(30f, h - 30f), Offset(30f, h - 30f - cornerLen), strokeWidth)

        // Bottom-Right Corner
        drawLine(HighContrastCyan, Offset(w - 30f, h - 30f), Offset(w - 30f - cornerLen, h - 30f), strokeWidth)
        drawLine(HighContrastCyan, Offset(w - 30f, h - 30f), Offset(w - 30f, h - 30f - cornerLen), strokeWidth)

        if (isScanning) {
            val laserY = h * scanProgress
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        HighContrastYellow.copy(alpha = 0.7f),
                        HighContrastYellow,
                        Color.Transparent
                    ),
                    startY = laserY - 15f,
                    endY = laserY + 15f
                ),
                topLeft = Offset(30f, laserY - 10f),
                size = Size(w - 60f, 20f)
            )
        }
    }
}

@Composable
fun PulsingAiOrb(
    isProcessing: Boolean,
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val orbColor = when {
        isProcessing -> HighContrastYellow
        isListening -> HighContrastCyan
        else -> Color(0xFF38BDF8)
    }

    Box(
        modifier = modifier
            .size(100.dp)
            .semantics { contentDescription = if (isListening) "VisionGuard is listening" else if (isProcessing) "VisionGuard is analyzing" else "VisionGuard Assistant idle" },
        contentAlignment = Alignment.Center
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .size((80 * if (isProcessing || isListening) pulseScale else 1f).dp)
                .clip(CircleShape)
                .background(orbColor.copy(alpha = 0.25f))
        )
        // Core orb
        Box(
            modifier = Modifier
                .size((56 * if (isProcessing || isListening) pulseScale else 1f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White, orbColor, orbColor.copy(alpha = 0.8f))
                    )
                )
        )
    }
}
