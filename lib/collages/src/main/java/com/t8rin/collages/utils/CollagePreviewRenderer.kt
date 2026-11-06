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

package com.t8rin.collages.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withTranslation
import com.t8rin.collages.CollageType
import com.t8rin.collages.view.FrameImageView

internal fun createCollagePreview(type: CollageType, size: Int = 525): Bitmap {
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.WHITE)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = size / 21f
        strokeJoin = Paint.Join.ROUND
    }
    val margin = stroke.strokeWidth / 2
    val contentSize = size - margin * 2
    type.layout?.photoItemList.orEmpty().forEach { item ->
        val path = FrameImageView.shapePath(
            item, contentSize * item.bound.width(),
            contentSize * item.bound.height()
        )
        stroke.strokeWidth =
            if (type.layoutId?.startsWith("layout_") == true || type.imageCount > 20) {
                minOf(size / 21f, path.maximumInset())
            } else size / 21f
        val border = Path()
        stroke.getFillPath(path, border)
        path.op(border, Path.Op.DIFFERENCE)
        canvas.withTranslation(
            margin + contentSize * item.bound.left,
            margin + contentSize * item.bound.top
        ) {
            drawPath(path, paint)
        }
    }
    return bitmap
}