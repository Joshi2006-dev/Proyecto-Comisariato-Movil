package com.example.comisapp.ui.screens.components

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.comisapp.R

class FavoriteSoundManager(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val soundId: Int = soundPool.load(context, R.raw.favorite_pop, 1)

    fun play() {
        soundPool.play(
            soundId,
            0.8f,
            0.8f,
           1,
            0,
            1.0f
        )
    }

    fun release() {
        soundPool.release()
    }
}


@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val soundManager = remember { FavoriteSoundManager(context) }
    DisposableEffect(Unit) {
        onDispose { soundManager.release() }
    }

    var triggered by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (triggered) 1.35f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        finishedListener = {
            if (triggered) triggered = false
        },
        label = "favorite_scale"
    )

    // ── Color animado ──
    val tint by animateColorAsState(
        targetValue = if (isFavorite) Color(0xFFE53935) else Color(0xFFBDBDBD),
        animationSpec = tween(durationMillis = 200),
        label = "favorite_color"
    )

    IconButton(
        onClick = {
            val newState = !isFavorite
            onToggle(newState)
            if (newState) {
                triggered = true
                soundManager.play()
            }
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
            tint = tint,
            modifier = Modifier
                .size(22.dp)
                .scale(scale)
        )
    }
}