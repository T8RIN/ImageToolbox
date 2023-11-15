package ru.tech.imageresizershrinker.presentation.root.widget.other

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.domain.image.draw.PathPaint
import ru.tech.imageresizershrinker.domain.model.IntegerSize
import ru.tech.imageresizershrinker.presentation.root.theme.blend
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.helper.scaleToFitCanvas
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.transparencyChecker

@Composable
fun PathPaintPreview(
    modifier: Modifier = Modifier,
    pathPaints: List<PathPaint<Path, Color>>
) {
    val color = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.6f)
    val visuals = Modifier
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant(),
            shape = RoundedCornerShape(4.dp)
        )
        .clip(RoundedCornerShape(4.dp))
        .transparencyChecker(
            checkerHeight = 2.dp,
            checkerWidth = 2.dp
        )
    val first = pathPaints.firstOrNull()
    if (first != null) {
        Box(
            modifier = modifier
                .aspectRatio(first.canvasSize.aspectRatio)
                .then(visuals)
                .drawBehind {
                    val currentSize = IntegerSize(
                        size.width.toInt(),
                        size.height.toInt()
                    )
                    drawIntoCanvas {
                        val canvas = it.nativeCanvas
                        pathPaints.forEach { pathPaint ->
                            canvas.drawPath(
                                pathPaint.path
                                    .scaleToFitCanvas(
                                        currentSize = currentSize,
                                        oldSize = pathPaint.canvasSize
                                    )
                                    .asAndroidPath(),
                                Paint()
                                    .apply {
                                        isAntiAlias = true
                                        this.color = color.blend(pathPaint.drawColor, 0.5f)
                                        if (pathPaint.isErasing) {
                                            blendMode = BlendMode.Clear
                                        }
                                        style = PaintingStyle.Stroke
                                        strokeWidth = pathPaint.strokeWidth.toPx(
                                            currentSize
                                        )
                                        strokeCap = StrokeCap.Round
                                        strokeJoin = StrokeJoin.Round
                                    }
                                    .asFrameworkPaint()
                                    .apply {
                                        if (pathPaint.brushSoftness.value > 0f) {
                                            maskFilter =
                                                BlurMaskFilter(
                                                    pathPaint.brushSoftness.toPx(currentSize),
                                                    BlurMaskFilter.Blur.NORMAL
                                                )
                                        }
                                    }
                            )
                        }
                    }
                }
        )
    } else {
        val textMeasurer = rememberTextMeasurer()
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .then(visuals)
                .drawBehind {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "N",
                        topLeft = Offset(
                            size.width / 2,
                            size.height / 2
                        ),
                        style = TextStyle(color = color)
                    )
                }
        )
    }
}