/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.core.ui.widget.other

import android.graphics.Matrix
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.graphics.path.PathIterator
import androidx.graphics.path.PathSegment
import androidx.graphics.path.iterator
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.options.Neighbors
import io.github.alexzhirkevich.qrose.options.QrShapeModifier
import java.nio.charset.StandardCharsets
import kotlin.math.abs
import kotlin.math.roundToInt

private const val OUTPUT_SIZE = 1024
private const val PREVIEW_MAX_SIZE = 400f
private const val PREVIEW_PADDING_FRACTION = 1f / 23f
private const val ARGB_ALPHA_SHIFT = 24
private const val RGB_MASK = 0xFFFFFF
private const val HEX_RADIX = 16
private const val RGB_HEX_LENGTH = 6
private const val MAX_ALPHA = 255
private const val FINDER_FRAME_SIZE = 7f
private const val FINDER_BALL_SIZE = 3f
private const val FINDER_BALL_OFFSET = 2f
private const val FINDER_PATTERN_LAST_INDEX = 7
private const val FINDER_PATTERN_AREA_SIZE = 8
private const val EYE_ROTATION_DEGREES = 90f
private const val SVG_PRECISION = 1000f
private const val PATH_ITERATOR_TOLERANCE = 0.01f
private const val CUBIC_END_POINT_INDEX = 3

