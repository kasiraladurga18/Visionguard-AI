package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Theme3DMode
import com.example.ui.theme.Theme3DSpec
import kotlin.math.cos
import kotlin.math.sin

/**
 * Tactile 3D Action Card with physical push-down extrusion mechanics,
 * top specular bevel highlight, and tactile click response.
 */
@Composable
fun Tactile3DActionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    themeSpec: Theme3DSpec,
    accentColor: Color = themeSpec.primaryAccent,
    containerGradient: Brush = themeSpec.cardGradient,
    testTag: String,
    badgeText: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 3D physical sink animation on press
    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 6.dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "press_offset_y"
    )
    val rimExtrusionHeight by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 7.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "rim_extrusion_height"
    )
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "press_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .semantics {
                this.role = Role.Button
                this.contentDescription = "$title, $subtitle. Double tap to activate."
            }
    ) {
        // Bottom 3D Extrusion Layer (provides physical depth underneath the card)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .offset(y = 7.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    if (themeSpec.mode == Theme3DMode.HYPER_OLED) {
                        themeSpec.primaryAccent
                    } else {
                        themeSpec.cardRimExtrusion
                    }
                )
        )

        // Main 3D Card Body (sinks down into the extrusion on click)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .offset(y = pressOffsetY)
                .clip(RoundedCornerShape(22.dp))
                .background(containerGradient)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .testTag(testTag)
        ) {
            // Specular Lighting Highlight on Top Edge
            Canvas(modifier = Modifier.matchParentSize()) {
                val strokeW = 2.dp.toPx()
                // Top bevel highlight
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.9f),
                            Color.White.copy(alpha = 0.6f),
                            accentColor.copy(alpha = 0.3f)
                        )
                    ),
                    start = Offset(24.dp.toPx(), 1.dp.toPx()),
                    end = Offset(size.width - 24.dp.toPx(), 1.dp.toPx()),
                    strokeWidth = strokeW
                )
                // Left subtle depth bevel
                drawLine(
                    color = Color.White.copy(alpha = 0.15f),
                    start = Offset(1.dp.toPx(), 24.dp.toPx()),
                    end = Offset(1.dp.toPx(), size.height - 24.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Card Content
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 3D Embossed Icon Container
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.25f),
                                    themeSpec.surfaceVariant
                                )
                            )
                        )
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 19.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = themeSpec.textPrimary
                        )
                        if (badgeText != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(accentColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = badgeText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.5.sp
                        ),
                        color = themeSpec.textSecondary
                    )
                }
            }
        }
    }
}

/**
 * Interactive 3D Spatial LiDAR / Radar Canvas:
 * Features 3D perspective grid, rotating sweep ray, interactive touch probing,
 * and live spatial obstacle telemetry readout.
 */
