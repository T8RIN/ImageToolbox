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
import androidx.compose.runtime.derivedStateOf
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
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shimmer
import com.t8rin.imagetoolbox.core.utils.generateQrBitmap
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrColors
import io.github.alexzhirkevich.qrose.options.QrErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

/**
 * Creates a [Painter] that draws a QR code for the given [content].
 * The [size] parameter defines the size of the QR code in dp.
 * The [padding] parameter defines the padding of the QR code in dp.
 */
@Composable
private fun rememberBarcodePainter(
    content: String,
    params: BarcodeParams,
    onLoading: () -> Unit = {},
    onSuccess: () -> Unit = {},
    onFailure: (Throwable) -> Unit = {}
): Painter = when (params) {
    is BarcodeParams.Barcode -> {
        val width = params.width
        val height = params.height
        val foregroundColor = params.foregroundColor
        val backgroundColor = params.backgroundColor
        val type = params.type

        val density = LocalDensity.current
        val widthPx = with(density) { width.roundToPx() }
        val heightPx = with(density) { height.roundToPx() }

        val bitmapState = remember(content) {
            mutableStateOf<Bitmap?>(null)
        }

        // Use dependency on 'content' to re-trigger the effect when content changes
        LaunchedEffect(
            content,
            type,
            widthPx,
            heightPx,
            foregroundColor,
            backgroundColor
        ) {
            onLoading()

            if (content.isNotEmpty()) {
                bitmapState.value = runSuspendCatching {
                    generateQrBitmap(
                        content = content,
                        widthPx = widthPx,
                        heightPx = heightPx,
                        paddingPx = 0,
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

        remember(bitmap) {
            bitmap?.asImageBitmap()?.let {
                BitmapPainter(it)
            } ?: EmptyPainter(widthPx, heightPx)
        }
    }

    is BarcodeParams.Qr -> {
        LaunchedEffect(Unit) { onSuccess() }

        //TODO: Fork to fix the crash with long text, and add more customization
        rememberQrCodePainter(
            data = content,
            colors = QrColors(
                dark = params.foregroundColor,
                light = params.backgroundColor
            ),
            errorCorrectionLevel = params.errorCorrectionLevel
        )
    }
}

private sealed interface BarcodeParams {
    data class Qr(
        val foregroundColor: QrBrush,
        val backgroundColor: QrBrush,
        val errorCorrectionLevel: QrErrorCorrectionLevel,
    ) : BarcodeParams

    data class Barcode(
        val width: Dp = 150.dp,
        val height: Dp = width,
        val type: BarcodeType,
        val foregroundColor: Color,
        val backgroundColor: Color,
    ) : BarcodeParams {
        init {
            check(width >= 0.dp && height >= 0.dp) { "Size must be non negative" }
        }
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

data class QrCodeParams(
    val foregroundColor: Color? = null,
    val backgroundColor: Color? = null,
    val errorCorrectionLevel: QrErrorCorrectionLevel = QrErrorCorrectionLevel.Auto
)

@Composable
fun QrCode(
    content: String,
    modifier: Modifier,
    cornerRadius: Dp = 4.dp,
    heightRatio: Float = 2f,
    qrParams: QrCodeParams,
    type: BarcodeType = BarcodeType.QR_CODE,
    onFailure: (Throwable) -> Unit = {},
    onSuccess: () -> Unit = {},
) {
    val settingsState = LocalSettingsState.current

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val width = min(this.maxWidth, maxHeight)
        val height = animateDpAsState(
            if (type.isSquare && type != BarcodeType.DATA_MATRIX) width else width / heightRatio
        ).value

        val backgroundColor = qrParams.backgroundColor ?: if (settingsState.isNightMode) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        }

        val foregroundColor = qrParams.foregroundColor ?: if (settingsState.isNightMode) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        }

        var isLoading by remember {
            mutableStateOf(true)
        }

        val params by remember(
            width,
            height,
            type,
            foregroundColor,
            backgroundColor,
            qrParams
        ) {
            derivedStateOf {
                when (type) {
                    BarcodeType.QR_CODE -> {
                        BarcodeParams.Qr(
                            foregroundColor = QrBrush.solid(foregroundColor),
                            backgroundColor = QrBrush.solid(backgroundColor),
                            errorCorrectionLevel = qrParams.errorCorrectionLevel
                        )
                    }

                    else -> {
                        BarcodeParams.Barcode(
                            width = width,
                            height = height,
                            foregroundColor = foregroundColor,
                            backgroundColor = backgroundColor,
                            type = type,
                        )
                    }
                }
            }
        }

        val painter = rememberBarcodePainter(
            content = content,
            params = params,
            onLoading = {
                isLoading = true
            },
            onSuccess = {
                isLoading = false
                onSuccess()
            },
            onFailure = onFailure
        )

        val padding = 12.dp

        Picture(
            model = painter,
            modifier = Modifier
                .size(
                    width = width,
                    height = height
                )
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
                .size(
                    width = width,
                    height = height
                )
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