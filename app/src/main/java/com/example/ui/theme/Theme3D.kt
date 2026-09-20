package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class Theme3DMode(
    val id: String,
    val displayName: String,
    val subtitle: String,
    val iconName: String
) {
    CYBER_TACTILE(
        id = "cyber_tactile",
        displayName = "Cyber 3D",
        subtitle = "Electric Cyan & Obsidian",
        iconName = "cyber"
    ),
    HYPER_OLED(
        id = "hyper_oled",
        displayName = "OLED Safety",
        subtitle = "High-Vis Yellow & Black",
        iconName = "oled"
    ),
    THERMAL_IR(
        id = "thermal_ir",
        displayName = "Thermal IR",
        subtitle = "Laser Crimson & Violet",
        iconName = "thermal"
    ),
    MATRIX_EMERALD(
        id = "matrix_emerald",
        displayName = "Matrix 3D",
        subtitle = "Phosphor Mint & Forest",
        iconName = "emerald"
    );

    companion object {
        fun fromId(id: String?): Theme3DMode {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: CYBER_TACTILE
        }
    }
}

data class Theme3DSpec(
    val mode: Theme3DMode,
    val primaryAccent: Color,
    val primaryGlow: Color,
    val secondaryAccent: Color,
    val backgroundBase: Color,
    val surfaceBase: Color,
    val surfaceVariant: Color,
    val cardTopColor: Color,
    val cardBottomColor: Color,
    val cardRimExtrusion: Color,
    val specularHighlight: Color,
    val borderStroke: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val cardGradient: Brush,
    val backgroundGradient: Brush
)

val CyberTactileSpec = Theme3DSpec(
    mode = Theme3DMode.CYBER_TACTILE,
    primaryAccent = Color(0xFF00F0FF),
    primaryGlow = Color(0xFF00F0FF).copy(alpha = 0.35f),
    secondaryAccent = Color(0xFFFFB703),
    backgroundBase = Color(0xFF060B14),
    surfaceBase = Color(0xFF0C1628),
    surfaceVariant = Color(0xFF142440),
    cardTopColor = Color(0xFF13233F),
    cardBottomColor = Color(0xFF0A1322),
    cardRimExtrusion = Color(0xFF04080F),
    specularHighlight = Color(0xFF38BDF8),
    borderStroke = Color(0xFF1E3A63),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFF94A3B8),
    cardGradient = Brush.verticalGradient(listOf(Color(0xFF16294A), Color(0xFF0C1729))),
    backgroundGradient = Brush.verticalGradient(listOf(Color(0xFF091222), Color(0xFF04080F)))
)

val HyperOledSpec = Theme3DSpec(
    mode = Theme3DMode.HYPER_OLED,
    primaryAccent = Color(0xFFFFEB3B),
    primaryGlow = Color(0xFFFFEB3B).copy(alpha = 0.4f),
    secondaryAccent = Color(0xFFFFFFFF),
    backgroundBase = Color(0xFF000000),
    surfaceBase = Color(0xFF111111),
    surfaceVariant = Color(0xFF1A1A1A),
    cardTopColor = Color(0xFF1E1E1E),
    cardBottomColor = Color(0xFF0D0D0D),
    cardRimExtrusion = Color(0xFFFFEB3B),
    specularHighlight = Color(0xFFFFFFFF),
    borderStroke = Color(0xFFFFEB3B),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFE2E8F0),
    cardGradient = Brush.verticalGradient(listOf(Color(0xFF222222), Color(0xFF0D0D0D))),
    backgroundGradient = Brush.verticalGradient(listOf(Color(0xFF000000), Color(0xFF050505)))
)

val ThermalIrSpec = Theme3DSpec(
    mode = Theme3DMode.THERMAL_IR,
    primaryAccent = Color(0xFFFF2A6D),
    primaryGlow = Color(0xFFFF2A6D).copy(alpha = 0.35f),
    secondaryAccent = Color(0xFFFF7A00),
    backgroundBase = Color(0xFF0B0418),
    surfaceBase = Color(0xFF16092E),
    surfaceVariant = Color(0xFF240F47),
    cardTopColor = Color(0xFF250D4C),
    cardBottomColor = Color(0xFF120526),
    cardRimExtrusion = Color(0xFF06020E),
    specularHighlight = Color(0xFFFF6597),
    borderStroke = Color(0xFF4A1985),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFE9D5FF),
    cardGradient = Brush.verticalGradient(listOf(Color(0xFF2E105C), Color(0xFF15062C))),
    backgroundGradient = Brush.verticalGradient(listOf(Color(0xFF110526), Color(0xFF06020E)))
)

