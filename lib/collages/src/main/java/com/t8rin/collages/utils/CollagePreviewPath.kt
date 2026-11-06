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

import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import com.t8rin.collages.model.CollageLayout
import com.t8rin.collages.view.FrameImageView

internal const val CollagePreviewSize = 1000f

internal fun createCollagePreviewPath(layout: CollageLayout): Path {
    val paths = layout.photoItemList.map { item ->
        FrameImageView.shapePath(
            item,
            CollagePreviewSize * item.bound.width(),
            CollagePreviewSize * item.bound.height()
        ).apply {
            op(this, this, Path.Op.UNION)
            offset(CollagePreviewSize * item.bound.left, CollagePreviewSize * item.bound.top)
        }
    }
    val gap = minOf(48f, paths.minOfOrNull { it.maximumInset() * .7f } ?: 48f)
    val contentTransform = Matrix().apply {
        val scale = 1f - gap / CollagePreviewSize
        setScale(scale, scale)
        postTranslate(gap / 2, gap / 2)
    }
    paths.forEach { it.transform(contentTransform) }
    val pathBounds = paths.map { path -> RectF().also { path.computeBounds(it, true) } }
    val intersection = Path()
    val stacked = paths.indices.any { index ->
        (0 until index).any { other ->
            RectF.intersects(pathBounds[index], pathBounds[other]) &&
                    intersection.op(paths[index], paths[other], Path.Op.INTERSECT) &&
                    !intersection.isEmpty && intersection.maximumInset() > .01f
        }
    }
    val stroke = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = gap
        strokeJoin = Paint.Join.MITER
    }
    val preview = Path().apply {
        addRect(0f, 0f, CollagePreviewSize, CollagePreviewSize, Path.Direction.CW)
    }
    val holes = Path()
    val bounds = RectF()
    val border = Path()
    val measure = PathMeasure()
    paths.forEachIndexed { index, path ->
        val outline = if (stacked) Path(path) else null
        val item = layout.photoItemList[index]
        if (item.normalizedPath != null) {
            bounds.set(
                item.bound.left * CollagePreviewSize,
                item.bound.top * CollagePreviewSize,
                item.bound.right * CollagePreviewSize,
                item.bound.bottom * CollagePreviewSize
            )
            contentTransform.mapRect(bounds)
            path.transform(Matrix().apply {
                setScale(
                    1f - gap / bounds.width(),
                    1f - gap / bounds.height(),
                    bounds.centerX(),
                    bounds.centerY()
                )
            })
        } else if (path.isRect(bounds)) {
            bounds.inset(gap / 2, gap / 2)
            path.reset()
            path.addRect(bounds, Path.Direction.CW)
        } else {
            stroke.getFillPath(path, border)
            path.op(border, Path.Op.DIFFERENCE)
        }
        measure.setPath(path, false)
        if (outline != null) {
            preview.op(outline, Path.Op.UNION)
            preview.op(path, Path.Op.DIFFERENCE)
        } else if (measure.nextContour()) {
            // addPath does not preserve the even-odd holes of another path.
            preview.op(path, Path.Op.DIFFERENCE)
        } else {
            holes.addPath(path)
        }
    }
    preview.op(holes, Path.Op.DIFFERENCE)
    return preview
}