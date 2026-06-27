package com.example.video

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

/**
 * Lecteur vidéo ExoPlayer pour le feed Cusbuy.
 * Remplace la simulation par une vraie lecture vidéo.
 *
 * @param videoUrl URL de la vidéo (Firebase Storage, Cloudflare Stream...)
 * @param isPlaying Si la vidéo est en lecture
 * @param isMuted Si le son est coupé (par défaut dans le feed)
 * @param modifier Modifier Compose
 */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun CusbuyVideoPlayer(
    videoUrl: String,
    isPlaying: Boolean = true,
    isMuted: Boolean = true,
    modifier: Modifier = Modifier,
    onProgressChanged: ((Float) -> Unit)? = null
) {
    val context = LocalContext.current
    var progress by remember { mutableStateOf(0f) }

    // Créer et mémoriser le player
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            repeatMode = Player.REPEAT_MODE_ONE   // Boucle infinie comme TikTok
            volume = if (isMuted) 0f else 1f
            prepare()
        }
    }

    // Sync play/pause
    LaunchedEffect(isPlaying) {
        if (isPlaying) exoPlayer.play() else exoPlayer.pause()
    }

    // Sync mute
    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }

    // Suivi de la progression
    LaunchedEffect(exoPlayer) {
        while (true) {
            val duration = exoPlayer.duration
            val position = exoPlayer.currentPosition
            if (duration > 0) {
                progress = position.toFloat() / duration.toFloat()
                onProgressChanged?.invoke(progress)
            }
            delay(200)
        }
    }

    // Libérer le player à la destruction
    DisposableEffect(exoPlayer) {
        onDispose { exoPlayer.release() }
    }

    Box(modifier = modifier) {
        // Vue ExoPlayer intégrée dans Compose
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false // On gère nous-mêmes les contrôles
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Barre de progression en bas (style TikTok)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.BottomCenter),
            color = Color(0xFFFF4500),
            trackColor = Color.White.copy(alpha = 0.3f)
        )
    }
}

/**
 * Player simple pour les Replays Live et Stories.
 */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun SimpleVideoPlayer(
    videoUrl: String,
    showControls: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }

    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose { exoPlayer.release() }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = showControls
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
