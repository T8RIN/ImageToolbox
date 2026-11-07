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

package com.t8rin.collages.layout.catalog

import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import androidx.core.graphics.PathParser
import com.t8rin.collages.handles.ParamsManagerBuilder
import com.t8rin.collages.handles.withRectangularHandles
import com.t8rin.collages.model.CollageLayout
import com.t8rin.collages.model.CollageMask
import com.t8rin.collages.model.CollagePoint
import com.t8rin.collages.model.PhotoItem
import org.json.JSONArray
import org.json.JSONObject

internal fun CollageLayoutDefinition.parseCollageLayout(
    masks: List<CollageMask>,
    shapes: List<FloatArray>
): CollageLayout {
    val items = slots.mapIndexed { index, slot -> slot.photoItem(index, masks, shapes, inset) }
    val layout = CollageLayout(title = id, photoItemList = items)
    if (parameters == null || parameters.isEmpty()) return layout.withRectangularHandles()

    val builder = ParamsManagerBuilder()
    parameters.forEach(builder::param)
    items.forEachIndexed { index, item ->
        val slot = slots[index]
        val edges = checkNotNull(slot.edges)
        val original = RectF(item.bound)
        builder.addBoxedItem(
            photoItem = item,
            xParams = slot.xHandles,
            yParams = slot.yHandles,
            boxParams = { values ->
                fun edge(index: Int, fallback: Float): Float =
                    edges[index].takeIf { it >= 0 }?.let { values[it] } ?: fallback
                RectF(
                    edge(0, original.left), edge(1, original.top),
                    edge(2, original.right), edge(3, original.bottom)
                )
            },
            action = { photo, _, bounds ->
                if (photo.shape != null) photo.shape = listOf(
                    CollagePoint(bounds.left, bounds.top),
                    CollagePoint(bounds.right, bounds.top),
                    CollagePoint(bounds.right, bounds.bottom),
                    CollagePoint(bounds.left, bounds.bottom)
                )
            }
        )
    }
    val (manager, photoItems) = builder.build()
    return layout.copy(paramsManager = manager, photoItemList = photoItems)
}

private fun CollageLayoutDefinition.Slot.photoItem(
    index: Int,
    masks: List<CollageMask>,
    shapes: List<FloatArray>,
    inset: Float
): PhotoItem {
    val shape = shape.takeIf { it >= 0 }?.let { shapes[it] }?.let { points ->
        List(points.size / 2) { CollagePoint(points[it * 2], points[it * 2 + 1]) }
    }
    val mask = mask.takeIf { it >= 0 }?.let { masks[it] }
    return PhotoItem(
        index = index,
        shape = shape,
        mask = mask,
        maximumMaskInset = inset,
        bound = RectF(bounds[0], bounds[1], bounds[2], bounds[3])
    ).apply {
        properties?.let { properties ->
            this.index = properties.optInt("index", index)
            pointList = properties.optJSONArray("pointList")?.points() ?: arrayListOf()
            path = properties.optJSONObject("path")?.path()
            pathRatioBound = properties.optJSONArray("pathRatioBound")?.rect()
            pathInCenterHorizontal = properties.optBoolean("pathInCenterHorizontal")
            pathInCenterVertical = properties.optBoolean("pathInCenterVertical")
            pathAlignParentRight = properties.optBoolean("pathAlignParentRight")
            pathScaleRatio = properties.optDouble("pathScaleRatio", 1.0).toFloat()
            fitBound = properties.optBoolean("fitBound")
            hasBackground = properties.optBoolean("hasBackground")
            shrinkMethod = properties.optInt("shrinkMethod")
            cornerMethod = properties.optInt("cornerMethod")
            disableShrink = properties.optBoolean("disableShrink")
            shrinkMap = properties.optJSONArray("shrinkMap")?.let { map ->
                HashMap<PointF, PointF>().apply {
                    repeat(map.length()) {
                        val entry = map.getJSONArray(it)
                        put(entry.getJSONArray(0).point(), entry.getJSONArray(1).point())
                    }
                }
            }
            clearAreaPoints = properties.optJSONArray("clearAreaPoints")?.points()
            clearPath = properties.optJSONObject("clearPath")?.path()
            clearPathRatioBound = properties.optJSONArray("clearPathRatioBound")?.rect()
            clearPathInCenterHorizontal = properties.optBoolean("clearPathInCenterHorizontal")
            clearPathInCenterVertical = properties.optBoolean("clearPathInCenterVertical")
            clearPathScaleRatio = properties.optDouble("clearPathScaleRatio", 1.0).toFloat()
            centerInClearBound = properties.optBoolean("centerInClearBound")
        }
    }
}

private fun JSONObject.path(): Path {
    val circle = optJSONArray("circle")
    return if (circle != null) Path().apply {
        addCircle(
            circle.getDouble(0).toFloat(), circle.getDouble(1).toFloat(),
            circle.getDouble(2).toFloat(), Path.Direction.CCW
        )
    } else checkNotNull(PathParser.createPathFromPathData(getString("data"))).apply {
        if (optBoolean("evenOdd")) fillType = Path.FillType.EVEN_ODD
    }
}

private fun JSONArray.rect() = RectF(
    getDouble(0).toFloat(), getDouble(1).toFloat(),
    getDouble(2).toFloat(), getDouble(3).toFloat()
)

private fun JSONArray.point() = PointF(getDouble(0).toFloat(), getDouble(1).toFloat())

private fun JSONArray.points() = ArrayList(List(length()) { getJSONArray(it).point() })