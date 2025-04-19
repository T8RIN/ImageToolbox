package ru.tech.imageresizershrinker.core.ui.utils.helper

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun PredictiveBackObserver(
    onProgress: (Float) -> Unit,
    onClean: suspend (isCompleted: Boolean) -> Unit,
    enabled: Boolean = true
) {
    val scope = rememberCoroutineScope()

    if (!enabled) return

    PredictiveBackHandler { progress ->
        try {
            progress.collect { event ->
                if (event.progress <= 0.05f) {
                    onClean(false)
                }
                onProgress(event.progress)
            }
            scope.launch {
                onClean(true)
            }
        } catch (_: CancellationException) {
            onClean(false)
        }
    }
}