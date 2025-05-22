package ru.tech.imageresizershrinker.feature.draw.presentation.components.utils

import android.graphics.Bitmap
import android.graphics.PorterDuff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Pt
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.createScaledBitmap
import ru.tech.imageresizershrinker.feature.draw.domain.DrawMode
import ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode
import ru.tech.imageresizershrinker.feature.draw.presentation.components.UiPathPaint
import android.graphics.Path as NativePath

@Composable
internal fun DrawPathEffectPreview(
    drawPathCanvas: Canvas,
    drawMode: DrawMode.PathEffect,
    canvasSize: IntegerSize,
    imageWidth: Int,
    imageHeight: Int,
    outputImage: ImageBitmap,
    onRequestFiltering: suspend (Bitmap, List<Filter<*>>) -> Bitmap?,
    paths: List<UiPathPaint>,
    drawPath: Path,
    backgroundColor: Color,
    strokeWidth: Pt,
    drawPathMode: DrawPathMode
) {
    var shaderBitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    LaunchedEffect(outputImage, paths, backgroundColor, drawMode) {
        shaderBitmap = onRequestFiltering(
            outputImage.asAndroidBitmap(),
            transformationsForMode(
                drawMode = drawMode,
                canvasSize = canvasSize
            )
        )?.createScaledBitmap(
            width = imageWidth,
            height = imageHeight
        )?.asImageBitmap()
    }

    shaderBitmap?.let {
        with(drawPathCanvas) {
            with(nativeCanvas) {
                drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)

                val paint by rememberPathEffectPaint(
                    strokeWidth = strokeWidth,
                    drawPathMode = drawPathMode,
                    canvasSize = canvasSize
                )
                val newPath = drawPath.copyAsAndroidPath().apply {
                    fillType = NativePath.FillType.INVERSE_WINDING
                }
                val imagePaint = remember { Paint() }

                drawImage(
                    image = it,
                    topLeftOffset = Offset.Zero,
                    paint = imagePaint
                )
                drawPath(newPath, paint)
            }
        }
    }
}
