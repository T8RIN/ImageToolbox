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

package com.t8rin.collages.components.preview

import android.graphics.Path
import android.graphics.Region
import com.t8rin.collages.model.CollageLayout
import com.t8rin.collages.view.FrameImageView
import kotlin.math.sqrt

internal const val CollagePreviewSize = 1000f

internal data class CollagePreviewShape(val path: Path, val outline: Path?)

internal fun createCollagePreviewShapes(layout: CollageLayout): List<CollagePreviewShape> {
    val gap = minOf(
        48f,
        CollagePreviewSize / (4 * sqrt(layout.photoItemList.size.coerceAtLeast(1).toFloat()))
    )
    val contentSize = CollagePreviewSize - gap * 2
    val clip = Region(0, 0, CollagePreviewSize.toInt(), CollagePreviewSize.toInt())
    val occupied = Region()
    return layout.photoItemList.map { item ->
        fun shape(spacing: Float) = FrameImageView.shapePath(
            item = item,
            width = contentSize * item.bound.width(),
            height = contentSize * item.bound.height(),
            spacing = spacing
        ).apply {
            offset(gap + contentSize * item.bound.left, gap + contentSize * item.bound.top)
        }

        val outline = shape(0f)
        val region = Region().apply { setPath(outline, clip) }
        val overlaps = Region(region).op(occupied, Region.Op.INTERSECT)
        occupied.op(region, Region.Op.UNION)
        CollagePreviewShape(
            path = shape(gap / 2),
            outline = outline.takeIf { overlaps }
        )
    }
}