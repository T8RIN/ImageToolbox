/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.widget.other

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.modifier.shimmer

/**
 * Creates a [Painter] that draws a QR code for the given [content].
 * The [size] parameter defines the size of the QR code in dp.
 * The [padding] parameter defines the padding of the QR code in dp.
 */
@Composable
fun rememberQrBitmapPainter(
    content: String,
    size: Dp = 150.dp,
    padding: Dp = 0.5.dp,
    foregroundColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    backgroundColor: Color = MaterialTheme.colorScheme.tertiaryContainer
): Painter {

    check(size >= 0.dp) { "Size must be non negative" }
    check(padding >= 0.dp) { "Padding must be non negative" }

    val density = LocalDensity.current
    val sizePx = with(density) { size.roundToPx() }
    val paddingPx = with(density) { padding.roundToPx() }

    val bitmapState = remember(content) {
        mutableStateOf<Bitmap?>(null)
    }

    // Use dependency on 'content' to re-trigger the effect when content changes
    LaunchedEffect(content, sizePx, paddingPx, foregroundColor, backgroundColor) {
        if (content.isNotEmpty()) {
            delay(100)
            bitmapState.value = generateQrBitmap(
                content = content,
                sizePx = sizePx,
                paddingPx = paddingPx,
                foregroundColor = foregroundColor,
                backgroundColor = backgroundColor
            )
        } else bitmapState.value = null
    }

    val bitmap = bitmapState.value ?: createDefaultBitmap(sizePx)

    return remember(bitmap) {
        bitmap?.asImageBitmap()?.let {
            BitmapPainter(it)
        } ?: EmptyPainter(sizePx)
    }
}

private class EmptyPainter(
    private val sizePx: Int
) : Painter() {
    override val intrinsicSize: Size
        get() = Size(sizePx.toFloat(), sizePx.toFloat())

    override fun DrawScope.onDraw() = Unit
}


/**
 * Generates a QR code bitmap for the given [content].
 * The [sizePx] parameter defines the size of the QR code in pixels.
 * The [paddingPx] parameter defines the padding of the QR code in pixels.
 * Returns null if the QR code could not be generated.
 * This function is suspendable and should be called from a coroutine is thread-safe.
 */
private suspend fun generateQrBitmap(
    content: String,
    sizePx: Int,
    paddingPx: Int,
    foregroundColor: Color,
    backgroundColor: Color
): Bitmap? = withContext(Dispatchers.IO) {
    val qrCodeWriter = QRCodeWriter()

    val encodeHints = mutableMapOf<EncodeHintType, Any?>()
        .apply {
            this[EncodeHintType.CHARACTER_SET] = Charsets.UTF_8
            this[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
            this[EncodeHintType.MARGIN] = paddingPx
        }

    try {
        val bitmapMatrix = qrCodeWriter.encode(
            content, BarcodeFormat.QR_CODE,
            sizePx, sizePx, encodeHints
        )

        val matrixWidth = bitmapMatrix.width
        val matrixHeight = bitmapMatrix.height

        val colors = IntArray(matrixWidth * matrixHeight) { index ->
            val x = index % matrixWidth
            val y = index / matrixWidth
            val shouldColorPixel = bitmapMatrix.get(x, y)
            if (shouldColorPixel) foregroundColor.toArgb() else backgroundColor.toArgb()
        }

        Bitmap.createBitmap(colors, matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)
    } catch (ex: WriterException) {
        null
    }
}

/**
 * Creates a default bitmap with the given [sizePx].
 * The bitmap is transparent.
 * This is used as a fallback if the QR code could not be generated.
 * The bitmap is created on the UI thread.
 */
private fun createDefaultBitmap(sizePx: Int): Bitmap? {
    return if (sizePx > 0) {
        Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.Transparent.toArgb())
        }
    } else null
}

@Composable
fun QrCode(
    content: String,
    modifier: Modifier,
    shape: Shape = RoundedCornerShape(4.dp)
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val size = min(maxWidth, maxHeight)
        val backgroundColor = if (LocalSettingsState.current.isNightMode) {
            MaterialTheme.colorScheme.onSurface
        } else MaterialTheme.colorScheme.surfaceContainerHigh

        val foregroundColor = if (LocalSettingsState.current.isNightMode) {
            MaterialTheme.colorScheme.surfaceContainer
        } else MaterialTheme.colorScheme.onSurface

        val painter = rememberQrBitmapPainter(
            content = content,
            size = size,
            foregroundColor = foregroundColor,
            backgroundColor = backgroundColor
        )
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .shimmer(true)
        )
        Image(
            painter = painter,
            modifier = Modifier.clip(shape),
            contentDescription = null
        )
    }
}