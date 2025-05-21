package ru.tech.imageresizershrinker.feature.draw.presentation.components.utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.magnifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.gesture.pointerMotionEvents
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.ZoomableDefaults.defaultZoomOnDoubleTap
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.ui.widget.modifier.observePointersCountWithOffset
import ru.tech.imageresizershrinker.core.ui.widget.modifier.smartDelayAfterDownInMillis

fun Modifier.pointerDrawObserver(
    onPointersCountChange: (Int, Offset) -> Unit,
    magnifierEnabled: Boolean,
    currentDrawPosition: Offset,
    globalTouchPosition: Offset,
    zoomState: ZoomState,
    globalTouchPointersCount: Int,
    panEnabled: Boolean
) = this.then(
    Modifier
        .fillMaxSize()
        .observePointersCountWithOffset(onChange = onPointersCountChange)
        .then(
            if (magnifierEnabled) {
                Modifier.magnifier(
                    sourceCenter = {
                        if (currentDrawPosition.isSpecified) {
                            globalTouchPosition
                        } else Offset.Unspecified
                    },
                    magnifierCenter = {
                        globalTouchPosition - Offset(0f, 100.dp.toPx())
                    },
                    size = DpSize(height = 100.dp, width = 100.dp),
                    cornerRadius = 50.dp,
                    elevation = 2.dp
                )
            } else Modifier
        )
        .clipToBounds()
        .zoomable(
            zoomState = zoomState,
            zoomEnabled = (globalTouchPointersCount >= 2 || panEnabled),
            enableOneFingerZoom = panEnabled,
            onDoubleTap = { pos ->
                if (panEnabled) zoomState.defaultZoomOnDoubleTap(pos)
            }
        )
)

fun Modifier.pointerDrawHandler(
    globalTouchPointersCount: Int,
    onReceiveMotionEvent: (MotionEvent) -> Unit,
    onInvalidate: () -> Unit,
    onUpdateCurrentDrawPosition: (Offset) -> Unit,
    onUpdateDrawDownPosition: (Offset) -> Unit,
    enabled: Boolean
) = if (enabled) {
    this.composed {
        var drawStartedWithOnePointer by remember {
            mutableStateOf(false)
        }

        Modifier.pointerMotionEvents(
            onDown = { pointerInputChange ->
                drawStartedWithOnePointer = globalTouchPointersCount <= 1

                if (drawStartedWithOnePointer) {
                    onReceiveMotionEvent(MotionEvent.Down)
                    onUpdateCurrentDrawPosition(pointerInputChange.position)
                    onUpdateDrawDownPosition(pointerInputChange.position)
                    pointerInputChange.consume()
                    onInvalidate()
                }
            },
            onMove = { pointerInputChange ->
                if (drawStartedWithOnePointer) {
                    onReceiveMotionEvent(MotionEvent.Move)
                    onUpdateCurrentDrawPosition(pointerInputChange.position)
                    pointerInputChange.consume()
                    onInvalidate()
                }
            },
            onUp = { pointerInputChange ->
                if (drawStartedWithOnePointer) {
                    onReceiveMotionEvent(MotionEvent.Up)
                    pointerInputChange.consume()
                    onInvalidate()
                }
                drawStartedWithOnePointer = false
            },
            delayAfterDownInMillis = smartDelayAfterDownInMillis(globalTouchPointersCount)
        )
    }
} else {
    this
}