package pl.wieczorek.planerpodrozy.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.ui.format.DateFormatter
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme
import pl.wieczorek.planerpodrozy.ui.theme.caption

/** Nagłówek karty multimediów: ikona + tytuł („Film z podróży”, „Notatka głosowa”). */
@Composable
private fun MediaCardHeader(icon: ImageVector, title: String, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun WhiteMediaCard(modifier: Modifier, content: @Composable ColumnScope.() -> Unit) {
    OutlinedCard(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = planerCardBorder(),
        modifier = modifier.fillMaxWidth(),
        content = content,
    )
}

/**
 * Odtwarzacz filmu: nagłówek, podgląd z przyciskiem play i oś czasu.
 * [preview] to miniatura (pierwsza klatka) albo widok odtwarzacza, gdy film gra.
 * Kliknięcie w podgląd wywołuje [onPlayPause].
 */
@Composable
fun VideoPlayerCard(
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier,
    preview: @Composable () -> Unit,
) {
    WhiteMediaCard(modifier) {
        MediaCardHeader(
            icon = Icons.Outlined.Videocam,
            title = stringResource(R.string.media_video_title),
            modifier = Modifier
                .height(Dimens.MediaHeaderHeight)
                .padding(horizontal = Dimens.SpaceL),
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.VideoPreviewHeight)
                .clickable(onClick = onPlayPause),
        ) {
            preview()
            if (!isPlaying) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.12f)),
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.PlayButtonSize),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = stringResource(R.string.cd_play),
                            modifier = Modifier.size(Dimens.IconL),
                        )
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(Dimens.MediaProgressRowHeight)
                .padding(horizontal = Dimens.SpaceL),
        ) {
            TimeText(DateFormatter.duration(positionMs), MaterialTheme.colorScheme.onSurfaceVariant)
            LinearProgressIndicator(
                progress = { progressOf(positionMs, durationMs) },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant,
                gapSize = 0.dp,
                drawStopIndicator = {},
                modifier = Modifier
                    .weight(1f)
                    .height(Dimens.ProgressHeight),
            )
            TimeText(DateFormatter.duration(durationMs), MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Odtwarzacz notatki głosowej: play/pauza, fala dźwiękowa i czas „0:42 / 1:30”. */
@Composable
fun VoiceNotePlayerCard(
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        border = planerCardBorder(),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
            modifier = Modifier.padding(Dimens.SpaceL),
        ) {
            MediaCardHeader(icon = Icons.Outlined.Mic, title = stringResource(R.string.media_voice_title))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceL),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilledIconButton(onClick = onPlayPause, modifier = Modifier.size(Dimens.VoiceButtonSize)) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = stringResource(if (isPlaying) R.string.cd_pause else R.string.cd_play),
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXs),
                    modifier = Modifier.weight(1f),
                ) {
                    AudioWaveform(
                        progress = progressOf(positionMs, durationMs),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.WaveformHeight),
                    )
                    TimeText(
                        text = stringResource(
                            R.string.media_time_progress,
                            DateFormatter.duration(positionMs),
                            DateFormatter.duration(durationMs),
                        ),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

/** Pusta karta, gdy filmu lub notatki jeszcze nie ma. */
@Composable
fun EmptyMediaCard(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    WhiteMediaCard(modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceS),
            modifier = Modifier.padding(Dimens.SpaceL),
        ) {
            MediaCardHeader(icon = icon, title = title)
            Text(
                text = message,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// Wysokości słupków odczytane z makiety (w jednostkach względnych, max = 36).
private val WaveformPattern = listOf(
    10, 18, 12, 26, 32, 18, 12, 28, 36, 22, 14, 30, 20, 34,
    26, 12, 18, 32, 24, 36, 18, 26, 12, 30, 22, 16, 10, 18,
).map { it / 36f }

/**
 * Dekoracyjna fala dźwiękowa ze słupków. Część „odtworzona” (wg [progress] 0..1)
 * jest w kolorze akcentu, reszta półprzezroczysta. Liczba słupków dopasowuje się do szerokości.
 */
@Composable
fun AudioWaveform(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Canvas(modifier) {
        val barWidth = Dimens.WaveBarWidth.toPx()
        val gap = Dimens.WaveBarGap.toPx()
        val barCount = ((size.width + gap) / (barWidth + gap)).toInt().coerceAtLeast(1)
        val totalWidth = barCount * barWidth + (barCount - 1) * gap
        val startX = (size.width - totalWidth) / 2
        val playedBars = (progress.coerceIn(0f, 1f) * barCount).toInt()
        repeat(barCount) { index ->
            val barHeight = size.height * WaveformPattern[index % WaveformPattern.size]
            drawRoundRect(
                color = if (index < playedBars) color else color.copy(alpha = 0.35f),
                topLeft = Offset(startX + index * (barWidth + gap), (size.height - barHeight) / 2),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2),
            )
        }
    }
}

@Composable
private fun TimeText(text: String, color: Color) {
    Text(text = text, style = MaterialTheme.typography.caption, color = color)
}

private fun progressOf(positionMs: Long, durationMs: Long): Float =
    if (durationMs <= 0) 0f else (positionMs.toFloat() / durationMs).coerceIn(0f, 1f)

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun MediaCardsPreview() {
    PlanerPodrozyTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
            modifier = Modifier.padding(Dimens.SpaceL),
        ) {
            VideoPlayerCard(
                isPlaying = false,
                positionMs = 4_000,
                durationMs = 138_000,
                onPlayPause = {},
            ) {
                EmptyTripPlaceholder(Modifier.fillMaxSize())
            }
            VoiceNotePlayerCard(isPlaying = true, positionMs = 42_000, durationMs = 90_000, onPlayPause = {})
            EmptyMediaCard(
                icon = Icons.Outlined.Mic,
                title = "Notatka głosowa",
                message = "Brak notatki. Nagraj ją przyciskiem „+”.",
            )
        }
    }
}
