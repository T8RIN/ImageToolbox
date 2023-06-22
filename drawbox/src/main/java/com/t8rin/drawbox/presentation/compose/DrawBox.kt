package com.t8rin.drawbox.presentation.compose

import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import com.smarttoolfactory.image.util.update
import com.smarttoolfactory.image.zoom.animatedZoom
import com.smarttoolfactory.image.zoom.rememberAnimatedZoomState
import com.t8rin.drawbox.domain.DrawController
import com.t8rin.drawbox.presentation.view.DrawView

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawBox(
    modifier: Modifier = Modifier,
    drawingModifier: Modifier = Modifier,
    drawController: DrawController?,
    zoomEnabled: Boolean = false,
    contentAlignment: Alignment = Alignment.Center,
    onGetDrawController: (DrawController) -> Unit,
    content: @Composable () -> Unit
) {
    val zoomState = rememberAnimatedZoomState(maxZoom = 30f)
    var size by remember { mutableStateOf(IntSize(0, 0)) }
    Box(
        modifier = if (zoomEnabled) {
            Modifier.animatedZoom(animatedZoomState = zoomState)
        } else {
            Modifier.graphicsLayer {
                update(zoomState)
            }
        }.then(modifier),
        contentAlignment = contentAlignment
    ) {
        Box(
            modifier = Modifier.onSizeChanged { size = it }
        ) {
            content()
        }
        AndroidView(
            factory = {
                val view = DrawView(it, zoomState, zoomEnabled).apply {
                    clipToOutline = true
                }
                onGetDrawController(view)
                view
            },
            modifier = drawingModifier
                .size(size)
                .pointerInteropFilter {
                    if (zoomEnabled) false
                    else {
                        (drawController as DrawView).apply {
                            val x = (it.x / zoomState.zoom).coerceIn(0f, this.width.toFloat())
                            val y = it.y / zoomState.zoom.coerceIn(0f, this.height.toFloat())
                            when (it.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    startX = x
                                    startY = y
                                    actionDown(x, y)
                                    undonePaths = linkedMapOf()
                                    invalidate()
                                }

                                MotionEvent.ACTION_MOVE -> {
                                    actionMove(x, y)
                                    invalidate()
                                }

                                MotionEvent.ACTION_UP -> {
                                    actionUp()
                                    invalidate()
                                }
                            }
                        }
                        true
                    }
                },
            update = {
                drawController?.apply {
                    it.isEraserOn = isEraserOn
                    it.curX = curX
                    it.curY = curY
                    it.isStrokeWidthBarEnabled = isStrokeWidthBarEnabled
                    it.lastPaths = lastPaths
                    it.paint = paint
                    it.setDrawBackground(backgroundColor)
                    it.paths = paths
                    it.paintOptions = paintOptions
                    it.undonePaths = undonePaths
                    it.drawPath = drawPath
                    it.startX = startX
                    it.startY = startY
                }
                it.zoomState = zoomState
                it.zoomEnabled = zoomEnabled
            }
        )
    }

}

private fun Modifier.size(size: IntSize): Modifier = composed {
    val density = LocalDensity.current
    size(
        width = with(density) { size.width.toDp() },
        height = with(density) { size.height.toDp() }
    )
}