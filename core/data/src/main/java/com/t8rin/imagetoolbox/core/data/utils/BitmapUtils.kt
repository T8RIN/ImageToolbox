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

package com.t8rin.imagetoolbox.core.data.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.core.graphics.drawable.toBitmap
import coil3.Image
import java.io.ByteArrayOutputStream

private val possibleConfigs = mutableListOf<Bitmap.Config>().apply {
    if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        add(Bitmap.Config.RGBA_1010102)
    }
    if (SDK_INT >= Build.VERSION_CODES.O) {
        add(Bitmap.Config.RGBA_F16)
    }
    add(Bitmap.Config.ARGB_8888)
    add(Bitmap.Config.RGB_565)
}

fun getSuitableConfig(
    image: Bitmap? = null
): Bitmap.Config = image?.config?.takeIf {
    it in possibleConfigs
} ?: Bitmap.Config.ARGB_8888

fun Bitmap.toSoftware(): Bitmap = copy(getSuitableConfig(this), false) ?: this

val Bitmap.aspectRatio: Float get() = width / height.toFloat()

val Image.aspectRatio: Float get() = width / height.toFloat()

val Drawable.aspectRatio: Float get() = intrinsicWidth / intrinsicHeight.toFloat()

val Bitmap.safeAspectRatio: Float
    get() = aspectRatio
        .coerceAtLeast(0.005f)
        .coerceAtMost(1000f)

val Bitmap.safeConfig: Bitmap.Config
    get() = config ?: getSuitableConfig(this)

val Drawable.safeAspectRatio: Float
    get() = aspectRatio
        .coerceAtLeast(0.005f)
        .coerceAtMost(1000f)

fun Drawable.toBitmap(): Bitmap = toBitmap(config = getSuitableConfig())

fun Bitmap.compress(
    format: Bitmap.CompressFormat,
    quality: Int
): ByteArray = ByteArrayOutputStream().apply {
    use { out ->
        compress(format, quality, out)
    }
}.toByteArray()

val Bitmap.Config.isHardware: Boolean
    get() = SDK_INT >= 26 && this == Bitmap.Config.HARDWARE

fun Bitmap.Config?.toSoftware(): Bitmap.Config {
    return if (this == null || isHardware) Bitmap.Config.ARGB_8888 else this
}