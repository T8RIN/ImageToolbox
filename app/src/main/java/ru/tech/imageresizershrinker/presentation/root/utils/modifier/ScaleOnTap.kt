package ru.tech.imageresizershrinker.presentation.root.utils.modifier

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay

fun Modifier.scaleOnTap(
    initial: Float = 1f,
    min: Float = 0.8f,
    max: Float = 1.3f,
    onHold: () -> Unit = {},
    onRelease: () -> Unit
) = composed {
    var scaleState by remember(initial) { mutableStateOf(initial) }
    val scale by animateFloatAsState(scaleState)

    scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    scaleState = max
                    onHold()
                    delay(200)
                    tryAwaitRelease()
                    onRelease()
                    scaleState = min
                    delay(200)
                    scaleState = initial
                }
            )
        }
}