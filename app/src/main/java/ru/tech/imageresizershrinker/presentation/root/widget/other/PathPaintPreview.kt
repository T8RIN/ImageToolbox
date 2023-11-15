package ru.tech.imageresizershrinker.presentation.root.widget.other

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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
    val first = pathPaints.firstOrNull()
    if (first != null) {
        Box(
            modifier = modifier
                .aspectRatio(
                    first.canvasSize.run {
                        width.toFloat() / height
                    }
                )
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
                .drawBehind {
                    val currentSize = IntegerSize(
                        size.width.toInt(),
                        size.height.toInt()
                    )
                    pathPaints.forEach { pathPaint ->
                        drawPath(
                            path = pathPaint.path.scaleToFitCanvas(
                                currentSize = currentSize,
                                oldSize = pathPaint.canvasSize
                            ),
                            color = color.blend(pathPaint.drawColor, 0.5f),
                            style = Stroke(
                                width = pathPaint.strokeWidth.toPx(
                                    currentSize
                                ),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            ),
                            blendMode = if (pathPaint.isErasing) {
                                BlendMode.Clear
                            } else DrawScope.DefaultBlendMode
                        )
                    }
                }
        )
    } else {
        val textMeasurer = rememberTextMeasurer()
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant(),
                    shape = RoundedCornerShape(4.dp)
                )
                .clip(RoundedCornerShape(4.dp))
                .transparencyChecker(
                    checkerHeight = 1.dp,
                    checkerWidth = 1.dp
                )
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