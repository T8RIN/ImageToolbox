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

package com.t8rin.imagetoolbox.core.ui.widget.other

import android.graphics.Bitmap
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shimmer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Creates a [Painter] that draws a QR code for the given [content].
 * The [size] parameter defines the size of the QR code in dp.
 * The [padding] parameter defines the padding of the QR code in dp.
 */
@Composable
fun rememberQrBitmapPainter(
    content: String,
    width: Dp = 150.dp,
    height: Dp = width,
    type: BarcodeType,
    padding: Dp = 0.5.dp,
    foregroundColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    backgroundColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    onLoading: () -> Unit = {},
    onSuccess: () -> Unit = {},
    onFailure: (Throwable) -> Unit = {}
): Painter {

    check(width >= 0.dp && height >= 0.dp) { "Size must be non negative" }
    check(padding >= 0.dp) { "Padding must be non negative" }

    val density = LocalDensity.current
    val widthPx = with(density) { width.roundToPx() }
    val heightPx = with(density) { height.roundToPx() }
    val paddingPx = with(density) { padding.roundToPx() }

    val bitmapState = remember(content) {
        mutableStateOf<Bitmap?>(null)
    }

    // Use dependency on 'content' to re-trigger the effect when content changes
    LaunchedEffect(content, type, widthPx, heightPx, paddingPx, foregroundColor, backgroundColor) {
        onLoading()

        if (content.isNotEmpty()) {
            delay(100)
            bitmapState.value = runSuspendCatching {
                generateQrBitmap(
                    content = content,
                    widthPx = widthPx,
                    heightPx = heightPx,
                    paddingPx = paddingPx,
                    foregroundColor = foregroundColor,
                    backgroundColor = backgroundColor,
                    format = type.zxingFormat
                )
            }.onFailure(onFailure).getOrNull()
        } else {
            bitmapState.value = null
        }

        if (bitmapState.value != null) onSuccess()
    }

    val bitmap = bitmapState.value ?: createDefaultBitmap(widthPx, heightPx)

    return remember(bitmap) {
        bitmap?.asImageBitmap()?.let {
            BitmapPainter(it)
        } ?: EmptyPainter(widthPx, heightPx)
    }
}

private class EmptyPainter(
    private val widthPx: Int,
    private val heightPx: Int
) : Painter() {
    override val intrinsicSize: Size
        get() = Size(widthPx.toFloat(), heightPx.toFloat())

    override fun DrawScope.onDraw() = Unit
}


/**
 * Generates a QR code bitmap for the given [content].
 * The [widthPx] parameter defines the size of the QR code in pixels.
 * The [paddingPx] parameter defines the padding of the QR code in pixels.
 * Returns null if the QR code could not be generated.
 * This function is suspendable and should be called from a coroutine is thread-safe.
 */
private suspend fun generateQrBitmap(
    content: String,
    widthPx: Int,
    heightPx: Int,
    paddingPx: Int,
    foregroundColor: Color,
    backgroundColor: Color,
    format: BarcodeFormat
): Bitmap = withContext(Dispatchers.IO) {
    val encodeHints = mutableMapOf<EncodeHintType, Any?>()
        .apply {
            this[EncodeHintType.CHARACTER_SET] = Charsets.UTF_8
            if (format == BarcodeFormat.QR_CODE) {
                this[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
            }
            this[EncodeHintType.MARGIN] = paddingPx
        }

    val bitmapMatrix =
        MultiFormatWriter().encode(content, format, widthPx, heightPx, encodeHints)

    val matrixWidth = bitmapMatrix.width
    val matrixHeight = bitmapMatrix.height

    val colors = IntArray(matrixWidth * matrixHeight) { index ->
        val x = index % matrixWidth
        val y = index / matrixWidth
        val shouldColorPixel = bitmapMatrix.get(x, y)
        if (shouldColorPixel) foregroundColor.toArgb() else backgroundColor.toArgb()
    }

    Bitmap.createBitmap(colors, matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)
}

/**
 * Creates a default bitmap with the given [widthPx], [heightPx].
 * The bitmap is transparent.
 * This is used as a fallback if the QR code could not be generated.
 * The bitmap is created on the UI thread.
 */
private fun createDefaultBitmap(
    widthPx: Int,
    heightPx: Int
): Bitmap? {
    return if (widthPx > 0 && heightPx > 0) {
        createBitmap(widthPx, heightPx).apply {
            eraseColor(Color.Transparent.toArgb())
        }
    } else null
}

@Composable
fun QrCode(
    content: String,
    modifier: Modifier,
    cornerRadius: Dp = 4.dp,
    heightRatio: Float = 2f,
    type: BarcodeType = BarcodeType.QR_CODE,
    enforceBlackAndWhite: Boolean = false,
    onFailure: (Throwable) -> Unit = {},
    onSuccess: () -> Unit = {},
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val width = min(this.maxWidth, maxHeight)
        val height = animateDpAsState(
            if (type.isSquare && type != BarcodeType.DATA_MATRIX) width else width / heightRatio
        ).value

        val backgroundColor = if (enforceBlackAndWhite) {
            Color.White
        } else {
            if (LocalSettingsState.current.isNightMode) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            }
        }

        val foregroundColor = if (enforceBlackAndWhite) {
            Color.Black
        } else {
            if (LocalSettingsState.current.isNightMode) {
                MaterialTheme.colorScheme.surfaceContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        }

        var isLoading by remember {
            mutableStateOf(true)
        }

        val painter = rememberQrBitmapPainter(
            content = content,
            width = width,
            height = height,
            padding = 0.5.dp,
            foregroundColor = foregroundColor,
            backgroundColor = backgroundColor,
            type = type,
            onLoading = {
                isLoading = true
            },
            onSuccess = {
                isLoading = false
                onSuccess()
            },
            onFailure = onFailure
        )

        val padding = if (type == BarcodeType.QR_CODE) 0.5.dp else 8.dp
        Picture(
            model = painter,
            modifier = Modifier
                .size(width, height)
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor)
                .padding(padding)
                .alpha(
                    animateFloatAsState(
                        targetValue = if (isLoading) 0f else 1f,
                        animationSpec = tween(500)
                    ).value
                ),
            contentDescription = null
        )

        Box(
            modifier = Modifier
                .size(width, height)
                .clip(RoundedCornerShape((cornerRadius - 1.dp).coerceAtLeast(0.dp)))
                .shimmer(isLoading)
        )
    }
}


enum class BarcodeType(
    internal val zxingFormat: BarcodeFormat,
    val isSquare: Boolean
) {
    QR_CODE(BarcodeFormat.QR_CODE, true),
    AZTEC(BarcodeFormat.AZTEC, true),
    CODABAR(BarcodeFormat.CODABAR, false),
    CODE_39(BarcodeFormat.CODE_39, false),
    CODE_93(BarcodeFormat.CODE_93, false),
    CODE_128(BarcodeFormat.CODE_128, false),
    DATA_MATRIX(BarcodeFormat.DATA_MATRIX, true),
    EAN_8(BarcodeFormat.EAN_8, false),
    EAN_13(BarcodeFormat.EAN_13, false),
    ITF(BarcodeFormat.ITF, false),
    PDF_417(BarcodeFormat.PDF_417, false),
    UPC_A(BarcodeFormat.UPC_A, false),
    UPC_E(BarcodeFormat.UPC_E, false)
}