fun QrCodeParams.renderAsSvg(
    content: String,
    type: BarcodeType,
    heightRatio: Float,
    cornerRadius: Int
): ByteArray {
    val hints = buildMap<EncodeHintType, Any> {
        put(EncodeHintType.CHARACTER_SET, "utf-8")
        put(EncodeHintType.MARGIN, 0)

        if (type == BarcodeType.QR_CODE) {
            errorCorrectionLevel.toZxing().let {
                put(EncodeHintType.ERROR_CORRECTION, it)
            }
            maskPattern.toZxing()?.let {
                put(EncodeHintType.QR_MASK_PATTERN, it)
            }
        }
    }
    val matrix = MultiFormatWriter().encode(
        content,
        type.zxingFormat,
        1,
        1,
        hints
    )
    val outputWidth = OUTPUT_SIZE
    val outputHeight = if (type.isSquare && type != BarcodeType.DATA_MATRIX) {
        outputWidth
    } else {
        (outputWidth / heightRatio.coerceAtLeast(1f)).toInt()
    }
    val horizontalPadding = matrix.width * PREVIEW_PADDING_FRACTION
    val verticalPadding = matrix.height * PREVIEW_PADDING_FRACTION
    val viewBoxWidth = matrix.width + horizontalPadding * 2
    val viewBoxHeight = matrix.height + verticalPadding * 2
    val svgCornerRadius = (viewBoxWidth * cornerRadius / PREVIEW_MAX_SIZE)
        .coerceIn(0f, minOf(viewBoxWidth, viewBoxHeight) / 2)
    val foreground = foregroundColor ?: Color.Black
    val background = backgroundColor ?: Color.White
    val path = if (type == BarcodeType.QR_CODE) {
        createQrPath(matrix)
    } else {
        createBarcodePath(matrix)
    }

    return buildString {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        append("<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"")
        append((-horizontalPadding).svgNumber())
        append(' ')
        append((-verticalPadding).svgNumber())
        append(' ')
        append(viewBoxWidth.svgNumber())
        append(' ')
        append(viewBoxHeight.svgNumber())
        append("\" width=\"")
        append(outputWidth)
        append("\" height=\"")
        append(outputHeight)
        append("\" preserveAspectRatio=\"none\" shape-rendering=\"geometricPrecision\">\n")
        if (svgCornerRadius > 0f) {
            append("<defs><clipPath id=\"qr-corners\"><rect x=\"")
            append((-horizontalPadding).svgNumber())
            append("\" y=\"")
            append((-verticalPadding).svgNumber())
            append("\" width=\"")
            append(viewBoxWidth.svgNumber())
            append("\" height=\"")
            append(viewBoxHeight.svgNumber())
            append("\" rx=\"")
            append(svgCornerRadius.svgNumber())
            append("\"/></clipPath></defs>\n<g clip-path=\"url(#qr-corners)\">\n")
        }
        append("<rect x=\"")
        append((-horizontalPadding).svgNumber())
        append("\" y=\"")
        append((-verticalPadding).svgNumber())
        append("\" width=\"")
        append(viewBoxWidth.svgNumber())
        append("\" height=\"")
        append(viewBoxHeight.svgNumber())
        append('"')
        appendColor(background)
        append("/>\n<path fill-rule=\"evenodd\"")
        appendColor(foreground)
        append(" d=\"")
        appendPath(path)
        append("\"/>\n")
        if (svgCornerRadius > 0f) append("</g>\n")
        append("</svg>")
    }.toByteArray(StandardCharsets.UTF_8)
}

private fun QrCodeParams.createQrPath(matrix: BitMatrix): Path = Path().apply {
    fillType = PathFillType.EvenOdd

    val pixelShape = pixelShape.toLib(Density(1f))
    repeat(matrix.width) { x ->
        repeat(matrix.height) { y ->
            if (matrix[x, y] && !isInsideFinderPattern(x, y, matrix.width)) {
                addPath(
                    path = pixelShape.createPath(
                        size = 1f,
                        neighbors = matrix.neighbors(x, y)
                    ),
                    offset = Offset(x.toFloat(), y.toFloat())
                )
            }
        }
    }

    val finderOrigins = listOf(
        Offset.Zero,
        Offset(0f, matrix.height - FINDER_FRAME_SIZE),
        Offset(matrix.width - FINDER_FRAME_SIZE, 0f)
    )
    finderOrigins.forEachIndexed { index, origin ->
        val neighbors = finderNeighbors(index)
        addPath(
            path = frameShape.toLib()
                .createPath(FINDER_FRAME_SIZE, neighbors)
                .rotateEye(index, FINDER_FRAME_SIZE),
            offset = origin
        )
        addPath(
            path = ballShape.toLib(Density(1f))
                .createPath(FINDER_BALL_SIZE, neighbors)
                .rotateEye(index, FINDER_BALL_SIZE),
            offset = origin + Offset(FINDER_BALL_OFFSET, FINDER_BALL_OFFSET)
        )
    }
}

private fun createBarcodePath(matrix: BitMatrix): Path = Path().apply {
    fillType = PathFillType.EvenOdd
    repeat(matrix.height) { y ->
        var x = 0
        while (x < matrix.width) {
            if (!matrix[x, y]) {
                x++
                continue
            }

            val start = x
            while (x < matrix.width && matrix[x, y]) x++
            addRect(Rect(start.toFloat(), y.toFloat(), x.toFloat(), y + 1f))
        }
    }
}

private fun QrShapeModifier.createPath(
    size: Float,
    neighbors: Neighbors
): Path = Path().apply {
    fillType = PathFillType.EvenOdd
    with(this@createPath) {
        path(size, neighbors)
    }
}

private fun Path.rotateEye(
    index: Int,
    size: Float
): Path = apply {
    val angle = when (index) {
        1 -> -EYE_ROTATION_DEGREES
        2 -> EYE_ROTATION_DEGREES
        else -> return@apply
    }
    val center = size / 2
    asAndroidPath().transform(
        Matrix().apply {
            setRotate(angle, center, center)
        }
    )
}

private fun isInsideFinderPattern(
    x: Int,
    y: Int,
    matrixSize: Int
): Boolean = (x in 0..FINDER_PATTERN_LAST_INDEX && y in 0..FINDER_PATTERN_LAST_INDEX) ||
        (x in 0..FINDER_PATTERN_LAST_INDEX && y in matrixSize - FINDER_PATTERN_AREA_SIZE until matrixSize) ||
        (x in matrixSize - FINDER_PATTERN_AREA_SIZE until matrixSize && y in 0..FINDER_PATTERN_LAST_INDEX)

private fun BitMatrix.neighbors(x: Int, y: Int): Neighbors {
    fun matches(otherX: Int, otherY: Int): Boolean =
        otherX in 0 until width &&
                otherY in 0 until height &&
                this[otherX, otherY] == this[x, y]

    return Neighbors(
        topLeft = matches(x - 1, y - 1),
        topRight = matches(x + 1, y - 1),
        left = matches(x - 1, y),
        top = matches(x, y - 1),
        right = matches(x + 1, y),
        bottomLeft = matches(x - 1, y + 1),
        bottom = matches(x, y + 1),
        bottomRight = matches(x + 1, y + 1)
    )
}

private fun finderNeighbors(index: Int): Neighbors = when (index) {
    0 -> Neighbors(bottom = true, right = true)
    1 -> Neighbors(left = true, bottomLeft = true)
    else -> Neighbors(top = true, topRight = true)
}

private fun StringBuilder.appendPath(path: Path) {
    path.asAndroidPath().iterator(
        conicEvaluation = PathIterator.ConicEvaluation.AsQuadratics,
        tolerance = PATH_ITERATOR_TOLERANCE
    ).forEach { segment ->
        val points = segment.points
        when (segment.type) {
            PathSegment.Type.Move -> {
                append('M')
                appendPoint(points[0].x, points[0].y)
            }

            PathSegment.Type.Line -> {
                append('L')
                appendPoint(points[1].x, points[1].y)
            }

            PathSegment.Type.Quadratic,
            PathSegment.Type.Conic -> {
                append('Q')
                appendPoint(points[1].x, points[1].y)
                append(' ')
                appendPoint(points[2].x, points[2].y)
            }

            PathSegment.Type.Cubic -> {
                append('C')
                appendPoint(points[1].x, points[1].y)
                append(' ')
                appendPoint(points[2].x, points[2].y)
                append(' ')
                appendPoint(
                    points[CUBIC_END_POINT_INDEX].x,
                    points[CUBIC_END_POINT_INDEX].y
                )
            }

            PathSegment.Type.Close -> append('Z')
            PathSegment.Type.Done -> Unit
        }
    }
}

private fun StringBuilder.appendPoint(x: Float, y: Float) {
    append(x.svgNumber())
    append(' ')
    append(y.svgNumber())
}

private fun StringBuilder.appendColor(color: Color) {
    val argb = color.toArgb()
    val alpha = argb ushr ARGB_ALPHA_SHIFT
    append(" fill=\"#")
    append((argb and RGB_MASK).toString(HEX_RADIX).padStart(RGB_HEX_LENGTH, '0'))
    append('"')
    if (alpha < MAX_ALPHA) {
        append(" fill-opacity=\"")
        append((alpha / MAX_ALPHA.toFloat()).svgNumber())
        append('"')
    }
}

private fun Float.svgNumber(): String {
    val rounded = (this * SVG_PRECISION).roundToInt() / SVG_PRECISION
    return if (abs(rounded - rounded.roundToInt()) < 1f / SVG_PRECISION) {
        rounded.roundToInt().toString()
    } else {
        rounded.toString()
    }
}

private fun QrCodeParams.ErrorCorrectionLevel.toZxing(): ErrorCorrectionLevel = when (this) {
    QrCodeParams.ErrorCorrectionLevel.Auto -> ErrorCorrectionLevel.L
    QrCodeParams.ErrorCorrectionLevel.L -> ErrorCorrectionLevel.L
    QrCodeParams.ErrorCorrectionLevel.M -> ErrorCorrectionLevel.M
    QrCodeParams.ErrorCorrectionLevel.Q -> ErrorCorrectionLevel.Q
    QrCodeParams.ErrorCorrectionLevel.H -> ErrorCorrectionLevel.H
}

private fun QrCodeParams.MaskPattern.toZxing(): Int? = when (this) {
    QrCodeParams.MaskPattern.Auto -> null
    else -> ordinal - 1
}
