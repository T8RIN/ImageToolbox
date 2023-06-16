package com.t8rin.drawbox.presentation.compose

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import com.t8rin.drawbox.domain.DrawController
import com.t8rin.drawbox.presentation.view.DrawView

@Composable
fun DrawBox(
    modifier: Modifier = Modifier,
    drawingModifier: Modifier = Modifier,
    orientation: Int = Configuration.ORIENTATION_PORTRAIT,
    drawController: DrawController?,
    contentAlignment: Alignment = Alignment.Center,
    onGetDrawController: (DrawController) -> Unit,
    content: @Composable () -> Unit
) {
    var size by remember { mutableStateOf(IntSize(0, 0)) }
    LockScreenOrientation(orientation)
    Box(modifier = modifier, contentAlignment = contentAlignment) {
        Box(
            modifier = Modifier.onSizeChanged { size = it }
        ) {
            content()
        }
        AndroidView(
            factory = {
                val view = DrawView(it).apply {
                    clipToOutline = true
                }
                onGetDrawController(view)
                view
            },
            modifier = drawingModifier.size(size),
            update = {
                drawController?.apply {
                    it.isEraserOn = isEraserOn
                    it.curX = curX
                    it.curY = curY
                    it.isSaving = isSaving
                    it.isStrokeWidthBarEnabled = isStrokeWidthBarEnabled
                    it.lastPaths = lastPaths
                    it.paint = paint
                    it.paths = paths
                    it.paintOptions = paintOptions
                    it.undonePaths = undonePaths
                    it.drawPath = drawPath
                    it.startX = startX
                    it.startY = startY
                }
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

@Composable
private fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}