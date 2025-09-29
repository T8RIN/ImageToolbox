/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.utils

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Generates a QR code bitmap for the given [content].
 * The [widthPx] parameter defines the size of the QR code in pixels.
 * The [paddingPx] parameter defines the padding of the QR code in pixels.
 * Returns null if the QR code could not be generated.
 * This function is suspendable and should be called from a coroutine is thread-safe.
 */
suspend fun generateQrBitmap(
    content: String,
    widthPx: Int,
    heightPx: Int,
    paddingPx: Int,
    foregroundColor: Color = Color.Black,
    backgroundColor: Color = Color.White,
    format: BarcodeFormat = BarcodeFormat.QR_CODE
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