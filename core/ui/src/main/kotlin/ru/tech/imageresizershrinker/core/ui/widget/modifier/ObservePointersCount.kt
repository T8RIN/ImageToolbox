package ru.tech.imageresizershrinker.core.ui.widget.modifier

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastAny
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlin.coroutines.cancellation.CancellationException


fun Modifier.observePointersCount(
    enabled: Boolean = true,
    onChange: (Int) -> Unit
) = this then if (enabled) Modifier.pointerInput(Unit) {
    onEachGesture {
        val context = currentCoroutineContext()
        awaitPointerEventScope {
            do {
                val event = awaitPointerEvent()
                onChange(event.changes.size)
            } while (event.changes.any { it.pressed } && context.isActive)
            onChange(0)
        }
    }
} else Modifier

suspend fun PointerInputScope.onEachGesture(block: suspend PointerInputScope.() -> Unit) {
    val currentContext = currentCoroutineContext()
    while (currentContext.isActive) {
        try {
            block()

            // Wait for all pointers to be up. Gestures start when a finger goes down.
            awaitAllPointersUp()
        } catch (e: CancellationException) {
            if (currentContext.isActive) {
                // The current gesture was canceled. Wait for all fingers to be "up" before looping
                // again.
                awaitAllPointersUp()
            } else {
                // forEachGesture was cancelled externally. Rethrow the cancellation exception to
                // propagate it upwards.
                throw e
            }
        }
    }
}

private suspend fun PointerInputScope.awaitAllPointersUp() {
    awaitPointerEventScope { awaitAllPointersUp() }
}

private suspend fun AwaitPointerEventScope.awaitAllPointersUp() {
    if (!allPointersUp()) {
        do {
            val events = awaitPointerEvent(PointerEventPass.Final)
        } while (events.changes.fastAny { it.pressed })
    }
}

private fun AwaitPointerEventScope.allPointersUp(): Boolean =
    !currentEvent.changes.fastAny { it.pressed }


@Composable
fun smartDelayAfterDownInMillis(pointersCount: Int): Long {
    var delayAfterDownInMillis by remember {
        mutableLongStateOf(20L)
    }
    var previousCount by remember {
        mutableIntStateOf(pointersCount)
    }
    LaunchedEffect(pointersCount) {
        delayAfterDownInMillis = if (pointersCount <= 1 && previousCount >= 2) 5L else 20L
        previousCount = pointersCount
    }

    return delayAfterDownInMillis
}