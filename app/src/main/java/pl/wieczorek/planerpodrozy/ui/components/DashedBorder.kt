package pl.wieczorek.planerpodrozy.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Przerywana ramka z zaokrąglonymi rogami. M3 nie ma takiej ramki, a makieta używa jej
 * w pustym kadrze „Dodaj zdjęcie” i w chipie „Dodaj lokalizację”.
 */
fun Modifier.dashedBorder(
    color: Color,
    cornerRadius: Dp,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 6.dp,
    gapLength: Dp = 4.dp,
): Modifier = drawWithContent {
    // Najpierw zawartość (np. tło Surface), potem ramka na wierzchu, żeby tło jej nie zakryło.
    drawContent()
    val stroke = strokeWidth.toPx()
    // Linia rysowana jest po środku obrysu, więc przesuwamy ją o pół grubości do środka.
    val inset = stroke / 2
    drawRoundRect(
        color = color,
        topLeft = Offset(inset, inset),
        size = Size(size.width - stroke, size.height - stroke),
        cornerRadius = CornerRadius(cornerRadius.toPx() - inset),
        style = Stroke(
            width = stroke,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength.toPx(), gapLength.toPx())),
        ),
    )
}