val MatrixEmeraldSpec = Theme3DSpec(
    mode = Theme3DMode.MATRIX_EMERALD,
    primaryAccent = Color(0xFF00FF9D),
    primaryGlow = Color(0xFF00FF9D).copy(alpha = 0.35f),
    secondaryAccent = Color(0xFF10B981),
    backgroundBase = Color(0xFF02120B),
    surfaceBase = Color(0xFF062215),
    surfaceVariant = Color(0xFF0D3924),
    cardTopColor = Color(0xFF0B3320),
    cardBottomColor = Color(0xFF03160D),
    cardRimExtrusion = Color(0xFF010905),
    specularHighlight = Color(0xFF34D399),
    borderStroke = Color(0xFF145334),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFA7F3D0),
    cardGradient = Brush.verticalGradient(listOf(Color(0xFF0F4129), Color(0xFF04190F))),
    backgroundGradient = Brush.verticalGradient(listOf(Color(0xFF041C11), Color(0xFF010A06)))
)

val CyberTactileLightSpec = Theme3DSpec(
    mode = Theme3DMode.CYBER_TACTILE,
    primaryAccent = Color(0xFF0284C7),
    primaryGlow = Color(0xFF0284C7).copy(alpha = 0.2f),
    secondaryAccent = Color(0xFFD97706),
    backgroundBase = Color(0xFFF1F5F9),
    surfaceBase = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE2E8F0),
    cardTopColor = Color(0xFFFFFFFF),
    cardBottomColor = Color(0xFFF8FAFC),
    cardRimExtrusion = Color(0xFFCBD5E1),
    specularHighlight = Color(0xFF0284C7),
    borderStroke = Color(0xFFCBD5E1),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF475569),
    cardGradient = Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC))),
    backgroundGradient = Brush.verticalGradient(listOf(Color(0xFFF8FAFC), Color(0xFFEEF2F6)))
)

val HyperOledLightSpec = Theme3DSpec(
    mode = Theme3DMode.HYPER_OLED,
    primaryAccent = Color(0xFFCA8A04),
    primaryGlow = Color(0xFFCA8A04).copy(alpha = 0.25f),
    secondaryAccent = Color(0xFF000000),
    backgroundBase = Color(0xFFFFFFFF),
    surfaceBase = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF4F4F5),
    cardTopColor = Color(0xFFFFFFFF),
    cardBottomColor = Color(0xFFF4F4F5),
    cardRimExtrusion = Color(0xFF71717A),
    specularHighlight = Color(0xFFCA8A04),
    borderStroke = Color(0xFF000000),
    textPrimary = Color(0xFF000000),
    textSecondary = Color(0xFF18181B),
    cardGradient = Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF4F4F5))),
    backgroundGradient = Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFE4E4E7)))
)

val ThermalIrLightSpec = Theme3DSpec(
    mode = Theme3DMode.THERMAL_IR,
    primaryAccent = Color(0xFFE11D48),
    primaryGlow = Color(0xFFE11D48).copy(alpha = 0.2f),
    secondaryAccent = Color(0xFFEA580C),
    backgroundBase = Color(0xFFFFF1F2),
    surfaceBase = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFFFE4E6),
    cardTopColor = Color(0xFFFFFFFF),
    cardBottomColor = Color(0xFFFFF1F2),
    cardRimExtrusion = Color(0xFFFECDD3),
    specularHighlight = Color(0xFFE11D48),
    borderStroke = Color(0xFFFDA4AF),
    textPrimary = Color(0xFF881337),
    textSecondary = Color(0xFF4C0519),
    cardGradient = Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFFFF1F2))),
    backgroundGradient = Brush.verticalGradient(listOf(Color(0xFFFFF1F2), Color(0xFFFFE4E6)))
)

val MatrixEmeraldLightSpec = Theme3DSpec(
    mode = Theme3DMode.MATRIX_EMERALD,
    primaryAccent = Color(0xFF059669),
    primaryGlow = Color(0xFF059669).copy(alpha = 0.2f),
    secondaryAccent = Color(0xFF0284C7),
    backgroundBase = Color(0xFFF0FDF4),
    surfaceBase = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFDCFCE7),
    cardTopColor = Color(0xFFFFFFFF),
    cardBottomColor = Color(0xFFF0FDF4),
    cardRimExtrusion = Color(0xFFBBF7D0),
    specularHighlight = Color(0xFF059669),
    borderStroke = Color(0xFF86EFAC),
    textPrimary = Color(0xFF064E3B),
    textSecondary = Color(0xFF022C22),
    cardGradient = Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF0FDF4))),
    backgroundGradient = Brush.verticalGradient(listOf(Color(0xFFF0FDF4), Color(0xFFDCFCE7)))
)

fun getTheme3DSpec(mode: Theme3DMode, isDarkMode: Boolean = true): Theme3DSpec {
    return if (isDarkMode) {
        when (mode) {
            Theme3DMode.CYBER_TACTILE -> CyberTactileSpec
            Theme3DMode.HYPER_OLED -> HyperOledSpec
            Theme3DMode.THERMAL_IR -> ThermalIrSpec
            Theme3DMode.MATRIX_EMERALD -> MatrixEmeraldSpec
        }
    } else {
        when (mode) {
            Theme3DMode.CYBER_TACTILE -> CyberTactileLightSpec
            Theme3DMode.HYPER_OLED -> HyperOledLightSpec
            Theme3DMode.THERMAL_IR -> ThermalIrLightSpec
            Theme3DMode.MATRIX_EMERALD -> MatrixEmeraldLightSpec
        }
    }
}
