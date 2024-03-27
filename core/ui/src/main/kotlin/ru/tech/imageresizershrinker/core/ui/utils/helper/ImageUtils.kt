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

package ru.tech.imageresizershrinker.core.ui.utils.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.graphics.BitmapCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.text.isDigitsOnly
import androidx.exifinterface.media.ExifInterface
import com.t8rin.logger.makeLog
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.image.model.Metadata


object ImageUtils {

    fun Drawable.toBitmap(): Bitmap = toBitmap(config = getSuitableConfig())

    fun ExifInterface.toMap(): Map<String, String> {
        val hashMap = HashMap<String, String>()
        Metadata.metaTags.forEach { tag ->
            getAttribute(tag)?.let { hashMap[tag] = it }
        }
        return hashMap
    }

    fun getSuitableConfig(
        image: Bitmap? = null
    ): Bitmap.Config = image?.config?.takeIf {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            it != Bitmap.Config.HARDWARE
        } else true
    } ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Bitmap.Config.RGBA_1010102
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Bitmap.Config.RGBA_F16
    } else {
        Bitmap.Config.ARGB_8888
    }

    fun Uri.fileSize(context: Context): Long? {
        runCatching {
            context.contentResolver
                .query(this, null, null, null, null, null)
                .use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (!cursor.isNull(sizeIndex)) {
                            return cursor.getLong(sizeIndex)
                        }
                    }
                }
        }
        return null
    }

    object Dimens {
        const val MAX_IMAGE_SIZE = 8388607 * 16
    }

    fun String.restrict(with: Int): String {
        with.makeLog()
        if (isEmpty()) return this

        return if ((this.toIntOrNull() ?: 0) >= with) with.toString()
        else if (this.isDigitsOnly() && (this.toIntOrNull() == null)) ""
        else this.trim()
            .filter {
                !listOf('-', '.', ',', ' ', "\n").contains(it)
            }
    }

    fun Bitmap.createScaledBitmap(
        width: Int,
        height: Int
    ): Bitmap {
        if (width == this.width && height == this.height) return this

        return if (width < this.width && height < this.height) {
            BitmapCompat.createScaledBitmap(
                this,
                width,
                height,
                null,
                true
            )
        } else {
            Bitmap.createScaledBitmap(
                this, width, height, true
            )
        }
    }

    fun ImageInfo.haveChanges(original: Bitmap?): Boolean {
        if (original == null) return false
        return quality.qualityValue != 100 || rotationDegrees != 0f || isFlipped || width != original.width || height != original.height
    }

    val Bitmap.aspectRatio: Float get() = width / height.toFloat()

    val ImageBitmap.aspectRatio: Float get() = width / height.toFloat()

    val Drawable.aspectRatio: Float get() = intrinsicWidth / intrinsicHeight.toFloat()

    val Bitmap.safeAspectRatio: Float
        get() = aspectRatio
            .coerceAtLeast(0.005f)
            .coerceAtMost(1000f)

    val ImageBitmap.safeAspectRatio: Float
        get() = aspectRatio
            .coerceAtLeast(0.005f)
            .coerceAtMost(1000f)

    val Drawable.safeAspectRatio: Float
        get() = aspectRatio
            .coerceAtLeast(0.005f)
            .coerceAtMost(1000f)

}