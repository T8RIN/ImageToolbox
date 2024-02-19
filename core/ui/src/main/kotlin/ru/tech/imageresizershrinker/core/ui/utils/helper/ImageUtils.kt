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
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.graphics.BitmapCompat
import androidx.core.text.isDigitsOnly
import androidx.exifinterface.media.ExifInterface
import com.t8rin.logger.makeLog
import ru.tech.imageresizershrinker.core.domain.image.Metadata


object ImageUtils {

    fun Drawable.toBitmap(): Bitmap {
        val drawable = this
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        val bitmap: Bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun ExifInterface.toMap(): Map<String, String> {
        val hashMap = HashMap<String, String>()
        Metadata.metaTags.forEach { tag ->
            getAttribute(tag)?.let { hashMap[tag] = it }
        }
        return hashMap
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

}