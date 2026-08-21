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

package com.t8rin.imagetoolbox.core.data.image.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.domain.image.model.AnimationMergeItem

data class AnimationFrame(
    val bitmap: Bitmap,
    val durationMillis: Int
)

fun List<AnimationFrame>.transformForMerge(item: AnimationMergeItem): List<AnimationFrame> {
    val transformed = if (item.reverse) reversed() else this
    return if (item.boomerang && transformed.size > 1) {
        transformed + transformed.dropLast(1).reversed()
    } else transformed
}

fun Bitmap.placeOnAnimationCanvas(
    width: Int,
    height: Int,
    scaleToFit: Boolean
): Bitmap {
    if (this.width == width && this.height == height) return this
    val scale = if (scaleToFit) {
        minOf(width.toFloat() / this.width, height.toFloat() / this.height)
    } else 1f
    val targetWidth = this.width * scale
    val targetHeight = this.height * scale
    val left = (width - targetWidth) / 2f
    val top = (height - targetHeight) / 2f
    return createBitmap(width, height, Bitmap.Config.ARGB_8888).also { output ->
        Canvas(output).drawBitmap(
            this,
            null,
            RectF(left, top, left + targetWidth, top + targetHeight),
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        )
    }
}
