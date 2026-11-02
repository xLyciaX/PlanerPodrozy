package pl.wieczorek.planerpodrozy.ui.media

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import kotlinx.coroutines.delay
import pl.wieczorek.planerpodrozy.ui.components.VoiceNotePlayerCard

/**
 * Odtwarzacz notatki głosowej (systemowy MediaPlayer) w karcie z falą dźwiękową.
 * [onUnavailable] rysuje zastępczą treść, gdy pliku nie da się odtworzyć.
 */
@Composable
fun VoiceNotePlayer(
    audioPath: String,
    modifier: Modifier = Modifier,
    onUnavailable: @Composable () -> Unit = {},
) {
    val player = remember(audioPath) {
        runCatching { MediaPlayer().apply { setDataSource(audioPath); prepare() } }.getOrNull()
    }
    if (player == null) {
        onUnavailable()
        return
    }
    var isPlaying by remember(player) { mutableStateOf(false) }
    var positionMs by remember(player) { mutableLongStateOf(0L) }
    val durationMs = remember(player) { player.duration.toLong().coerceAtLeast(0) }

    DisposableEffect(player) {
        player.setOnCompletionListener {
            isPlaying = false
            positionMs = 0
            it.seekTo(0)
        }
        onDispose { player.release() }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        if (player.isPlaying) player.pause()
        isPlaying = false
    }
    LaunchedEffect(player, isPlaying) {
        while (isPlaying) {
            positionMs = player.currentPosition.toLong()
            delay(PlaybackTickMillis)
        }
    }

    VoiceNotePlayerCard(
        isPlaying = isPlaying,
        positionMs = positionMs,
        durationMs = durationMs,
        onPlayPause = {
            if (isPlaying) player.pause() else player.start()
            isPlaying = !isPlaying
        },
        modifier = modifier,
    )
}
