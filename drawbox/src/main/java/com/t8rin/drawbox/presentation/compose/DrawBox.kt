package com.t8rin.drawbox.presentation.compose

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.t8rin.drawbox.domain.DrawController
import com.t8rin.drawbox.presentation.view.DrawView

@Composable
fun DrawBox(
    modifier: Modifier = Modifier,
    drawingModifier: Modifier = Modifier,
    orientation: Int = Configuration.ORIENTATION_PORTRAIT,
    drawController: DrawController?,
    onGetDrawController: (DrawController) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    LockScreenOrientation(orientation)
    Box(modifier = modifier) {
        content()
        AndroidView(
            factory = {
                val view = DrawView(it).apply {
                    clipToOutline = true
                }
                onGetDrawController(view)
                view
            },
            modifier = drawingModifier,
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