package ru.tech.imageresizershrinker.coreui.widget.modifier

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.delay

fun Modifier.scaleOnTap(
    initial: Float = 1f,
    min: Float = 0.8f,
    max: Float = 1.3f,
    onHold: () -> Unit = {},
    onRelease: (time: Long) -> Unit
) = composed {
    var scaleState by remember(initial) { mutableFloatStateOf(initial) }
    val scale by animateFloatAsState(scaleState)
    val haptics = LocalHapticFeedback.current

    scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    val time = System.currentTimeMillis()
                    scaleState = max
                    haptics.performHapticFeedback(
                        HapticFeedbackType.LongPress
                    )
                    onHold()
                    delay(200)
                    tryAwaitRelease()
                    onRelease(System.currentTimeMillis() - time)
                    haptics.performHapticFeedback(
                        HapticFeedbackType.LongPress
                    )
                    scaleState = min
                    delay(200)
                    scaleState = initial
                }
            )
        }
}