@Composable
fun Interactive3DSpatialRadar(
    themeSpec: Theme3DSpec,
    onProbePoint: (distanceMeters: Float, bearingDegrees: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var touchX by remember { mutableFloatStateOf(-1f) }
    var touchY by remember { mutableFloatStateOf(-1f) }
    var probedDistance by remember { mutableFloatStateOf(1.8f) }
    var probedBearing by remember { mutableFloatStateOf(0f) }
    var isProbing by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "radar_loop")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_angle"
    )
    val pulseRing by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_ring"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(themeSpec.cardGradient)
            .padding(16.dp)
    ) {
        // Radar HUD Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Sensors,
                    contentDescription = null,
                    tint = themeSpec.primaryAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "3D SPATIAL LiDAR RADAR",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    color = themeSpec.primaryAccent
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isProbing) "TOUCH: ${String.format("%.1f", probedDistance)}m • ${String.format("%+.0f", probedBearing)}°" else "LIVE SCANNING",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (isProbing) themeSpec.secondaryAccent else themeSpec.primaryAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Interactive 3D Perspective Radar Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.Black.copy(alpha = 0.65f))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isProbing = true
                            touchX = offset.x
                            touchY = offset.y
                        },
                        onDragEnd = {
                            isProbing = false
                        },
                        onDragCancel = {
                            isProbing = false
                        },
                        onDrag = { change, _ ->
                            touchX = change.position.x
                            touchY = change.position.y
                            // Compute distance and bearing from bottom center
                            val centerX = size.width / 2f
                            val originY = size.height * 0.95f
                            val dx = touchX - centerX
                            val dy = originY - touchY
                            val distPx = kotlin.math.sqrt(dx * dx + dy * dy)
                            val maxDistPx = size.height * 0.9f
                            probedDistance = (distPx / maxDistPx * 4f).coerceIn(0.4f, 5.0f)
                            probedBearing = Math.toDegrees(kotlin.math.atan2(dx.toDouble(), dy.toDouble())).toFloat()
                            onProbePoint(probedDistance, probedBearing)
                        }
                    )
                }
                .semantics {
                    this.contentDescription = "Interactive 3D LiDAR radar. Touch and drag across the radar to probe spatial obstacle distances."
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val origin = Offset(w / 2f, h * 0.92f)
                val primaryColor = themeSpec.primaryAccent
                val secondaryColor = themeSpec.secondaryAccent

                // 1. 3D Perspective Floor Grid
                val gridLines = 5
                for (i in 0..gridLines) {
                    val angleDeg = 180f + 30f + (120f / gridLines) * i
                    val angleRad = Math.toRadians(angleDeg.toDouble())
                    val endX = origin.x + cos(angleRad).toFloat() * (w * 0.7f)
                    val endY = origin.y + sin(angleRad).toFloat() * (h * 0.9f)
                    drawLine(
                        color = primaryColor.copy(alpha = 0.15f),
                        start = origin,
                        end = Offset(endX, endY),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // 2. Concentric Distance Arcs (1m, 2m, 3m, 4m)
                val arcRadii = listOf(0.28f, 0.52f, 0.76f, 0.98f)
                arcRadii.forEachIndexed { index, ratio ->
                    val radius = h * 0.85f * ratio
                    drawArc(
                        color = primaryColor.copy(alpha = 0.25f),
                        startAngle = 195f,
                        sweepAngle = 150f,
                        useCenter = false,
                        topLeft = Offset(origin.x - radius, origin.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }

                // Dynamic Pulse Ring
                val dynamicRadius = h * 0.85f * pulseRing
                drawArc(
                    color = primaryColor.copy(alpha = (1f - pulseRing) * 0.4f),
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(origin.x - dynamicRadius, origin.y - dynamicRadius),
                    size = Size(dynamicRadius * 2, dynamicRadius * 2),
                    style = Stroke(width = 3.dp.toPx())
                )

                // 3. Sweeping Radar Beam
                val sweepRad = Math.toRadians(180.0 + 30.0 + (sweepAngle % 120.0))
                val sweepLen = h * 0.85f
                val sweepTarget = Offset(
                    origin.x + cos(sweepRad).toFloat() * sweepLen,
                    origin.y + sin(sweepRad).toFloat() * sweepLen
                )
                drawLine(
                    brush = Brush.radialGradient(
                        colors = listOf(primaryColor, Color.Transparent),
                        center = origin,
                        radius = sweepLen
                    ),
                    start = origin,
                    end = sweepTarget,
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // 4. Simulated Obstacles in 3D Space
                val obstacles = listOf(
                    Triple(-0.25f, 0.45f, "Doorway 1.8m"),
                    Triple(0.35f, 0.65f, "Walkway Clear 2.6m"),
                    Triple(-0.1f, 0.8f, "Pathway Ahead")
                )
                obstacles.forEach { (relX, relDist, label) ->
                    val obsX = origin.x + relX * (w * 0.6f)
                    val obsY = origin.y - relDist * (h * 0.8f)
                    // Glow diamond
                    drawCircle(
                        color = secondaryColor.copy(alpha = 0.35f),
                        radius = 12.dp.toPx(),
                        center = Offset(obsX, obsY)
                    )
                    drawCircle(
                        color = secondaryColor,
                        radius = 5.dp.toPx(),
                        center = Offset(obsX, obsY)
                    )
                }

                // 5. User Observer Node (bottom origin)
                drawCircle(
                    color = primaryColor,
                    radius = 8.dp.toPx(),
                    center = origin
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = origin
                )

                // 6. Interactive Touch Probing Crosshair
                if (isProbing && touchX >= 0 && touchY >= 0) {
                    val p = Offset(touchX, touchY)
                    drawLine(
                        color = secondaryColor,
                        start = Offset(p.x - 14.dp.toPx(), p.y),
                        end = Offset(p.x + 14.dp.toPx(), p.y),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = secondaryColor,
                        start = Offset(p.x, p.y - 14.dp.toPx()),
                        end = Offset(p.x, p.y + 14.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawCircle(
                        color = secondaryColor.copy(alpha = 0.4f),
                        radius = 16.dp.toPx(),
                        center = p,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
    }
}

/**
 * Interactive 3D Holographic AI Assistant Orb:
 * Features multi-axis rotating gimbal rings, touch-drag to spin in 3D,
 * and audio-reactive breathing pulses.
 */
@Composable
fun Interactive3DHolographicOrb(
    themeSpec: Theme3DSpec,
    isProcessing: Boolean,
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    var dragAngleX by remember { mutableFloatStateOf(0f) }
    var dragAngleY by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "orb_rotation")
    val autoRotZ by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auto_rot_z"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val activeColor = when {
        isProcessing -> themeSpec.secondaryAccent
        isListening -> themeSpec.primaryAccent
        else -> themeSpec.primaryAccent
    }

    Box(
        modifier = modifier
            .size(130.dp)
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    dragAngleY += dragAmount.x * 0.5f
                    dragAngleX = (dragAngleX - dragAmount.y * 0.5f).coerceIn(-60f, 60f)
                }
            }
            .semantics {
                contentDescription = if (isListening) "VisionGuard is listening" else if (isProcessing) "VisionGuard is analyzing surroundings" else "Interactive 3D Assistant Orb. Drag to rotate in 3D."
            },
        contentAlignment = Alignment.Center
    ) {
        // Multi-Layer 3D Gimbal Rings
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationX = dragAngleX
                    rotationY = dragAngleY
                    cameraDistance = 16f
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.width * 0.45f
            val midRadius = size.width * 0.36f

            // Outer Gimbal Ring (Rotated around Z)
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        activeColor,
                        activeColor.copy(alpha = 0.2f),
                        activeColor,
                        activeColor.copy(alpha = 0.2f)
                    ),
                    center = center
                ),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Inner Elliptical Gimbal Ring
            val ringPath = Path().apply {
                addOval(
                    androidx.compose.ui.geometry.Rect(
                        center.x - midRadius,
                        center.y - midRadius * 0.5f,
                        center.x + midRadius,
                        center.y + midRadius * 0.5f
                    )
                )
            }
            drawPath(
                path = ringPath,
                color = themeSpec.secondaryAccent.copy(alpha = 0.7f),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // 3D Orbital Node
            val rad = Math.toRadians((autoRotZ + dragAngleY).toDouble())
            val nodeX = center.x + cos(rad).toFloat() * outerRadius
            val nodeY = center.y + sin(rad).toFloat() * outerRadius
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = Offset(nodeX, nodeY)
            )
        }

        // Core Refraction Sphere with Breathing Pulsing
        Box(
            modifier = Modifier
                .size((64 * if (isProcessing || isListening) pulseScale else 1f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            activeColor,
                            activeColor.copy(alpha = 0.7f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

/**
 * Interactive 3D Theme Switcher Pill Carousel:
 * Allows user to switch between Cyber Tactile, OLED Safety, Thermal IR, and Matrix Emerald.
 */
@Composable
fun Tactile3DThemeSelector(
    currentMode: Theme3DMode,
    onSelectMode: (Theme3DMode) -> Unit,
    isDarkMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    val currentSpec = com.example.ui.theme.getTheme3DSpec(currentMode, isDarkMode)
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = currentSpec.surfaceBase,
        border = BorderStroke(1.5.dp, currentSpec.borderStroke),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Layers,
                        contentDescription = null,
                        tint = currentSpec.primaryAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "INTERACTIVE 3D THEME",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = currentSpec.primaryAccent
                    )
                }
                Text(
                    text = currentMode.displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = currentSpec.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Theme3DMode.entries.forEach { mode ->
                    val isSelected = mode == currentMode
                    val spec = com.example.ui.theme.getTheme3DSpec(mode, isDarkMode)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) spec.cardGradient else Brush.verticalGradient(
                                    listOf(currentSpec.surfaceVariant, currentSpec.surfaceBase)
                                )
                            )
                            .clickable { onSelectMode(mode) }
                            .semantics {
                                role = Role.RadioButton
                                contentDescription = "${mode.displayName} theme. ${mode.subtitle}. ${if (isSelected) "Selected" else "Not selected"}"
                            }
                            .then(
                                if (isSelected) Modifier.background(
                                    Brush.verticalGradient(
                                        listOf(spec.primaryAccent.copy(alpha = 0.2f), Color.Transparent)
                                    )
                                ) else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(spec.primaryAccent)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mode.displayName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                color = if (isSelected) spec.textPrimary else currentSpec.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
