package pl.wieczorek.planerpodrozy.ui.media

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import pl.wieczorek.planerpodrozy.ui.components.VideoPlayerCard
import java.io.File

/** Co ile odświeżamy czas na osi odtwarzaczy. */
internal const val PlaybackTickMillis = 200L

/**
 * Odtwarzacz filmu podróży (Media3 ExoPlayer) w karcie z makiety.
 * Przed odtworzeniem widać miniaturę (pierwszą klatkę), potem obraz z odtwarzacza.
 * Odtwarzacz żyje tylko, dopóki karta jest na ekranie, i pauzuje, gdy aplikacja schodzi w tło.
 */
@OptIn(UnstableApi::class)
@Composable
fun TripVideoPlayer(videoPath: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val player = remember(videoPath) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.fromFile(File(videoPath))))
            prepare()
        }
    }
    var isPlaying by remember(player) { mutableStateOf(false) }
    var hasStarted by remember(player) { mutableStateOf(false) }
    var positionMs by remember(player) { mutableLongStateOf(0L) }
    var durationMs by remember(player) { mutableLongStateOf(0L) }

    // Nasłuch zdarzeń odtwarzacza; po zejściu karty z ekranu odtwarzacz jest zwalniany.
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) durationMs = player.duration.coerceAtLeast(0)
                if (state == Player.STATE_ENDED) {
                    // Po końcu filmu wracamy do początku i miniatury.
                    player.pause()
                    player.seekTo(0)
                    hasStarted = false
                }
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { player.pause() }

    // W trakcie odtwarzania co chwilę odczytujemy pozycję, żeby przesuwać oś czasu.
    LaunchedEffect(player, isPlaying) {
        while (isPlaying) {
            positionMs = player.currentPosition
            delay(PlaybackTickMillis)
        }
        positionMs = player.currentPosition
    }

    VideoPlayerCard(
        isPlaying = isPlaying,
        positionMs = positionMs,
        durationMs = durationMs,
        onPlayPause = {
            if (player.isPlaying) {
                player.pause()
            } else {
                hasStarted = true
                player.play()
            }
        },
        modifier = modifier,
    ) {
        if (hasStarted) {
            // PlayerView to klasyczny widok Androida; AndroidView osadza go w Compose.
            AndroidView(
                factory = { viewContext ->
                    PlayerView(viewContext).apply {
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        this.player = player
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            AsyncImage(
                model = File(videoPath),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
