package io.github.alexzhirkevich.qrose.oned

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import io.github.alexzhirkevich.qrose.CachedPainter


typealias BarcodePathBuilder = (size: Size, code: BooleanArray) -> Path

/**
 * Remember barcode painter
 *
 * @param data code contents. Invalid contents can throw an exception
 * @param type barcode type
 * @param brush code brush
 * @param onError called when input content is invalid
 * @param builder build code path using painter size and encoded boolean list
 *
 * @see BarcodePainter
 * */
@Composable
@Stable
fun rememberBarcodePainter(
    data: String,
    type: BarcodeType,
    brush: Brush = SolidColor(Color.Black),
    onError: (Throwable) -> Painter = { throw it },
    builder: BarcodePathBuilder = ::defaultBarcodeBuilder
): Painter {

    val updatedBuilder by rememberUpdatedState(builder)

    return remember(data, brush) {
        runCatching {
            BarcodePainter(
                data = data,
                type = type,
                brush = brush,
                builder = { size, code ->
                    updatedBuilder(size, code)
                },
            )
        }.getOrElse(onError)
    }
}


/**
 * Create barcode painter
 *
 * @param code encoded barcode data
 * @param brush code brush
 * @param builder build code path using painter size and encoded boolean list
 *
 * @see rememberBarcodePainter
 * */
@Immutable
class BarcodePainter(
    private val code: BooleanArray,
    private val brush: Brush = SolidColor(Color.Black),
    private val builder: BarcodePathBuilder = ::defaultBarcodeBuilder
) : CachedPainter() {

    /**
     * Create barcode painter
     *
     * @param data code contents. Invalid contents can throw an exception
     * @param type barcode type
     * @param brush code brush
     * @param builder build code path using painter size and encoded boolean list
     *
     * @see rememberBarcodePainter
     * */
    constructor(
        data: String,
        type: BarcodeType,
        brush: Brush = SolidColor(Color.Black),
        builder: BarcodePathBuilder = ::defaultBarcodeBuilder
    ) : this(
        code = type.encoder.encode(data),
        brush = brush,
        builder = builder
    )

    override fun DrawScope.onCache() {
        drawPath(
            path = builder(size, code),
            brush = brush
        )
    }

    override val intrinsicSize: Size = Size(
        width = 6f * code.size,
        height = 120f
    )
}

@PublishedApi
@Stable
internal fun defaultBarcodeBuilder(size: Size, data: BooleanArray): Path = Path().apply {

    val width = size.width / data.size

    data.forEachIndexed { i, b ->
        if (b) {
            addRect(
                Rect(
                    left = i * width,
                    top = 0f,
                    right = (i + 1) * width,
                    bottom = size.height
                )
            )
        }
    }
}